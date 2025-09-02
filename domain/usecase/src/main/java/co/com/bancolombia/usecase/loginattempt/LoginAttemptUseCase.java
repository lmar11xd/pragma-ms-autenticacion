package co.com.bancolombia.usecase.loginattempt;

import co.com.bancolombia.model.loginattempt.LoginAttempt;
import co.com.bancolombia.model.loginattempt.gateways.LoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LoginAttemptUseCase {
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_TIME = Duration.ofMinutes(15);

    private final LoginAttemptRepository loginAttemptRepository;

    public Mono<Void> loginFailed(String email) {
        return loginAttemptRepository.findByEmail(email)
                .defaultIfEmpty(LoginAttempt.builder().email(email).attempts(0).build())
                .flatMap(attempt -> {
                    attempt.increment();
                    attempt.setLastAttempt(LocalDateTime.now());
                    if (attempt.getAttempts() >= MAX_ATTEMPTS) {
                        attempt.setLockedUntil(LocalDateTime.now().plus(LOCK_TIME));
                    }
                    return loginAttemptRepository.save(attempt);
                })
                .then();
    }

    public Mono<Void> loginSucceeded(String email) {
        return loginAttemptRepository.findByEmail(email)
                .flatMap(attempt -> {
                    attempt.reset();
                    return loginAttemptRepository.save(attempt);
                })
                .then();
    }

    public Mono<Boolean> isLocked(String email) {
        return loginAttemptRepository.findByEmail(email)
                .map(LoginAttempt::isLocked)
                .defaultIfEmpty(false);
    }

}
