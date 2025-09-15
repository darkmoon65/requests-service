package com.crediya.requests.security.config;

import com.crediya.requests.security.jwt.JwtFilter;
import com.crediya.requests.security.repository.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final SecurityContextRepository securityContextRepository;

    public SecurityConfig(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    private static final String[] ValidList = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/v1/solicitudes"
    };

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, JwtFilter jwtFilter){
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchangeSpec
                        -> exchangeSpec
                        .pathMatchers(ValidList).permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(
                        exceptions -> exceptions
                                .authenticationEntryPoint(authenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler()))
                .addFilterAfter(jwtFilter,  SecurityWebFiltersOrder.FIRST)
                .securityContextRepository(securityContextRepository)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .build();
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        };
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }
}
