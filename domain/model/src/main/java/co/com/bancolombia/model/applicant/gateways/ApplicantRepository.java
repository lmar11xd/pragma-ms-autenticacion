package co.com.bancolombia.model.applicant.gateways;

import co.com.bancolombia.model.applicant.Applicant;
import reactor.core.publisher.Mono;

public interface ApplicantRepository {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDocumentNumber(String documentNumber);
    Mono<Applicant> findByDocumentNumber(String documentNumber);
    Mono<Applicant> save(Applicant applicant);
}
