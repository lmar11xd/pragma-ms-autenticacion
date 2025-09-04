package co.com.bancolombia.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    void shouldReturnCodeAndMessageForSpecificEnum() {
        ErrorCode error = ErrorCode.INVALID_CREDENTIALS;

        assertThat(error.getCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(error.getDefaultMessage()).isEqualTo("Credenciales invalidas");
    }

    @Test
    void allErrorCodesShouldHaveNonEmptyCodeAndMessage() {
        for (ErrorCode error : ErrorCode.values()) {
            assertThat(error.getCode())
                    .as("Code for %s should not be null or blank", error.name())
                    .isNotNull()
                    .isNotBlank();

            assertThat(error.getDefaultMessage())
                    .as("DefaultMessage for %s should not be null or blank", error.name())
                    .isNotNull()
                    .isNotBlank();
        }
    }

    @Test
    void shouldContainExpectedNumberOfErrorCodes() {
        assertThat(ErrorCode.values()).hasSize(15); // total de constantes definidas
    }
}