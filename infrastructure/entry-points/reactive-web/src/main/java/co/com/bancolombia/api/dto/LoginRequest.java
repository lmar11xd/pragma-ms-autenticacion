package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Correo del solicitante", example = "pedroj@example.com")
        @Email @NotBlank String email,
        @Schema(description = "Contraseña del solicitante", example = "123456")
        @NotBlank String password
) {
}
