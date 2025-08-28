package com.crediya.requests.r2dbc;

import com.crediya.requests.r2dbc.entity.SolicitudeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SolicitudeReactiveRepository extends ReactiveCrudRepository<SolicitudeEntity, String>, ReactiveQueryByExampleExecutor<SolicitudeEntity> {

}
