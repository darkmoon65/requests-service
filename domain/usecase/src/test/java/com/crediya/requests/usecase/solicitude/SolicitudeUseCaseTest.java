package com.crediya.requests.usecase.solicitude;

import com.crediya.requests.model.loantype.LoanType;
import com.crediya.requests.model.loantype.gateways.LoanTypeRepository;
import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.dto.CalculateDebtMessage;
import com.crediya.requests.model.solicitude.dto.PaymentScheduleDTO;
import com.crediya.requests.model.solicitude.dto.SolicitudeWithNamesDto;
import com.crediya.requests.model.solicitude.gateways.SolicitudeRepository;
import com.crediya.requests.model.solicitude.gateways.SolicitudeStatusNotifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SolicitudeUseCaseTest {

    @InjectMocks
    SolicitudeUseCase solicitudeUseCase;

    @Mock
    SolicitudeRepository solicitudeRepository;

    @Mock
    LoanTypeRepository loanTypeRepository;

    @Mock
    SolicitudeStatusNotifier solicitudeStatusNotifier;

    @Test
    void saveSolicitude_ok() {
        Solicitude solTest = new Solicitude();
        solTest.setAmount(BigDecimal.valueOf(5000));
        solTest.setTerm(30);
        solTest.setEmail("test@example.com");
        solTest.setLoanTypeId(2);

        when(loanTypeRepository.existsById(anyInt())).thenReturn(Mono.just(true));
        when(solicitudeRepository.saveSolicitude(any(Solicitude.class))).thenReturn(Mono.just(solTest));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(solTest))
                .assertNext(saved -> assertThat(saved.getLoanTypeId()).isEqualTo(2))
                .verifyComplete();
    }

    @Test
    void saveSolicitude_invalidLoanTypeId() {
        Solicitude solTest = new Solicitude();
        solTest.setAmount(BigDecimal.valueOf(5000));
        solTest.setTerm(30);
        solTest.setEmail("test@example.com");
        solTest.setLoanTypeId(999);

        when(loanTypeRepository.existsById(anyInt())).thenReturn(Mono.just(false));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(solTest))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("Id tipo de prestamo invalido: 999"))
                .verify();
    }

    @Test
    void getPendingSolicitudes_ok() {
        int loanTypeId = 2;
        int stateId = 1;
        int page = 1;
        int size = 2;
        int offset = 0;

        SolicitudeWithNamesDto dto1 = new SolicitudeWithNamesDto(1, BigDecimal.valueOf(5000), 30,
                "test1@example.com", "Pendiente", "Prestamo Personal");

        SolicitudeWithNamesDto dto2 = new SolicitudeWithNamesDto(2, BigDecimal.valueOf(8000), 60,
                "test2@example.com", "Pendiente", "Prestamo Familiar");

        when(solicitudeRepository.findPendingSolicitudes(eq(loanTypeId), eq(stateId), eq(size), eq(offset)))
                .thenReturn(Flux.just(dto1, dto2));

        StepVerifier.create(solicitudeUseCase.getPendingSolicitudes(loanTypeId, stateId, page, size))
                .expectNext(dto1)
                .expectNext(dto2)
                .verifyComplete();
    }

    @Test
    void getPendingSolicitudes_emptyResult() {
        int loanTypeId = 2;
        int stateId = 1;
        int page = 1;
        int size = 2;
        int offset = 0;

        when(solicitudeRepository.findPendingSolicitudes(eq(loanTypeId), eq(stateId), eq(size), eq(offset)))
                .thenReturn(Flux.empty());

        StepVerifier.create(solicitudeUseCase.getPendingSolicitudes(loanTypeId, stateId, page, size))
                .verifyComplete();
    }

    @Test
    void countPendingSolicitudes_ok() {
        int loanTypeId = 2;
        int stateId = 1;
        long expectedCount = 5L;

        when(solicitudeRepository.countByStateId(eq(loanTypeId), eq(stateId)))
                .thenReturn(Mono.just(expectedCount));

        StepVerifier.create(solicitudeUseCase.countPendingSolicitudes(loanTypeId, stateId))
                .assertNext(count -> assertThat(count).isEqualTo(expectedCount))
                .verifyComplete();
    }

    @Test
    void countPendingSolicitudes_emptyResult() {
        int loanTypeId = 2;
        int stateId = 1;

        when(solicitudeRepository.countByStateId(eq(loanTypeId), eq(stateId)))
                .thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.countPendingSolicitudes(loanTypeId, stateId))
                .verifyComplete();
    }

    @Test
    void updateSolicitude_ok() {
        Solicitude existing = new Solicitude();
        existing.setId(1);
        existing.setStateId(1);

        Solicitude updateRequest = new Solicitude();
        updateRequest.setId(1);
        updateRequest.setStateId(2);

        Solicitude updated = new Solicitude();
        updated.setId(1);
        updated.setStateId(2);

        when(solicitudeRepository.getById(eq(1))).thenReturn(Mono.just(existing));
        when(solicitudeRepository.saveSolicitude(any(Solicitude.class))).thenReturn(Mono.just(updated));
        when(solicitudeStatusNotifier.notifyStatusChanged(any(Solicitude.class))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.updateSolicitudeManual(updateRequest))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(1);
                    assertThat(saved.getStateId()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void updateSolicitude_notFound() {
        Solicitude updateRequest = new Solicitude();
        updateRequest.setId(777);
        updateRequest.setStateId(2);

        when(solicitudeRepository.getById(eq(777))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.updateSolicitudeManual(updateRequest))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("No existe Solicitud con ese id"))
                .verify();
    }

    @Test
    void updateSolicitude_withPaymentSchedule() {
        Solicitude existing = new Solicitude();
        existing.setId(1);
        existing.setStateId(1);

        Solicitude updateRequest = new Solicitude();
        updateRequest.setId(1);
        updateRequest.setStateId(2);

        List<PaymentScheduleDTO> schedule = List.of(
                new PaymentScheduleDTO(1, BigDecimal.valueOf(100), BigDecimal.valueOf(50),
                        BigDecimal.valueOf(50), BigDecimal.valueOf(500))
        );

        Solicitude updated = new Solicitude();
        updated.setId(1);
        updated.setStateId(2);

        when(solicitudeRepository.getById(eq(1))).thenReturn(Mono.just(existing));
        when(solicitudeRepository.saveSolicitude(any(Solicitude.class))).thenReturn(Mono.just(updated));
        when(solicitudeStatusNotifier.notifyStatusWithPaymentSchedule(eq(updated), eq(schedule))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.updateSolicitude(updateRequest, schedule))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(1);
                    assertThat(saved.getStateId()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void updateSolicitude_withoutPaymentSchedule() {
        Solicitude existing = new Solicitude();
        existing.setId(1);
        existing.setStateId(1);

        Solicitude updateRequest = new Solicitude();
        updateRequest.setId(1);
        updateRequest.setStateId(3);

        Solicitude updated = new Solicitude();
        updated.setId(1);
        updated.setStateId(3);

        when(solicitudeRepository.getById(eq(1))).thenReturn(Mono.just(existing));
        when(solicitudeRepository.saveSolicitude(any(Solicitude.class))).thenReturn(Mono.just(updated));
        when(solicitudeStatusNotifier.notifyStatusChanged(eq(updated))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.updateSolicitude(updateRequest, null))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(1);
                    assertThat(saved.getStateId()).isEqualTo(3);
                })
                .verifyComplete();
    }


    @Test
    void processLoanEvaluation_withAutomaticValidation() {
        Solicitude solicitude = new Solicitude();
        solicitude.setId(1);
        solicitude.setLoanTypeId(2);
        solicitude.setAmount(BigDecimal.valueOf(1000));
        solicitude.setTerm(12);
        solicitude.setEmail("test@example.com");

        LoanType loanTypeDto = new LoanType();
        loanTypeDto.setInterestRate(BigDecimal.valueOf(16.0));

        when(loanTypeRepository.findInterestRateById(eq(2))).thenReturn(Mono.just(loanTypeDto));
        when(loanTypeRepository.hasAutomaticValidation(eq(2))).thenReturn(Mono.just(true));
        when(solicitudeRepository.findAllApprovedLoansByEmail(eq("test@example.com"))).thenReturn(Flux.empty());
        when(solicitudeStatusNotifier.notifyLoanEvaluation(any(CalculateDebtMessage.class))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.processLoanEvaluation(solicitude))
                .verifyComplete();
    }

    @Test
    void processLoanEvaluation_withoutAutomaticValidation() {
        Solicitude solicitude = new Solicitude();
        solicitude.setId(1);
        solicitude.setLoanTypeId(2);
        solicitude.setAmount(BigDecimal.valueOf(1000));
        solicitude.setTerm(12);
        solicitude.setEmail("test@example.com");

        LoanType loanTypeDto = new LoanType();
        loanTypeDto.setInterestRate(BigDecimal.valueOf(16.0));

        when(loanTypeRepository.findInterestRateById(eq(2))).thenReturn(Mono.just(loanTypeDto));
        when(loanTypeRepository.hasAutomaticValidation(eq(2))).thenReturn(Mono.just(false));

        StepVerifier.create(solicitudeUseCase.processLoanEvaluation(solicitude))
                .verifyComplete();
    }

}
