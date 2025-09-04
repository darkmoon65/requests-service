package com.crediya.requests.api.validation;

import com.crediya.requests.api.dto.CreateSolicitudeDto;
import com.crediya.requests.api.exception.SolicitudeValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SolicitudeValidator {
    private final Validator validator;

    public Mono<CreateSolicitudeDto> validate(CreateSolicitudeDto user) {
        Set<ConstraintViolation<CreateSolicitudeDto>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream().collect(
                    Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (msg1, msg2) -> msg1 + ", " + msg2
                    ));

            return Mono.error(new SolicitudeValidationException("Errores de validación", errors));
        }

        return Mono.just(user);
    }
}
