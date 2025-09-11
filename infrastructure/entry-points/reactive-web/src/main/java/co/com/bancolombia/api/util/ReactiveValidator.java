package co.com.bancolombia.api.util;

import co.com.bancolombia.exception.DomainException;
import co.com.bancolombia.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReactiveValidator {
    private final Validator validator;

    public ReactiveValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> Mono<T> validate(T bean) {
        BeanPropertyBindingResult errors =
                new BeanPropertyBindingResult(bean, bean.getClass().getSimpleName());

        validator.validate(bean, errors);

        if (errors.hasErrors()) {
            List<Map<String, Object>> details = errors.getFieldErrors().stream()
                    .map(err -> {
                        Map<String, Object> detail = new HashMap<>();
                        detail.put("field", err.getField());
                        detail.put("message", err.getDefaultMessage());
                        detail.put("rejectedValue", err.getRejectedValue());
                        detail.put("code", err.getCode());
                        return detail;
                    })
                    .toList();

            return Mono.error(new DomainException(ErrorCode.VALIDATION_ERROR, Map.of("details", details)));
        }

        return Mono.just(bean);
    }
}
