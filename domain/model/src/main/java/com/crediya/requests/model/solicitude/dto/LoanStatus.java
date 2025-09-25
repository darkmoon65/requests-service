package com.crediya.requests.model.solicitude.dto;

import lombok.Getter;

@Getter
public enum LoanStatus {
    PENDIENTE(1),
    APROBADO(2),
    RECHAZADO(3),
    MANUAL(4);

    private final Integer id;

    LoanStatus(Integer id) {
        this.id = id;
    }

    public static LoanStatus fromId(Integer id) {
        for (LoanStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid LoanStatus id: " + id);
    }
}
