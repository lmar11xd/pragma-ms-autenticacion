package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.Role;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.valueobject.Email;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        String,
        UserReactiveRepository
        > implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    private static User map(UserEntity e) {
        Set<Role> roles = Arrays.stream(e.rolesCsv().split(","))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        return new User(e.id(), e.applicantId(), new Email(e.email()), e.passwordHash(), roles, e.enabled());
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserReactiveRepositoryAdapter::map);
    }

    @Override
    public Mono<User> save(User user) {
        String rolesCsv = user.getRoles().stream().map(Enum::name).collect(Collectors.joining(","));
        UserEntity e = new UserEntity(
                user.getId(),
                user.getApplicantId(),
                user.getEmail().getValue(),
                user.getPasswordHash(),
                rolesCsv,
                user.isEnabled()
        );
        return repository.save(e).map(UserReactiveRepositoryAdapter::map);
    }
}
