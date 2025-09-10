package com.crediya.requests.api;

import com.crediya.requests.api.dto.CreateSolicitudeDto;
import com.crediya.requests.api.exception.TokenValidationException;
import com.crediya.requests.api.mapper.SolicitudeDtoMapper;
import com.crediya.requests.api.validation.SolicitudeValidator;
import com.crediya.requests.usecase.solicitude.SolicitudeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final SolicitudeUseCase solicitudeUseCase;
    private final SolicitudeDtoMapper solicitudeMapper;
    private final SolicitudeValidator solicitudeValidator;

    @PreAuthorize("hasAuthority('APPLICANT')")
    public Mono<ServerResponse> listenCreateSolicitude(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateSolicitudeDto.class)
                .flatMap(dto -> serverRequest.principal()
                        .cast(Authentication.class)
                        .flatMap(auth -> {
                            String emailFromToken = auth.getName();
                            if (!emailFromToken.equalsIgnoreCase(dto.getEmail())) {
                                return Mono.error(new TokenValidationException("Email no coincide"));
                            }
                            return Mono.just(dto);
                        })
                )
                .doOnNext(dto -> log.info("Received solicitude: {}", dto))
                .flatMap(solicitudeValidator::validate)
                .map(solicitudeMapper::toResponse)
                .flatMap(solicitudeUseCase::saveSolicitude)
                .flatMap(saved -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }
}
