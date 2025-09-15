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
            ),
            @RouterOperation(
                    path = "/api/v1/solicitudes",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenGetPendingSolicitudes",
                    operation = @Operation(
                            operationId = "listenGetPendingSolicitudes",
                            summary = "Obtener solicitudes pendientes",
                            description = "Obtiene la lista de solicitudes pendientes con soporte para paginación y filtros opcionales.",
                            tags = {"Solicitudes"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "Authorization",
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER,
                                            required = true,
                                            description = "Token JWT, ejemplo: Bearer <token>"
                                    ),
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "page",
                                            description = "Número de página (empezando desde 0)",
                                            required = false,
                                            example = "0"
                                    ),
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "size",
                                            description = "Cantidad de elementos por página",
                                            required = false,
                                            example = "10"
                                    ),
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "loanTypeId",
                                            description = "Tipo de prestamo id",
                                            required = false,
                                            example = "3"
                                    ),
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "stateId",
                                            description = "Estado de la solicitud id",
                                            required = false,
                                            example = "1"
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de solicitudes paginada",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(
                                                            example = """
                                                        {
                                                          "content": [
                                                            {
                                                              "id": "06453344-0388-4ee0-af08-393200f3145d",
                                                              "amount": 1500.00,
                                                              "term": 12,
                                                              "email": "usuario@dominio.com",
                                                              "stateId": 1,
                                                              "loanTypeId": 2
                                                            }
                                                          ],
                                                          "page": 0,
                                                          "size": 5,
                                                          "totalElements": 1,
                                                          "totalPages": 1,
                                                          "hasNext": false,
                                                          "hasPrevious": false
                                                        }
                                                        """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Parámetros de paginación o filtros inválidos",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Token JWT inválido o ausente",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    )
                            }
                    ))
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/solicitud"), handler::listenCreateSolicitude)
                .andRoute(GET("/api/v1/solicitudes"), handler::listenGetPendingSolicitudes);
    }
}
