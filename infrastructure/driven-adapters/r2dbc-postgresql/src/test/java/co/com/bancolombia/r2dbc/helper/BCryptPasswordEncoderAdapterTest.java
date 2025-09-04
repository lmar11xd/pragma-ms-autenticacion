package co.com.bancolombia.r2dbc.helper;

import co.com.bancolombia.helper.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordEncoderAdapterTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoderAdapter();
    }

    @Test
    void shouldEncodePassword() {
        String rawPassword = "mySecret123";

        String encoded = passwordEncoder.encode(rawPassword);

        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(encoded).startsWith("$2"); // BCrypt hashes start with $2a, $2b, etc.
    }

    @Test
    void shouldMatchEncodedPassword() {
        String rawPassword = "mySecret123";

        String encoded = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches(rawPassword, encoded);

        assertThat(matches).isTrue();
    }

    @Test
    void shouldNotMatchDifferentPassword() {
        String rawPassword = "mySecret123";
        String otherPassword = "wrongPassword";

        String encoded = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches(otherPassword, encoded);

        assertThat(matches).isFalse();
    }
}