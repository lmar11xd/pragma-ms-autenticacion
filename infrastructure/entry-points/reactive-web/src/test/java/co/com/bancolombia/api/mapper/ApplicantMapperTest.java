package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.ApplicantDto;
import co.com.bancolombia.api.dto.RegisterApplicantRequest;
import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.valueobject.Email;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicantMapperTest {

    @Test
    void toDomain_ShouldMapRequestToApplicant() {
        // Arrange
        RegisterApplicantRequest request = new RegisterApplicantRequest(
                "Juan",
                "Pérez",
                "123456789",
                LocalDate.of(1990, 5, 20),
                "Calle 123",
                "987654321",
                "juan@example.com",
                "123456",
                new BigDecimal("2500.00"),
                RegisterApplicantRequest.RoleDto.CUSTOMER
        );

        // Act
        Applicant applicant = ApplicantMapper.toDomain(request);

        // Assert
        assertThat(applicant.getId()).isNull();
        assertThat(applicant.getNames()).isEqualTo("Juan");
        assertThat(applicant.getLastNames()).isEqualTo("Pérez");
        assertThat(applicant.getDocumentNumber()).isEqualTo("123456789");
        assertThat(applicant.getBirthdate()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(applicant.getAddress()).isEqualTo("Calle 123");
        assertThat(applicant.getPhone()).isEqualTo("987654321");
        assertThat(applicant.getEmail()).isEqualTo(new Email("juan@example.com"));
        assertThat(applicant.getBaseSalary()).isEqualByComparingTo("2500.00");
    }

    @Test
    void toDto_ShouldMapApplicantToDto() {
        // Arrange
        Applicant applicant = new Applicant(
                "abc123",
                "Ana",
                "López",
                "987654321",
                LocalDate.of(1985, 3, 15),
                "Av. Principal 456",
                "123456789",
                new Email("ana@example.com"),
                new BigDecimal("3200.50")
        );

        // Act
        ApplicantDto dto = ApplicantMapper.toDto(applicant);

        // Assert
        assertThat(dto.id()).isEqualTo("abc123");
        assertThat(dto.names()).isEqualTo("Ana");
        assertThat(dto.lastNames()).isEqualTo("López");
        assertThat(dto.documentNumber()).isEqualTo("987654321");
        assertThat(dto.birthdate()).isEqualTo(LocalDate.of(1985, 3, 15));
        assertThat(dto.address()).isEqualTo("Av. Principal 456");
        assertThat(dto.phone()).isEqualTo("123456789");
        assertThat(dto.email()).isEqualTo("ana@example.com");
        assertThat(dto.baseSalary()).isEqualByComparingTo("3200.50");
    }
}