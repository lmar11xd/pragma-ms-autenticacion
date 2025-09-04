package co.com.bancolombia.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

class RouterRestTest {

    private WebTestClient webTestClient;
    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = Mockito.mock(Handler.class);

        // Mock respuestas de los handlers
        Mockito.when(handler.register(Mockito.any()))
                .thenReturn(ServerResponse.created(null).build());
        Mockito.when(handler.findByDocumentNumber(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());
        Mockito.when(handler.login(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());

        RouterRest routerRest = new RouterRest();
        webTestClient = WebTestClient.bindToRouterFunction(routerRest.routerFunction(handler)).build();
    }

    @Test
    void shouldRouteToRegisterHandler() {
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"email\":\"test@test.com\",\"password\":\"1234\"}")
                .exchange()
                .expectStatus().isCreated();

        Mockito.verify(handler).register(Mockito.any());
    }

    @Test
    void shouldRouteToFindByDocumentHandler() {
        webTestClient.get()
                .uri("/api/v1/usuarios/document/12345678")
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(handler).findByDocumentNumber(Mockito.any());
    }

    @Test
    void shouldRouteToLoginHandler() {
        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"email\":\"test@test.com\",\"password\":\"1234\"}")
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(handler).login(Mockito.any());
    }
}
