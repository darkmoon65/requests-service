package com.crediya.requests.model.solicitude.gateways;

import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.dto.CalculateDebtMessage;
import com.crediya.requests.model.solicitude.dto.PaymentScheduleDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudeStatusNotifier {
    Mono<Void> notifyStatusChanged(Solicitude solicitude);
    Mono<Void> notifyLoanEvaluation(CalculateDebtMessage message);
    Mono<Void> notifyStatusWithPaymentSchedule(Solicitude solicitude, List<PaymentScheduleDTO> paymentSchedule);
}
