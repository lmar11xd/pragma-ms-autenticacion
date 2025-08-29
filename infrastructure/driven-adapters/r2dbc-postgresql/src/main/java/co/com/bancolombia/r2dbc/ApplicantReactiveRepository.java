package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.ApplicantEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ApplicantReactiveRepository extends ReactiveCrudRepository<ApplicantEntity, String>, ReactiveQueryByExampleExecutor<ApplicantEntity> {
    @Query("select exists(select 1 from applicants where email = :email)")
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDocumentNumber(String documentNumber);
    Mono<ApplicantEntity> findByDocumentNumber(String documentNumber);
}
