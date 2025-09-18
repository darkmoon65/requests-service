package com.crediya.requests.sqs.sender;

import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.gateways.SolicitudeStatusNotifier;
import com.crediya.requests.sqs.sender.config.SQSSenderProperties;
import com.crediya.requests.sqs.sender.dto.SolicitudeStatusMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SolicitudeStatusNotifier {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> notifyStatusChanged(Solicitude solicitude) {
        return Mono.fromCallable(() -> {
                    var message = SolicitudeStatusMessage.builder()
                            .solicitudeId(solicitude.getId())
                            .email(solicitude.getEmail())
                            .statusId(solicitude.getStateId())
                            .timestamp(java.time.Instant.now().toString())
                            .build();
                    return objectMapper.writeValueAsString(message);
                })
                .flatMap(this::send)
                .doOnSuccess(msgId -> log.info("Mensaje enviado ID {}", msgId))
                .doOnError(err -> log.error("Error al enviar mensaje", err))
                .then();
    }
}
