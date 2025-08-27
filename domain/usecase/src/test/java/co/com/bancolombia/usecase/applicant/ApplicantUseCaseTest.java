package co.com.bancolombia.usecase.applicant;

import co.com.bancolombia.exception.DomainException;
import co.com.bancolombia.exception.ErrorCode;
import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.model.applicant.gateways.ApplicantRepository;
import co.com.bancolombia.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ApplicantUseCaseTest {
    private ApplicantRepository repository;
    private ApplicantUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ApplicantRepository.class);
        useCase = new ApplicantUseCase(repository);
    }

    private Applicant buildValidApplicant() {
        return Applicant.builder()
                .id("1")
                .names("Luis")
                .lastNames("Alvarado")
                .documentNumber("12345678")
                .birthdate(LocalDate.of(1995, 5, 20))
                .address("Av. Siempre Viva")
                .phone("987654321")
                .email(new Email("luis@example.com"))
                .baseSalary(new BigDecimal("5000"))
                .build();
    }

    @Test
    void testRegisterSuccess() {
        Applicant applicant = buildValidApplicant();

        when(repository.existsByEmail(eq("luis@example.com")))
                .thenReturn(Mono.just(false));
        when(repository.save(any(Applicant.class)))
                .thenReturn(Mono.just(applicant));

        StepVerifier.create(useCase.register(applicant))
                .expectNext(applicant)
                .verifyComplete();
    }

    @Test
    void testRegisterFailsWhenNamesEmpty() {
        Applicant applicant = buildValidApplicant();
        applicant.setNames("");

        StepVerifier.create(useCase.register(applicant))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ((DomainException) ex).getErrorCode() == ErrorCode.REQUERID_NAMES)
                .verify();
    }

    @Test
    void testRegisterFailsWhenLastNamesEmpty() {
        Applicant applicant = buildValidApplicant();
        applicant.setLastNames("");

        StepVerifier.create(useCase.register(applicant))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ((DomainException) ex).getErrorCode() == ErrorCode.REQUERID_LASTNAMES)
                .verify();
    }

    @Test
    void testRegisterFailsWhenDocumentNumberEmpty() {
        Applicant applicant = buildValidApplicant();
        applicant.setDocumentNumber("");

        StepVerifier.create(useCase.register(applicant))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ((DomainException) ex).getErrorCode() == ErrorCode.REQUERID_DOCUMENTNUMBER)
                .verify();
    }

    @Test
    void testRegisterFailsWhenSalaryNegative() {
        Applicant applicant = buildValidApplicant();
        applicant.setBaseSalary(new BigDecimal("-100"));

        StepVerifier.create(useCase.register(applicant))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ((DomainException) ex).getErrorCode() == ErrorCode.INVALID_SALARY)
                .verify();
    }

    @Test
    void testRegisterFailsWhenSalaryTooHigh() {
        Applicant applicant = buildValidApplicant();
        applicant.setBaseSalary(new BigDecimal("20000000")); // > MAX_SALARY

        StepVerifier.create(useCase.register(applicant))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ((DomainException) ex).getErrorCode() == ErrorCode.INVALID_SALARY)
                .verify();
    }

    @Test
    void testRegisterFailsWhenEmailAlreadyExists() {
        Applicant applicant = buildValidApplicant();

        when(repository.existsByEmail(eq("luis@example.com")))
                .thenReturn(Mono.just(true));

        StepVerifier.create(useCase.register(applicant))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ((DomainException) ex).getErrorCode() == ErrorCode.EXISTS_EMAIL)
                .verify();
    }
}
