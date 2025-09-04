package co.com.bancolombia.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApplicantDto(
        String id,
        String names,
        String lastNames,
        String documentNumber,
        LocalDate birthdate,
        String address,
        String phone,
        String email,
        BigDecimal baseSalary
) {
}