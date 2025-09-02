package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoginRequest;
import co.com.bancolombia.api.dto.LoginResponse;
import co.com.bancolombia.api.dto.RegisterApplicantRequest;
import co.com.bancolombia.api.mapper.ApplicantMapper;
import co.com.bancolombia.api.security.JwtProvider;
import co.com.bancolombia.api.util.ReactiveValidator;
import co.com.bancolombia.model.user.Role;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.applicant.ApplicantUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import co.com.bancolombia.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final ReactiveValidator validator;
    private final ApplicantUseCase applicantUseCase;
    private final UserUseCase userUseCase;
    private final JwtProvider jwtProvider;
    private final TransactionalOperator tx;

    public Mono<ServerResponse> register(ServerRequest serverRequest) {
        log.info("Solicitud POST={}", serverRequest.path());

        return serverRequest.bodyToMono(RegisterApplicantRequest.class)
                .flatMap(validator::validate)
                .flatMap(dto -> applicantUseCase
                        .register(ApplicantMapper.toDomain(dto))
                        .flatMap(saved -> {
                            User user = User
                                    .builder()
                                    .id(null)
                                    .applicantId(saved.getId())
                                    .email(saved.getEmail())
                                    .passwordHash(dto.password())
                                    .roles(Set.of(Role.valueOf(dto.role().name())))
                                    .enabled(true)
                                    .build();
                            return userUseCase.save(user).thenReturn(ApplicantMapper.toDto(saved));
                        })
                )
                .as(tx::transactional)
                .flatMap(response -> {
                            log.info("Solicitante creado con ID {}", response.id());
                            return ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(response);
                        }
                )
                .doOnError(error ->
                        log.error("Fallo en creacion del solicitante: {}", error.getMessage(), error)
                );
    }

    public Mono<ServerResponse> findByDocumentNumber(ServerRequest serverRequest) {
        log.info("Solicitud GET={}", serverRequest.path());

        String documentNumber = serverRequest.pathVariable("documentNumber");

        return applicantUseCase.findByDocumentNumber(documentNumber)
                .flatMap(applicant -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApplicantMapper.toDto(applicant))
                );
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequest.class)
                .flatMap(validator::validate)
                .flatMap(dto -> userUseCase.authenticate(new Email(dto.email()), dto.password()))
                .map(u -> {
                    String token = jwtProvider.generateToken(
                            u.getEmail().getValue(),
                            u.getRoles().stream().map(Enum::name).toList()
                    );

                    return new LoginResponse(token, 3600);
                })
                .flatMap(response -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                );
    }
}
