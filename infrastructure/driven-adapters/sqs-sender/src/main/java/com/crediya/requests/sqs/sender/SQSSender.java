package com.crediya.requests.sqs.sender;

import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.dto.PaymentScheduleDTO;
import com.crediya.requests.model.solicitude.gateways.SolicitudeStatusNotifier;
import com.crediya.requests.sqs.sender.config.SQSSenderProperties;
import com.crediya.requests.model.solicitude.dto.CalculateDebtMessage;
import com.crediya.requests.model.solicitude.dto.LoanDetails;
import com.crediya.requests.sqs.sender.dto.SolicitudeStatusMessage;
import com.crediya.requests.model.solicitude.dto.UserConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SolicitudeStatusNotifier {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String queueName, String message) {
        return Mono.fromCallable(() -> buildRequest(queueName, message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String queueName, String message) {
        String queueUrl = resolveQueueUrl(queueName);
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }

    private String resolveQueueUrl(String queueName) {
        var url = properties.queueUrls().get(queueName);
        if (url == null) {
            throw new IllegalArgumentException("Queue not configured: " + queueName);
        }
        return url;
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
                .flatMap(msg -> this.send("notifications", msg))
                .doOnSuccess(msgId -> log.info("Mensaje enviado ID {}", msgId))
                .doOnError(err -> log.error("Error al enviar mensaje", err))
                .then();
    }

    @Override
    public Mono<Void> notifyLoanEvaluation(CalculateDebtMessage message) {
        return Mono.fromCallable(() -> {
                    return objectMapper.writeValueAsString(message);
                })
                .flatMap(msg -> this.send("debtCapacity", msg))
                .doOnSuccess(msgId -> log.info("Mensaje de evaluación enviado con ID {}", msgId))
                .doOnError(err -> log.error("Error al enviar mensaje de evaluación", err))
                .then();
    }

    @Override
    public Mono<Void> notifyStatusWithPaymentSchedule(Solicitude solicitude, List<PaymentScheduleDTO> paymentSchedule) {
        return Mono.fromCallable(() -> {
                    var message = SolicitudeStatusMessage.builder()
                            .solicitudeId(solicitude.getId())
                            .email(solicitude.getEmail())
                            .statusId(solicitude.getStateId())
                            .timestamp(java.time.Instant.now().toString())
                            .paymentSchedule(paymentSchedule)
                            .build();
                    return objectMapper.writeValueAsString(message);
                })
                .flatMap(msg -> this.send("notifications", msg))
                .doOnSuccess(msgId -> log.info("Mensaje enviado ID {}", msgId))
                .doOnError(err -> log.error("Error al enviar mensaje", err))
                .then();
    }
}
