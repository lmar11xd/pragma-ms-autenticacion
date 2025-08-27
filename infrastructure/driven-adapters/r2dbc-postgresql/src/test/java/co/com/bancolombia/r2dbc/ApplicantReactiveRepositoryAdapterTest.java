package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.r2dbc.entity.ApplicantEntity;
import co.com.bancolombia.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicantReactiveRepositoryAdapterTest {

    private ApplicantReactiveRepository repository;
    private ObjectMapper mapper;
    private ApplicantReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ApplicantReactiveRepository.class);
        mapper = Mockito.mock(ObjectMapper.class);
        adapter = new ApplicantReactiveRepositoryAdapter(repository, mapper);
    }

    private Applicant buildApplicant() {
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

    private ApplicantEntity buildEntityFromApplicant(Applicant applicant) {
        return new ApplicantEntity(
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

    @Test
    void testExistsByEmail() {
        when(repository.existsByEmail(eq("luis@example.com")))
                .thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByEmail("luis@example.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testSaveMapsCorrectly() {
        Applicant applicant = buildApplicant();
        ApplicantEntity entity = buildEntityFromApplicant(applicant);

        when(repository.save(any(ApplicantEntity.class)))
                .thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.save(applicant))
                .expectNextMatches(saved ->
                        saved.getId().equals(applicant.getId()) &&
                                saved.getNames().equals(applicant.getNames()) &&
                                saved.getLastNames().equals(applicant.getLastNames()) &&
                                saved.getDocumentNumber().equals(applicant.getDocumentNumber()) &&
                                saved.getBirthdate().equals(applicant.getBirthdate()) &&
                                saved.getAddress().equals(applicant.getAddress()) &&
                                saved.getPhone().equals(applicant.getPhone()) &&
                                saved.getEmail().getValue().equals(applicant.getEmail().getValue()) &&
                                saved.getBaseSalary().equals(applicant.getBaseSalary())
                )
                .verifyComplete();
    }
}
