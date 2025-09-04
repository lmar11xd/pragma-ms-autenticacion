package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.Role;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserReactiveRepositoryAdapterTest {

    private UserReactiveRepository repository;
    private UserReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(UserReactiveRepository.class);
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        adapter = new UserReactiveRepositoryAdapter(repository, mapper);
    }

    @Test
    void findByEmailShouldReturnMappedUser() {
        UserEntity entity = new UserEntity(
                "123",
                "applicant-1",
                "test@test.com",
                "hashedPwd",
                "ADMIN,CUSTOMER",
                true
        );

        when(repository.findByEmail("test@test.com")).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.findByEmail("test@test.com"))
                .assertNext(user -> {
                    assertThat(user.getId()).isEqualTo("123");
                    assertThat(user.getApplicantId()).isEqualTo("applicant-1");
                    assertThat(user.getEmail().getValue()).isEqualTo("test@test.com");
                    assertThat(user.getPasswordHash()).isEqualTo("hashedPwd");
                    assertThat(user.getRoles()).containsExactlyInAnyOrder(Role.ADMIN, Role.CUSTOMER);
                    assertThat(user.isEnabled()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void findByEmailShouldReturnEmptyIfNotFound() {
        when(repository.findByEmail("missing@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByEmail("missing@test.com"))
                .verifyComplete();
    }

    /*@Test
    void saveShouldMapUserToEntityAndBack() {
        User user = new User(
                "123",
                "applicant-1",
                new Email("test@test.com"),
                "hashedPwd",
                Set.of(Role.ADMIN, Role.ADVISER),
                true
        );

        UserEntity entity = new UserEntity(
                "123",
                "applicant-1",
                "test@test.com",
                "hashedPwd",
                "ADMIN,ADVISER",
                true
        );

        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.save(user))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(user.getId());
                    assertThat(saved.getApplicantId()).isEqualTo(user.getApplicantId());
                    assertThat(saved.getEmail()).isEqualTo(user.getEmail());
                    assertThat(saved.getPasswordHash()).isEqualTo(user.getPasswordHash());
                    assertThat(saved.getRoles()).containsExactlyInAnyOrder(Role.ADMIN, Role.ADVISER);
                    assertThat(saved.isEnabled()).isTrue();
                })
                .verifyComplete();

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        UserEntity savedEntity = captor.getValue();
        assertThat(savedEntity.rolesCsv()).isEqualTo("ADMIN,ADVISER");
    }*/
}