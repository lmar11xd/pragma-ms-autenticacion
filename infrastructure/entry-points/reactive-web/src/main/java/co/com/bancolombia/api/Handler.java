package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.RegisterApplicantRequest;
import co.com.bancolombia.api.mapper.ApplicantMapper;
import co.com.bancolombia.api.util.ReactiveValidator;
import co.com.bancolombia.usecase.applicant.ApplicantUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final ReactiveValidator validator;
    private final ApplicantUseCase applicantUseCase;
    private final TransactionalOperator tx;

    public Mono<ServerResponse> register(ServerRequest serverRequest) {
        log.info("Solicitud POST={}", serverRequest.path());

        return serverRequest.bodyToMono(RegisterApplicantRequest.class)
                .flatMap(validator::validate)
                .flatMap(dto -> applicantUseCase.register(ApplicantMapper.toDomain(dto)))
                .as(tx::transactional)
                .map(saved -> {
                    log.info("Solicitante creado con ID {}", saved.getId());
                    return ApplicantMapper.toDto(saved);
                })
                .flatMap(response -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                )
                .doOnError(error -> log.error("Fallo en creacion del solicitante: {}", error.getMessage(), error));
    }
}
