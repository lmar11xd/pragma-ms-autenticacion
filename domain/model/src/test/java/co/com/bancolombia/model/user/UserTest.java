package co.com.bancolombia.model.user;

import co.com.bancolombia.valueobject.Email;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldBuildUserWithBuilder() {
        Email email = new Email("test@example.com");
        Role role = Role.ADMIN;

        User user = User.builder()
                .id("1234")
                .applicantId("5678")
                .email(email)
                .passwordHash("hashedPassword")
                .roles(Set.of(role))
                .enabled(true)
                .build();

        assertThat(user.getId()).isEqualTo("1234");
        assertThat(user.getApplicantId()).isEqualTo("5678");
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPasswordHash()).isEqualTo("hashedPassword");
        assertThat(user.getRoles()).containsExactly(role);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void shouldModifyUserWithSetters() {
        User user = new User();
        user.setId("id1");
        user.setApplicantId("applicant1");
        Email email = new Email("user@test.com");
        user.setEmail(email);
        user.setPasswordHash("pwd123");
        Role role = Role.CUSTOMER;
        user.setRoles(Set.of(role));
        user.setEnabled(false);

        assertThat(user.getId()).isEqualTo("id1");
        assertThat(user.getApplicantId()).isEqualTo("applicant1");
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPasswordHash()).isEqualTo("pwd123");
        assertThat(user.getRoles()).containsExactly(role);
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    void shouldCloneUserWithToBuilder() {
        User original = User.builder()
                .id("original-id")
                .enabled(true)
                .build();

        User clone = original.toBuilder()
                .id("new-id")
                .enabled(false)
                .build();

        assertThat(clone.getId()).isEqualTo("new-id");
        assertThat(clone.isEnabled()).isFalse();

        // Verificamos que el original no se alteró
        assertThat(original.getId()).isEqualTo("original-id");
        assertThat(original.isEnabled()).isTrue();
    }

    @Test
    void shouldCreateUserWithAllArgsConstructor() {
        Email email = new Email("allargs@test.com");
        Role role = Role.ADVISER;

        User user = new User("id2", "app2", email, "hash123", Set.of(role), true);

        assertThat(user.getId()).isEqualTo("id2");
        assertThat(user.getApplicantId()).isEqualTo("app2");
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPasswordHash()).isEqualTo("hash123");
        assertThat(user.getRoles()).containsExactly(role);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void shouldCreateUserWithNoArgsConstructor() {
        User user = new User();
        assertThat(user).isNotNull();
    }
}