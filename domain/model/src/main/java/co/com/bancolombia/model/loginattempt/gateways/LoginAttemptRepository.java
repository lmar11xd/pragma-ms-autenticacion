package co.com.bancolombia.model.loginattempt.gateways;

import co.com.bancolombia.model.loginattempt.LoginAttempt;
import reactor.core.publisher.Mono;

public interface LoginAttemptRepository {
    Mono<LoginAttempt> findByEmail(String email);
    Mono<LoginAttempt> save(LoginAttempt attempt);
}
