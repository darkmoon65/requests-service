package com.crediya.requests.sqs.listener;

import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.sqs.listener.dto.LoanResponseDTO;
import com.crediya.requests.usecase.solicitude.SolicitudeUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SolicitudeUseCase solicitudeUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        System.out.println(message.body());
        return Mono.just(message.body())
                .map(body -> {
                    try {
                        return objectMapper.readValue(body, LoanResponseDTO.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error deserializando mensaje SQS", e);
                    }
                })
                .flatMap(response -> {
                    System.out.println("Estado id recibido: " + response.getStatus().getId());
                    Solicitude updatedSolicitude = new Solicitude();
                    updatedSolicitude.setId(response.getSolicitudeId());
                    updatedSolicitude.setStateId(response.getStatus().getId());
                    return solicitudeUseCase.updateSolicitude(updatedSolicitude, response.getPaymentSchedule());
                }).then();
    }
}
