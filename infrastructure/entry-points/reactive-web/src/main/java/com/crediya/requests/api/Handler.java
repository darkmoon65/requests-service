package com.crediya.requests.api;

import com.crediya.requests.api.dto.CreateSolicitudeDto;
import com.crediya.requests.api.dto.ExternalUserInfoDto;
import com.crediya.requests.api.dto.PaginatedResponse;
import com.crediya.requests.api.dto.SolicitudeResponseDto;
import com.crediya.requests.api.exception.TokenValidationException;
import com.crediya.requests.api.mapper.SolicitudeDtoMapper;
import com.crediya.requests.api.validation.SolicitudeValidator;
import com.crediya.requests.usecase.solicitude.SolicitudeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final SolicitudeUseCase solicitudeUseCase;
    private final SolicitudeDtoMapper solicitudeMapper;
    private final SolicitudeValidator solicitudeValidator;
    private final WebClient externalApiWebClient;

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

    public Mono<ServerResponse> listenGetPendingSolicitudes(ServerRequest serverRequest) {
        int page = Integer.parseInt(serverRequest.queryParam("page").orElse("1"));
        int size = Integer.parseInt(serverRequest.queryParam("size").orElse("10"));
        Integer loanTypeId = serverRequest.queryParam("loanTypeId")
                .map(Integer::parseInt)
                .orElse(null);

        Integer stateId = serverRequest.queryParam("stateId")
                .map(Integer::parseInt)
                .orElse(null);

        return extractToken(serverRequest)
                .flatMapMany(token -> solicitudeUseCase.getPendingSolicitudes(loanTypeId, stateId, page, size)
                .flatMap(solicitude ->
                        getUserInfo(solicitude.getEmail(), token)
                                .doOnNext(userInfoDto ->  log.info("looggg {}", userInfoDto))
                                .map(externalInfo -> {
                                    SolicitudeResponseDto dto = new SolicitudeResponseDto();
                                    dto.setId(solicitude.getId());
                                    dto.setAmount(solicitude.getAmount());
                                    dto.setTerm(solicitude.getTerm());
                                    dto.setEmail(solicitude.getEmail());
                                    dto.setStateName(solicitude.getStateName());
                                    dto.setLoanTypeName(solicitude.getLoanTypeName());
                                    dto.setNombreUsuario(externalInfo.getName());
                                    dto.setSalarioBase(externalInfo.getBaseSalary());
                                    return dto;
                                })
                ))
                .collectList()
                .flatMap(dtoList ->
                        solicitudeUseCase.countPendingSolicitudes(loanTypeId, stateId)
                                .map(totalElements -> {
                                    int totalPages = (int) Math.ceil((double) totalElements / size);

                                    PaginatedResponse<SolicitudeResponseDto> response = new PaginatedResponse<>();
                                    response.setContent(dtoList);
                                    response.setPage(page);
                                    response.setSize(size);
                                    response.setTotalElements(totalElements);
                                    response.setTotalPages(totalPages);
                                    response.setHasNext(page + 1 <= totalPages);
                                    response.setHasPrevious(page > 0);

                                    return response;
                                })
                )
                .flatMap(response ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)
                );
    }
    public Mono<ExternalUserInfoDto> getUserInfo(String email, String token) {
        return externalApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{email}")
                        .build(email))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(ExternalUserInfoDto.class)
                .doOnNext(user -> log.info("Respuesta API externa: {}", user))
                .onErrorResume(error -> {
                    return Mono.just(new ExternalUserInfoDto("Desconocido", BigDecimal.ZERO));
                });
    }

    public Mono<String> extractToken(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.headers()
                        .firstHeader(HttpHeaders.AUTHORIZATION))
                .map(authHeader -> authHeader.replace("Bearer ", ""))
                .doOnNext( tk -> log.info("tk {}", tk));
    }
}
