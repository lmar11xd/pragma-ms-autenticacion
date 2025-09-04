package co.com.bancolombia.usecase.applicant;

import co.com.bancolombia.exception.DomainException;
import co.com.bancolombia.exception.ErrorCode;
import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.model.applicant.gateways.ApplicantRepository;
import co.com.bancolombia.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicantUseCaseTest {

    private ApplicantRepository repository;
    private ApplicantUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ApplicantRepository.class);
        useCase = new ApplicantUseCase(repository);
    }

    private Applicant buildValidApplicant() {
        return Applicant.builder()
                .names("John")
                .lastNames("Doe")
                .documentNumber("12345")
                .email(new Email("john@example.com"))
                .baseSalary(new BigDecimal("5000"))
                .build();
    }

    // --- register tests ---

    @Test
    void registerShouldErrorWhenNamesBlank() {
        Applicant applicant = buildValidApplicant();
        applicant.setNames(" ");

        StepVerifier.create(useCase.register(applicant))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.REQUERID_NAMES;
                })
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void registerShouldErrorWhenLastNamesBlank() {
        Applicant applicant = buildValidApplicant();
        applicant.setLastNames(" ");

        StepVerifier.create(useCase.register(applicant))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.REQUERID_LASTNAMES;
                })
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void registerShouldErrorWhenDocumentNumberBlank() {
        Applicant applicant = buildValidApplicant();
        applicant.setDocumentNumber(" ");

        StepVerifier.create(useCase.register(applicant))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.REQUERID_DOCUMENTNUMBER;
                })
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void registerShouldErrorWhenSalaryNegative() {
        Applicant applicant = buildValidApplicant();
        applicant.setBaseSalary(new BigDecimal("-10"));

        StepVerifier.create(useCase.register(applicant))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.INVALID_SALARY;
                })
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void registerShouldErrorWhenSalaryTooHigh() {
        Applicant applicant = buildValidApplicant();
        applicant.setBaseSalary(new BigDecimal("20000000")); // mayor que MAX_SALARY

        StepVerifier.create(useCase.register(applicant))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.INVALID_SALARY;
                })
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void registerShouldErrorWhenEmailExists() {
        Applicant applicant = buildValidApplicant();

        when(repository.existsByEmail(applicant.getEmail().getValue()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(useCase.register(applicant))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.EXISTS_EMAIL;
                })
                .verify();

        verify(repository).existsByEmail(applicant.getEmail().getValue());
        verify(repository, never()).save(any());
    }

    @Test
    void registerShouldErrorWhenDocumentNumberExists() {
        Applicant applicant = buildValidApplicant();

        when(repository.existsByEmail(applicant.getEmail().getValue())).thenReturn(Mono.just(false));
        when(repository.existsByDocumentNumber(applicant.getDocumentNumber())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.register(applicant))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.EXISTS_DOCUMENTNUMBER;
                })
                .verify();

        verify(repository).existsByEmail(applicant.getEmail().getValue());
        verify(repository).existsByDocumentNumber(applicant.getDocumentNumber());
        verify(repository, never()).save(any());
    }

    @Test
    void registerShouldSaveWhenValid() {
        Applicant applicant = buildValidApplicant();

        when(repository.existsByEmail(applicant.getEmail().getValue())).thenReturn(Mono.just(false));
        when(repository.existsByDocumentNumber(applicant.getDocumentNumber())).thenReturn(Mono.just(false));
        when(repository.save(applicant)).thenReturn(Mono.just(applicant));

        StepVerifier.create(useCase.register(applicant))
                .expectNext(applicant)
                .verifyComplete();

        verify(repository).existsByEmail(applicant.getEmail().getValue());
        verify(repository).existsByDocumentNumber(applicant.getDocumentNumber());
        verify(repository).save(applicant);
    }

    // --- findByDocumentNumber tests ---

    @Test
    void findByDocumentNumberShouldErrorWhenBlank() {
        StepVerifier.create(useCase.findByDocumentNumber(" "))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.REQUERID_DOCUMENTNUMBER;
                })
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void findByDocumentNumberShouldErrorWhenNotFound() {
        when(repository.findByDocumentNumber("12345")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findByDocumentNumber("12345"))
                .expectErrorSatisfies(e -> {
                    assert e instanceof DomainException;
                    assert ((DomainException) e).getErrorCode() == ErrorCode.APPLICANT_NOT_FOUND;
                })
                .verify();

        verify(repository).findByDocumentNumber("12345");
    }

    @Test
    void findByDocumentNumberShouldReturnApplicantWhenFound() {
        Applicant applicant = buildValidApplicant();

        when(repository.findByDocumentNumber("12345")).thenReturn(Mono.just(applicant));

        StepVerifier.create(useCase.findByDocumentNumber("12345"))
                .expectNext(applicant)
                .verifyComplete();

        verify(repository).findByDocumentNumber("12345");
    }
}
