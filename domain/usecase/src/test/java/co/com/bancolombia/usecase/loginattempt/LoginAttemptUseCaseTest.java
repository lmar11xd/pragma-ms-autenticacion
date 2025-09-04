package co.com.bancolombia.usecase.loginattempt;

import co.com.bancolombia.model.loginattempt.LoginAttempt;
import co.com.bancolombia.model.loginattempt.gateways.LoginAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoginAttemptUseCaseTest {

    private LoginAttemptRepository repository;
    private LoginAttemptUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(LoginAttemptRepository.class);
        useCase = new LoginAttemptUseCase(repository);
    }

    @Test
    void loginFailedShouldCreateNewAttemptIfNotExists() {
        when(repository.findByEmail("test@test.com")).thenReturn(Mono.empty());
        when(repository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.loginFailed("test@test.com"))
                .verifyComplete();

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(repository).save(captor.capture());

        LoginAttempt saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("test@test.com");
        assertThat(saved.getAttempts()).isEqualTo(1);
        assertThat(saved.getLastAttempt()).isNotNull();
    }

    @Test
    void loginFailedShouldIncrementExistingAttempt() {
        LoginAttempt existing = LoginAttempt.builder()
                .email("test@test.com")
                .attempts(2)
                .lastAttempt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(repository.findByEmail("test@test.com")).thenReturn(Mono.just(existing));
        when(repository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.loginFailed("test@test.com"))
                .verifyComplete();

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(repository).save(captor.capture());

        LoginAttempt saved = captor.getValue();
        assertThat(saved.getAttempts()).isEqualTo(3);
        assertThat(saved.getLastAttempt()).isNotNull();
    }

    @Test
    void loginFailedShouldLockAccountAfterMaxAttempts() {
        LoginAttempt existing = LoginAttempt.builder()
                .email("test@test.com")
                .attempts(5)
                .build();

        when(repository.findByEmail("test@test.com")).thenReturn(Mono.just(existing));
        when(repository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.loginFailed("test@test.com"))
                .verifyComplete();

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(repository).save(captor.capture());

        LoginAttempt saved = captor.getValue();
        assertThat(saved.getAttempts()).isEqualTo(6);
        assertThat(saved.getLockedUntil()).isAfter(LocalDateTime.now());
    }

    @Test
    void loginSucceededShouldResetAttemptsIfExists() {
        LoginAttempt existing = LoginAttempt.builder()
                .email("test@test.com")
                .attempts(4)
                .lockedUntil(LocalDateTime.now().plusMinutes(10))
                .build();

        when(repository.findByEmail("test@test.com")).thenReturn(Mono.just(existing));
        when(repository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.loginSucceeded("test@test.com"))
                .verifyComplete();

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(repository).save(captor.capture());

        LoginAttempt saved = captor.getValue();
        assertThat(saved.getAttempts()).isZero();
        assertThat(saved.getLockedUntil()).isNull();
    }

    @Test
    void loginSucceededShouldDoNothingIfNotExists() {
        when(repository.findByEmail("missing@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.loginSucceeded("missing@test.com"))
                .verifyComplete();

        verify(repository, never()).save(any());
    }

    @Test
    void isLockedShouldReturnFalseIfNotExists() {
        when(repository.findByEmail("unknown@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.isLocked("unknown@test.com"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isLockedShouldReturnTrueIfLocked() {
        LoginAttempt attempt = LoginAttempt.builder()
                .email("locked@test.com")
                .lockedUntil(LocalDateTime.now().plusMinutes(5))
                .build();

        when(repository.findByEmail("locked@test.com")).thenReturn(Mono.just(attempt));

        StepVerifier.create(useCase.isLocked("locked@test.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isLockedShouldReturnFalseIfNotLocked() {
        LoginAttempt attempt = LoginAttempt.builder()
                .email("free@test.com")
                .lockedUntil(null)
                .build();

        when(repository.findByEmail("free@test.com")).thenReturn(Mono.just(attempt));

        StepVerifier.create(useCase.isLocked("free@test.com"))
                .expectNext(false)
                .verifyComplete();
    }
}
