package co.com.bancolombia.exception;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {

    @Test
    void shouldCreateWithErrorCodeAndDefaultMessage() {
        DomainException ex = new DomainException(ErrorCode.REQUERID_NAMES);

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REQUERID_NAMES);
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.REQUERID_NAMES.getDefaultMessage());
        assertThat(ex.getDetails()).isEmpty();
    }

    @Test
    void shouldCreateWithCustomMessage() {
        DomainException ex = new DomainException(ErrorCode.INVALID_SALARY, "Custom salary error");

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_SALARY);
        assertThat(ex.getMessage()).isEqualTo("Custom salary error");
        assertThat(ex.getDetails()).isEmpty();
    }

    @Test
    void shouldCreateWithErrorCodeAndDetails() {
        Map<String, Object> details = Map.of("field", "email", "value", "invalid@example.com");

        DomainException ex = new DomainException(ErrorCode.EXISTS_EMAIL, details);

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EXISTS_EMAIL);
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.EXISTS_EMAIL.getDefaultMessage());
        assertThat(ex.getDetails()).containsEntry("field", "email")
                .containsEntry("value", "invalid@example.com");
    }
}
