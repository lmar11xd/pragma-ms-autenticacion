package co.com.bancolombia.usecase.applicant;

import co.com.bancolombia.exception.DomainException;
import co.com.bancolombia.exception.ErrorCode;
import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.model.applicant.gateways.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static co.com.bancolombia.constants.Constants.*;

@RequiredArgsConstructor
public class ApplicantUseCase {

    /*
     * - Se debe poder registrar un nuevo solicitante proporcionando sus datos personales:
     *       nombres, apellidos, fecha_nacimiento, direccion, telefono, correo_electronico y salario_base.
     * - El sistema debe validar que los campos nombres, apellidos, correo_electronico y salario_base no sean nulos o vacíos.
     * - El sistema debe validar que el correo_electronico proporcionado no esté previamente registrado por otro solicitante.
     * - El sistema debe validar el formato correcto de los datos, como por ejemplo,
     *       que el salario_base sea un valor numérico (este entre 0 y 15000000) y el correo_electronico tenga un formato de email válido.
     * */
    private final ApplicantRepository applicantRepository;

    public Mono<Applicant> register(Applicant applicant) {
        if (applicant.getNames().isBlank())
            return Mono.error(new DomainException(ErrorCode.REQUERID_NAMES));

        if (applicant.getLastNames().isBlank())
            return Mono.error(new DomainException(ErrorCode.REQUERID_LASTNAMES));

        if (applicant.getDocumentNumber().isBlank())
            return Mono.error(new DomainException(ErrorCode.REQUERID_DOCUMENTNUMBER));

        BigDecimal salary = applicant.getBaseSalary();
        if (salary.compareTo(BigDecimal.ZERO) < 0 || salary.compareTo(new BigDecimal(MAX_SALARY)) > 0)
            return Mono.error(new DomainException(ErrorCode.INVALID_SALARY));

        return applicantRepository.existsByEmail(applicant.getEmail().getValue())
                .flatMap(exists -> exists
                        ? Mono.error(new DomainException(ErrorCode.EXISTS_EMAIL))
                        : applicantRepository.save(applicant)
                );
    }
}
