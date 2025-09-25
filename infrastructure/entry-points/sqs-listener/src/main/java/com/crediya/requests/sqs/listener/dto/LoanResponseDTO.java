package com.crediya.requests.sqs.listener.dto;

import com.crediya.requests.model.solicitude.dto.LoanStatus;
import com.crediya.requests.model.solicitude.dto.PaymentScheduleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
    private Integer solicitudeId;
    private LoanStatus status;
    private List<PaymentScheduleDTO> paymentSchedule;
}