package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.applicant.Applicant;
import co.com.bancolombia.model.applicant.gateways.ApplicantRepository;
import co.com.bancolombia.r2dbc.entity.ApplicantEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.valueobject.Email;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class ApplicantReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Applicant,
        ApplicantEntity,
        String,
        ApplicantReactiveRepository
        > implements ApplicantRepository {
    public ApplicantReactiveRepositoryAdapter(ApplicantReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Applicant.class));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        log.info("Comprobando si existe solicitante con email: {}", email);
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByDocumentNumber(String documentNumber) {
        log.info("Comprobando si existe solicitante con documento: {}", documentNumber);
        return repository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public Mono<Applicant> findByDocumentNumber(String documentNumber) {
        log.debug("Obtener solicitante con documento: {}", documentNumber);
        return repository.findByDocumentNumber(documentNumber).map(this::toDomain);
    }

    @Override
    public Mono<Applicant> save(Applicant applicant) {
        log.info("Iniciando registro de solicitante con documento: {}", applicant.getDocumentNumber());

        return repository.save(toEntity(applicant)).map(this::toDomain);
    }

    private Applicant toDomain(ApplicantEntity e) {
        return new Applicant(
                e.id(),
                e.names(),
                e.lastNames(),
                e.documentNumber(),
                e.birthdate(),
                e.address(),
                e.phone(),
                new Email(e.email()),
                e.baseSalary()
        );
    }

    private ApplicantEntity toEntity(Applicant applicant) {
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
}
