package com.crediya.requests.sqs.sender.dto;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SolicitudeStatusMessage {
    Integer solicitudeId;
    String email;
    Integer statusId;
    String timestamp;
}