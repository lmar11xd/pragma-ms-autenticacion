package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.ApplicantDto;
import co.com.bancolombia.api.dto.RegisterApplicantRequest;
import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.valueobject.Email;

public final class ApplicantMapper {

    public static Applicant toDomain(RegisterApplicantRequest dto) {
        return new Applicant(
                null,
                dto.names(),
                dto.lastNames(),
                dto.documentNumber(),
                dto.birthdate(),
                dto.address(),
                dto.phone(),
                new Email(dto.email()),
                dto.baseSalary()
        );
    }

    public static ApplicantDto toDto(Applicant applicant) {
        return new ApplicantDto(
                applicant.getId(),
                applicant.getNames(),
                applicant.getLastNames(),
                applicant.getDocumentNumber(),
                applicant.getBirthdate(),
                applicant.getAddress(),
                applicant.getPhone(),
                applicant.getEmail().getValue(),
                applicant.getBaseSalary()
        );
    }

    private ApplicantMapper() {
    }
}
