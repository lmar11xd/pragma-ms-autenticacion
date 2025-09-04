package co.com.bancolombia.usecase.user;

import co.com.bancolombia.exception.DomainException;
import co.com.bancolombia.exception.ErrorCode;
import co.com.bancolombia.helper.PasswordEncoder;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userUseCase = new UserUseCase(userRepository, passwordEncoder);
    }

    @Test
    void authenticateShouldReturnUserWhenCredentialsValid() {
        Email email = new Email("test@example.com");
        User user = User.builder()
                .id("1")
                .applicantId("app1")
                .email(email)
                .passwordHash("hashed")
                .roles(Set.of())
                .enabled(true)
                .build();

        when(userRepository.findByEmail(email.getValue())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);

        StepVerifier.create(userUseCase.authenticate(email, "raw"))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).findByEmail(email.getValue());
        verify(passwordEncoder).matches("raw", "hashed");
    }

    @Test
    void authenticateShouldErrorWhenUserNotFound() {
        Email email = new Email("notfound@example.com");

        when(userRepository.findByEmail(email.getValue())).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.authenticate(email, "raw"))
                .expectErrorSatisfies(error -> {
                    assert error instanceof DomainException;
                    assert ((DomainException) error).getErrorCode() == ErrorCode.INVALID_CREDENTIALS;
                })
                .verify();

        verify(userRepository).findByEmail(email.getValue());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authenticateShouldErrorWhenPasswordDoesNotMatch() {
        Email email = new Email("test@example.com");
        User user = User.builder()
                .id("1")
                .applicantId("app1")
                .email(email)
                .passwordHash("hashed")
                .roles(Set.of())
                .enabled(true)
                .build();

        when(userRepository.findByEmail(email.getValue())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        StepVerifier.create(userUseCase.authenticate(email, "wrong"))
                .expectErrorSatisfies(error -> {
                    assert error instanceof DomainException;
                    assert ((DomainException) error).getErrorCode() == ErrorCode.INVALID_CREDENTIALS;
                })
                .verify();

        verify(userRepository).findByEmail(email.getValue());
        verify(passwordEncoder).matches("wrong", "hashed");
    }

    @Test
    void saveShouldErrorWhenApplicantIdIsBlank() {
        User user = User.builder()
                .id("1")
                .applicantId("   ") // blank
                .email(new Email("test@example.com"))
                .passwordHash("pwd")
                .roles(Set.of())
                .enabled(true)
                .build();

        StepVerifier.create(userUseCase.save(user))
                .expectErrorSatisfies(error -> {
                    assert error instanceof DomainException;
                    assert ((DomainException) error).getErrorCode() == ErrorCode.REQUERID_APPLICANTID;
                })
                .verify();

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void saveShouldEncryptPasswordAndSaveUser() {
        User user = User.builder()
                .id("1")
                .applicantId("app1")
                .email(new Email("test@example.com"))
                .passwordHash("rawPassword")
                .roles(Set.of())
                .enabled(true)
                .build();

        when(passwordEncoder.encode("rawPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(userUseCase.save(user))
                .assertNext(savedUser -> {
                    assert savedUser.getPasswordHash().equals("hashedPassword");
                    assert savedUser.getApplicantId().equals("app1");
                })
                .verifyComplete();

        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(any(User.class));
    }
}