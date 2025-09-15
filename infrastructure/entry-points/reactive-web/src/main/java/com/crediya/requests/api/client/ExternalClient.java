package com.crediya.requests.api.client;

import com.crediya.requests.api.dto.ExternalUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalClient {

    private final WebClient externalApiWebClient;

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

}
