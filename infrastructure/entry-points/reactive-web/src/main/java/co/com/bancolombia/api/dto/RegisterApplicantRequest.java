package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "RegisterApplicantRequest", description = "Datos de registro de solicitante")
public record RegisterApplicantRequest(
        @Schema(description = "Nombres del solicitante", example = "Pedro Junior")
        @NotBlank String names,
        @Schema(description = "Apellidos del solicitante", example = "Martinez")
        @NotBlank String lastNames,
        @Schema(description = "Documento de indentidad del solicitante", example = "12345678")
        @NotBlank String documentNumber,
        @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
        LocalDate birthdate,
        @Schema(description = "Direccion", example = "Av. Siempre Viva 123")
        String address,
        @Schema(description = "Telefono", example = "987654321")
        String phone,
        @Schema(description = "Correo electronico", example = "pedroj@example.com")
        @Email @NotBlank String email,
        @Schema(description = "Contraseña", example = "123456")
        @NotBlank String password,
        @Schema(description = "Salario base", example = "5000")
        @NotNull @DecimalMin("0") @DecimalMax("15000000") BigDecimal baseSalary,
        @Schema(description = "Rol asignado", example = "CUSTOMER")
        @NotNull RoleDto role
) {
    public enum RoleDto {ADMIN, ADVISER, CUSTOMER}
}