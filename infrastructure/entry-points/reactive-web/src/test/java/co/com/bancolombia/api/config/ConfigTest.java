package co.com.bancolombia.api.config;

import co.com.bancolombia.api.Handler;
import co.com.bancolombia.api.RouterRest;
import co.com.bancolombia.api.util.ReactiveValidator;
import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.usecase.applicant.ApplicantUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    private ReactiveValidator validator;
    private ApplicantUseCase applicantUseCase;
    private TransactionalOperator tx;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        validator = Mockito.mock(ReactiveValidator.class);
        applicantUseCase = Mockito.mock(ApplicantUseCase.class);
        tx = Mockito.mock(TransactionalOperator.class);
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        // Mockear comportamiento mínimo para que no falle el flujo
        Mockito.when(validator.validate(Mockito.any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mockito.when(applicantUseCase.register(Mockito.any()))
                .thenReturn(Mono.just(Applicant.builder().id("1").build())); // fake Domain entity

        Mockito.when(tx.transactional((Flux<Object>) Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

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