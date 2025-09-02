package co.com.bancolombia.loginattempt;

import co.com.bancolombia.model.loginattempt.LoginAttempt;
import co.com.bancolombia.model.loginattempt.gateways.LoginAttemptRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryLoginAttemptRepository implements LoginAttemptRepository {

    private final ConcurrentHashMap<String, LoginAttempt> store = new ConcurrentHashMap<>();

    @Override
    public Mono<LoginAttempt> findByEmail(String email) {
        return Mono.justOrEmpty(store.get(email));
    }

    @Override
    public Mono<LoginAttempt> save(LoginAttempt attempt) {
        store.put(attempt.getEmail(), attempt);
        return Mono.just(attempt);
    }
}