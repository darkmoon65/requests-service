package com.crediya.requests.r2dbc;


import com.crediya.requests.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity, Integer>, ReactiveQueryByExampleExecutor<LoanTypeEntity> {
    @Query("SELECT auto_validation FROM loan_type WHERE id = :loanTypeId")
    Mono<Boolean> hasAutomaticValidation(@Param("loanTypeId") Integer loanTypeId);
}