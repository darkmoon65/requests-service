package com.crediya.requests.usecase.solicitude;

import com.crediya.requests.model.loantype.gateways.LoanTypeRepository;
import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.dto.*;
import com.crediya.requests.model.solicitude.gateways.SolicitudeRepository;
import com.crediya.requests.model.solicitude.gateways.SolicitudeStatusNotifier;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.List;


@RequiredArgsConstructor
public class SolicitudeUseCase {
    private final SolicitudeRepository solicitudeRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final SolicitudeStatusNotifier solicitudeStatusNotifier;

    public Mono<Solicitude> saveSolicitude(Solicitude solicitude) {
        return loanTypeRepository.existsById(solicitude.getLoanTypeId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Id tipo de prestamo invalido: "
                                + solicitude.getLoanTypeId()));
                    }
                    solicitude.setStateId(1);
                    return solicitudeRepository.saveSolicitude(solicitude);
                });
    }

    public Flux<SolicitudeWithNamesDto> getPendingSolicitudes(int loanTypeId, int stateId, int page, int size) {
        int offset = (page - 1) * size;
        return solicitudeRepository.findPendingSolicitudes(loanTypeId, stateId, size, offset);
    }

    public Mono<Long> countPendingSolicitudes(int loanTypeId, int stateId) {
        return solicitudeRepository.countByStateId(loanTypeId, stateId);
    }

    public Mono<Solicitude> updateSolicitudeManual(Solicitude solicitude) {
        return solicitudeRepository.getById(solicitude.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No existe Solicitud con ese id")))
                .map(existSol -> {
                    existSol.setStateId(solicitude.getStateId());
                    return existSol;
                })
                .flatMap(solicitudeRepository::saveSolicitude)
                .flatMap(saved -> solicitudeStatusNotifier.notifyStatusChanged(saved)
                        .thenReturn(saved));
    }

    public Mono<Solicitude> updateSolicitude(Solicitude solicitude, List<PaymentScheduleDTO> paymentSchedule) {
            return solicitudeRepository.getById(solicitude.getId())
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("No existe Solicitud con ese id")))
                    .map(existSol -> {
                        existSol.setStateId(solicitude.getStateId());
                        return existSol;
                    })
                    .flatMap(solicitudeRepository::saveSolicitude)
                    .flatMap(saved -> {
                        Mono<Void> notifyMono;
                        if (paymentSchedule != null && !paymentSchedule.isEmpty() &&
                                LoanStatus.APROBADO.getId().equals(saved.getStateId())) {
                            notifyMono = solicitudeStatusNotifier.notifyStatusWithPaymentSchedule(saved, paymentSchedule);

                        } else {
                            notifyMono = solicitudeStatusNotifier.notifyStatusChanged(saved);
                        }
                        return notifyMono.thenReturn(saved);
                    });
    }

    public Mono<Void> processLoanEvaluation(Solicitude solicitude) {
        return loanTypeRepository.findInterestRateById(solicitude.getLoanTypeId())
                .flatMap(loanTyoe ->
                        loanTypeRepository.hasAutomaticValidation(solicitude.getLoanTypeId())
                                .flatMap(hasValidation -> {
                                    if (Boolean.TRUE.equals(hasValidation)) {
                                        return solicitudeRepository.findAllApprovedLoansByEmail(solicitude.getEmail())
                                                .collectList()
                                                .flatMap(approvedLoansList -> {
                                                    System.out.println(approvedLoansList);
                                                    var userConsumer = new UserConsumer(
                                                            BigDecimal.valueOf(5000)
                                                    );

                                                    var newLoan = new LoanDetails(
                                                            solicitude.getId(),
                                                            solicitude.getAmount(),
                                                            solicitude.getTerm()
                                                    );

                                                    var message = new CalculateDebtMessage(
                                                            loanTyoe.getInterestRate(),
                                                            userConsumer,
                                                            approvedLoansList,
                                                            newLoan
                                                    );

                                                    return solicitudeStatusNotifier.notifyLoanEvaluation(message);
                                                });
                                    }
                                    return Mono.empty();
                                })
                );

    }


}
