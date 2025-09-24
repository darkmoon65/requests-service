package com.crediya.requests.sqs.sender.dto;
import com.crediya.requests.model.solicitude.dto.PaymentScheduleDTO;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SolicitudeStatusMessage {
    Integer solicitudeId;
    String email;
    Integer statusId;
    String timestamp;
    List<PaymentScheduleDTO> paymentSchedule;
}