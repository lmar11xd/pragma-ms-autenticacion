package co.com.bancolombia.api.config;

import co.com.bancolombia.api.Handler;
import co.com.bancolombia.api.RouterRest;
import co.com.bancolombia.api.security.JwtProvider;
import co.com.bancolombia.api.util.ReactiveValidator;
import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.usecase.applicant.ApplicantUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@TestPropertySource(properties = {
        "cors.allowed-origins=http://localhost:3000,http://example.com",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration"
})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReactiveValidator validator;

    @MockitoBean
    private ApplicantUseCase applicantUseCase;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private TransactionalOperator tx;

    @BeforeEach
    void setUp() {
        Mockito.when(validator.validate(Mockito.any()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        Mockito.when(applicantUseCase.register(Mockito.any()))
                .thenReturn(Mono.just(Applicant.builder().id("1").build()));

        Mockito.when(tx.transactional(Mockito.<Mono<Object>>any()))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}