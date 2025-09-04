package co.com.bancolombia.model.loginattempt;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptTest {

    @Test
    void shouldReturnFalseWhenLockedUntilIsNull() {
        LoginAttempt attempt = LoginAttempt.builder()
                .email("test@example.com")
                .attempts(1)
                .lockedUntil(null)
                .build();

        assertThat(attempt.isLocked()).isFalse();
    }

    @Test
    void shouldReturnFalseWhenLockedUntilIsPast() {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setLockedUntil(LocalDateTime.now().minusMinutes(5));

        assertThat(attempt.isLocked()).isFalse();
    }

    @Test
    void shouldReturnTrueWhenLockedUntilIsFuture() {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setLockedUntil(LocalDateTime.now().plusMinutes(10));

        assertThat(attempt.isLocked()).isTrue();
    }

    @Test
    void shouldIncrementAttempts() {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setAttempts(2);

        attempt.increment();

        assertThat(attempt.getAttempts()).isEqualTo(3);
    }

    @Test
    void shouldResetAttemptsAndLockedUntil() {
        LoginAttempt attempt = LoginAttempt.builder()
                .attempts(5)
                .lockedUntil(LocalDateTime.now().plusMinutes(10))
                .build();

        attempt.reset();

        assertThat(attempt.getAttempts()).isZero();
        assertThat(attempt.getLockedUntil()).isNull();
    }

    @Test
    void shouldBuildObjectWithBuilder() {
        LocalDateTime now = LocalDateTime.now();

        LoginAttempt attempt = LoginAttempt.builder()
                .email("user@test.com")
                .attempts(1)
                .lastAttempt(now)
                .lockedUntil(now.plusMinutes(1))
                .build();

        assertThat(attempt.getEmail()).isEqualTo("user@test.com");
        assertThat(attempt.getAttempts()).isEqualTo(1);
        assertThat(attempt.getLastAttempt()).isEqualTo(now);
        assertThat(attempt.getLockedUntil()).isAfter(now);
    }
}
