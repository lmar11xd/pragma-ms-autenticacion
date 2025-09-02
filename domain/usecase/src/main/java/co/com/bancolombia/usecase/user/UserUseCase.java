package co.com.bancolombia.usecase.user;

import co.com.bancolombia.exception.DomainException;
import co.com.bancolombia.exception.ErrorCode;
import co.com.bancolombia.helper.PasswordEncoder;
import co.com.bancolombia.valueobject.Email;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> authenticate(Email email, String password) {
        return userRepository.findByEmail(email.getValue())
                .switchIfEmpty(Mono.error(new DomainException(ErrorCode.INVALID_CREDENTIALS)))
                .flatMap(u -> passwordEncoder.matches(password, u.getPasswordHash())
                        ? Mono.just(u)
                        : Mono.error(new DomainException(ErrorCode.INVALID_CREDENTIALS)));
    }

    public Mono<User> save(User user) {
        if (user.getApplicantId().isBlank())
            return Mono.error(new DomainException(ErrorCode.REQUERID_APPLICANTID));

        // ¿Se debe encriptar la contraseña?
        String passwordHashed = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(passwordHashed);

        return userRepository.save(user);
    }
}
