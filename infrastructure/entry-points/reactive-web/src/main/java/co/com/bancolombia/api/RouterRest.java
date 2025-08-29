package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.RegisterApplicantRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    consumes = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "register",
                    operation = @Operation(
                            operationId = "register",
                            summary = "Crear solicitante",
                            description = "Registra un nuevo solicitante en el sistema",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = RegisterApplicantRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitante creado"),
                                    @ApiResponse(responseCode = "400", description = "Datos invalidos"),
                                    @ApiResponse(responseCode = "409", description = "Correo o documento de identidad ya registrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/document/{documentNumber}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "findByDocumentNumber",
                    operation = @Operation(
                            operationId = "findByDocumentNumber",
                            summary = "Obtener Solicitante por numero de documento",
                            description = "Permite consultar un solicitante usando su numero de documento",
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.PATH,
                                            name = "documentNumber",
                                            required = true,
                                            description = "Numero de documento del solicitante",
                                            example = "12345678"
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Solicitante encontrado"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado"),
                                    @ApiResponse(responseCode = "400", description = "Parametro invalido")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::register)
                .andRoute(GET("/api/v1/usuarios/document/{documentNumber}"), handler::findByDocumentNumber);
    }
}
