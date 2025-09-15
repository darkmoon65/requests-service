package com.crediya.requests.api.mapper;

import com.crediya.requests.api.dto.ExternalUserInfoDto;
import com.crediya.requests.api.dto.SolicitudeResponseDto;
import com.crediya.requests.model.solicitude.dto.SolicitudeWithNamesDto;
import org.springframework.stereotype.Component;

@Component
public class SolicitudeGetMapper {

    public SolicitudeResponseDto toDto(SolicitudeWithNamesDto solicitude, ExternalUserInfoDto externalInfo) {
        SolicitudeResponseDto dto = new SolicitudeResponseDto();
        dto.setId(solicitude.getId());
        dto.setAmount(solicitude.getAmount());
        dto.setTerm(solicitude.getTerm());
        dto.setEmail(solicitude.getEmail());
        dto.setStateName(solicitude.getStateName());
        dto.setLoanTypeName(solicitude.getLoanTypeName());
        dto.setNameUser(externalInfo.getName());
        dto.setBaseSalary(externalInfo.getBaseSalary());
        return dto;
    }
}
