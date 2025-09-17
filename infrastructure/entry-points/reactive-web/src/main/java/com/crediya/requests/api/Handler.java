package com.crediya.requests.api;

import com.crediya.requests.api.client.ExternalClient;
import com.crediya.requests.api.dto.CreateSolicitudeDto;
import com.crediya.requests.api.dto.UpdateSolicitudeDto;
import com.crediya.requests.api.exception.TokenValidationException;
import com.crediya.requests.api.mapper.SolicitudeDtoMapper;
import com.crediya.requests.api.mapper.SolicitudeGetMapper;
import com.crediya.requests.api.util.AuthUtils;
import com.crediya.requests.api.util.PaginationUtils;
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
    private final SolicitudeDtoMapper solicitudeCreateMapper;
    private final SolicitudeValidator solicitudeValidator;
    private final SolicitudeGetMapper solicitudeGetMapper;
    private final PaginationUtils paginationUtils;
    private final AuthUtils authUtils;
    private final ExternalClient externalClient;

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
                .map(solicitudeCreateMapper::toResponse)
                .flatMap(solicitudeUseCase::saveSolicitude)
                .flatMap(saved -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ServerResponse> listenUpdateSolicitude(ServerRequest serverRequest){
        return serverRequest.bodyToMono(UpdateSolicitudeDto.class)
                .doOnNext(dto -> log.info("Received update solicitude: {}", dto))
                .flatMap(solicitudeValidator::validate)
                .map(solicitudeCreateMapper::toEntity)
                .flatMap(solicitudeUseCase::updateSolicitude)
                .flatMap(saved -> ServerResponse.status(200)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }

    public Mono<ServerResponse> listenGetPendingSolicitudes(ServerRequest serverRequest) {
        int page = Integer.parseInt(serverRequest.queryParam("page").orElse("1"));
        int size = Integer.parseInt(serverRequest.queryParam("size").orElse("10"));
        int loanTypeId = Integer.parseInt(serverRequest.queryParam("loanTypeId").orElse("3"));
        int stateId = Integer.parseInt(serverRequest.queryParam("stateId").orElse("1"));

        return authUtils.extractToken(serverRequest)
                .flatMapMany(token -> solicitudeUseCase.getPendingSolicitudes(loanTypeId, stateId, page, size)
                .flatMap(solicitude ->
                        externalClient.getUserInfo(solicitude.getEmail(), token)
                                .doOnNext(userInfoDto ->  log.info("User Info Dto {}", userInfoDto))
                                .map(externalInfo -> solicitudeGetMapper.toDto(solicitude, externalInfo))
                ))
                .collectList()
                .flatMap(dtoList ->
                        solicitudeUseCase.countPendingSolicitudes(loanTypeId, stateId)
                                .map(totalElements -> paginationUtils.buildPaginatedResponse(dtoList, page, size, totalElements))
                )
                .flatMap(response ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)
                );
    }
}
