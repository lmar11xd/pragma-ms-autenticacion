package co.com.bancolombia.model.applicant.gateways;

import co.com.bancolombia.model.applicant.Applicant;
import reactor.core.publisher.Mono;

public interface ApplicantRepository {
    Mono<Boolean> existsByEmail(String email);
    Mono<Applicant> save(Applicant applicant);
}
