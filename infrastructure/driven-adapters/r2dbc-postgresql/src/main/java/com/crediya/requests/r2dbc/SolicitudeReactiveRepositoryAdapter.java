package com.crediya.requests.r2dbc;

import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.dto.SolicitudeWithNamesDto;
import com.crediya.requests.model.solicitude.gateways.SolicitudeRepository;
import com.crediya.requests.r2dbc.entity.SolicitudeEntity;
import com.crediya.requests.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class SolicitudeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Solicitude,SolicitudeEntity,
    Integer,SolicitudeReactiveRepository
> implements SolicitudeRepository {
    public SolicitudeReactiveRepositoryAdapter(SolicitudeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Solicitude.class));
    }

    @Override
    public Mono<Solicitude> getById(Integer id){
        return super.findById(id);
    }

    @Override
    public Mono<Solicitude> saveSolicitude(Solicitude solicitude) {
        return super.save(solicitude);
    }

    @Override
    public Flux<SolicitudeWithNamesDto> findPendingSolicitudes(int loanTypeId, int stateId, int limit, int offset) {
        return repository.findPendingSolicitudes(loanTypeId, stateId, limit, offset)
                .map(entity -> mapper.map(entity, SolicitudeWithNamesDto.class));
    }

    @Override
    public Mono<Long> countByStateId(int loanTypeId, int stateId) {
        return repository.countByStateId(loanTypeId, stateId);
    }

    @Override
    public Flux<Solicitude> findAllApprovedLoansByEmail(String email) {
        return repository.findAllApprovedLoansByEmail(email);
    }
}
