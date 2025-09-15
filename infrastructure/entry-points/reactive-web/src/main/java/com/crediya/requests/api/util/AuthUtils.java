package com.crediya.requests.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthUtils {

    public Mono<String> extractToken(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.headers()
                        .firstHeader(HttpHeaders.AUTHORIZATION))
                .map(authHeader -> authHeader.replace("Bearer ", ""))
                .doOnNext( tk -> log.info("Token: {}", tk));
    }
}
