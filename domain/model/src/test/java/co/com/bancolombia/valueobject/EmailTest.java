package co.com.bancolombia.valueobject;

import co.com.bancolombia.exception.DomainException;
import co.com.bancolombia.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        Email email = new Email("User@Test.COM");

        assertThat(email.getValue()).isEqualTo("user@test.com"); // normaliza a minúsculas
        assertThat(email.toString()).isEqualTo("user@test.com");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(DomainException.class)
                .hasMessage(ErrorCode.INVALID_EMAIL.getDefaultMessage())
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_EMAIL);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        assertThatThrownBy(() -> new Email("not-an-email"))
                .isInstanceOf(DomainException.class)
                .hasMessage(ErrorCode.INVALID_EMAIL.getDefaultMessage())
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_EMAIL);
    }

    @Test
    void equalsAndHashCodeShouldWorkProperly() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("TEST@example.com"); // se normaliza a minúscula
        Email email3 = new Email("other@example.com");

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());

        assertThat(email1).isNotEqualTo(email3);
    }

    @Test
    void toStringShouldReturnValue() {
        Email email = new Email("example@test.com");
        assertThat(email.toString()).isEqualTo("example@test.com");
    }
}
