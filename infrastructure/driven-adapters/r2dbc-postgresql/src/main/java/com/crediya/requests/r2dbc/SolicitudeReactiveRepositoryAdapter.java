package com.crediya.requests.r2dbc;

import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.gateways.SolicitudeRepository;
import com.crediya.requests.r2dbc.entity.SolicitudeEntity;
import com.crediya.requests.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class SolicitudeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Solicitude,SolicitudeEntity,
    String,SolicitudeReactiveRepository
> implements SolicitudeRepository {
    public SolicitudeReactiveRepositoryAdapter(SolicitudeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Solicitude.class));
    }

    @Override
    public Mono<Solicitude> saveSolicitude(Solicitude solicitude) {
        return super.save(solicitude);
    }

    @Override
    public Flux<Solicitude> getAllSolicitudes() {
        return super.findAll();
    }
}
