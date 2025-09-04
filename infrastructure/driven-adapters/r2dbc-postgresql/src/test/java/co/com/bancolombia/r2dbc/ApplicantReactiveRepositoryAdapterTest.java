package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.r2dbc.entity.ApplicantEntity;
import co.com.bancolombia.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApplicantReactiveRepositoryAdapterTest {

    private ApplicantReactiveRepository repository;
    private ApplicantReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ApplicantReactiveRepository.class);
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        adapter = new ApplicantReactiveRepositoryAdapter(repository, mapper);
    }

    @Test
    void existsByEmailShouldReturnTrue() {
        when(repository.existsByEmail("test@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByEmail("test@test.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmailShouldReturnFalse() {
        when(repository.existsByEmail("missing@test.com")).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsByEmail("missing@test.com"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByDocumentNumberShouldReturnTrue() {
        when(repository.existsByDocumentNumber("12345")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByDocumentNumber("12345"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByDocumentNumberShouldReturnApplicant() {
        ApplicantEntity entity = new ApplicantEntity(
                "1",
                "John",
                "Doe",
                "12345",
                LocalDate.of(1990, 1, 1),
                "Street 123",
                "555-1234",
                "john.doe@test.com",
                BigDecimal.valueOf(1500)
        );

        when(repository.findByDocumentNumber("12345")).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.findByDocumentNumber("12345"))
                .assertNext(applicant -> {
                    assertThat(applicant.getId()).isEqualTo("1");
                    assertThat(applicant.getNames()).isEqualTo("John");
                    assertThat(applicant.getLastNames()).isEqualTo("Doe");
                    assertThat(applicant.getDocumentNumber()).isEqualTo("12345");
                    assertThat(applicant.getBirthdate()).isEqualTo(LocalDate.of(1990, 1, 1));
                    assertThat(applicant.getEmail().getValue()).isEqualTo("john.doe@test.com");
                    assertThat(applicant.getBaseSalary()).isEqualByComparingTo("1500");
                })
                .verifyComplete();
    }

    @Test
    void findByDocumentNumberShouldReturnEmptyIfNotFound() {
        when(repository.findByDocumentNumber("00000")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByDocumentNumber("00000"))
                .verifyComplete();
    }

    @Test
    void saveShouldMapApplicantCorrectly() {
        Applicant applicant = new Applicant(
                "1",
                "Jane",
                "Smith",
                "54321",
                LocalDate.of(1985, 5, 20),
                "Main Ave 456",
                "555-6789",
                new Email("jane.smith@test.com"),
                BigDecimal.valueOf(2000)
        );

        ApplicantEntity entity = new ApplicantEntity(
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

        when(repository.save(any(ApplicantEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.save(applicant))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(applicant.getId());
                    assertThat(saved.getEmail()).isEqualTo(applicant.getEmail());
                    assertThat(saved.getBaseSalary()).isEqualByComparingTo(applicant.getBaseSalary());
                })
                .verifyComplete();

        // capturar el argumento enviado al repositorio
        ArgumentCaptor<ApplicantEntity> captor = ArgumentCaptor.forClass(ApplicantEntity.class);
        verify(repository).save(captor.capture());
        ApplicantEntity savedEntity = captor.getValue();
        assertThat(savedEntity.email()).isEqualTo("jane.smith@test.com");
    }
}