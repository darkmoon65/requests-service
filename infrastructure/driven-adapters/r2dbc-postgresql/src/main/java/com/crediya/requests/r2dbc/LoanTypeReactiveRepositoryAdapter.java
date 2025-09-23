package com.crediya.requests.r2dbc;

import com.crediya.requests.model.loantype.LoanType;
import com.crediya.requests.model.loantype.gateways.LoanTypeRepository;
import com.crediya.requests.r2dbc.entity.LoanTypeEntity;
import com.crediya.requests.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType, LoanTypeEntity,
        Integer, LoanTypeReactiveRepository
        > implements LoanTypeRepository {
    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class));
    }

    @Override
    public Mono<Boolean> existsById(Integer loanTypeId) {
        return super.repository.existsById(loanTypeId);
    }

    @Override
    public Mono<LoanType> findInterestRateById(Integer loanTypeId) {
        return super.findById(loanTypeId);
    }
    @Override
    public Mono<Boolean> hasAutomaticValidation(Integer loanTypeId) {
        return super.repository.hasAutomaticValidation(loanTypeId);
    }
}
