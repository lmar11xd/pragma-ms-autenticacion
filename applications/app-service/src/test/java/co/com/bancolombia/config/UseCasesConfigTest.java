package co.com.bancolombia.config;

import co.com.bancolombia.helper.PasswordEncoder;
import co.com.bancolombia.model.applicant.gateways.ApplicantRepository;
import co.com.bancolombia.model.loginattempt.gateways.LoginAttemptRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = UseCasesConfig.class)
class UseCasesConfigTest {

    @MockitoBean
    private ApplicantRepository applicantRepository;

    @MockitoBean
    private LoginAttemptRepository loginAttemptRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void testUseCaseBeansExist(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();

        boolean useCaseBeanFound = Arrays.stream(beanNames)
                .anyMatch(name -> name.endsWith("UseCase"));

        assertThat(useCaseBeanFound)
                .as("Debe existir al menos un bean que termine en 'UseCase'")
                .isTrue();
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}