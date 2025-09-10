package com.crediya.requests.api;

import com.crediya.requests.api.dto.CreateSolicitudeDto;
import com.crediya.requests.api.exception.ErrorResponse;
import com.crediya.requests.model.solicitude.Solicitude;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean

    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenCreateSolicitude",
                    operation = @Operation(
                            operationId = "listenCreateSolicitude",
                            summary = "Registrar nueva solicitud",
                            description = "Recibe un objeto Solicitude y guarda la solicitud.",
                            tags = {"Solicitudes"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "Authorization",
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER,
                                            required = true,
                                            description = "Token JWT, ejemplo: Bearer <token>"
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la solicitud a registrar",
                                    content = @Content(schema = @Schema(implementation = CreateSolicitudeDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Solicitud registrada correctamente",
                                            content = @Content(schema = @Schema(implementation = Solicitude.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Error tipo de prestamo invalido",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/solicitud"), handler::listenCreateSolicitude);
    }
}
