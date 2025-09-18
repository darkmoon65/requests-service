package com.crediya.requests.r2dbc;

import com.crediya.requests.model.solicitude.dto.SolicitudeWithNamesDto;
import com.crediya.requests.r2dbc.entity.SolicitudeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudeReactiveRepository extends ReactiveCrudRepository<SolicitudeEntity, Integer>, ReactiveQueryByExampleExecutor<SolicitudeEntity> {
    @Query("""
    SELECT s.id, s.amount, s.term, s.email,
              st.name AS state_name,
              lt.name AS loan_type_name
       FROM solicitude s
       INNER JOIN state st ON s.state_id = st.id
       INNER JOIN loan_type lt ON s.loan_type_id = lt.id
       WHERE (s.loan_type_id = :loanTypeId)
        AND (s.state_id = :stateId)
       ORDER BY s.id DESC
       LIMIT :limit OFFSET :offset
    """)
    Flux<SolicitudeWithNamesDto> findPendingSolicitudes(int loanTypeId, int stateId, int limit, int offset);


    @Query("""
        SELECT COUNT(*)
        FROM solicitude s
        WHERE ( s.loan_type_id = :loanTypeId)
          AND ( s.state_id = :stateId)
        """)
    Mono<Long> countByStateId(int loanTypeId , int stateId);
}
