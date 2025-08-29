package com.crediya.requests.api;

import com.crediya.requests.api.dto.CreateSolicitudeDto;
import com.crediya.requests.api.mapper.SolicitudeDtoMapper;
import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.usecase.solicitude.SolicitudeUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final SolicitudeUseCase solicitudeUseCase;
    private final SolicitudeDtoMapper solicitudeMapper;
    private final Validator validator;

    public Mono<ServerResponse> listenGetAllSolicitudes(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitudeUseCase.getAllSolicitudes(), Solicitude.class);
    }

    public Mono<ServerResponse> listenCreateSolicitude(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateSolicitudeDto.class)
                .doOnNext(dto -> log.info("Received solicitude: {}", dto))
                .flatMap(this::validate)
                .map(solicitudeMapper::toResponse)
                .flatMap(solicitudeUseCase::saveSolicitude)
                .flatMap(saved -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }

    private Mono<CreateSolicitudeDto> validate(CreateSolicitudeDto dto) {
        Set<ConstraintViolation<CreateSolicitudeDto>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return Mono.error(new IllegalArgumentException(errors));
        }

        return Mono.just(dto);
    }
}
