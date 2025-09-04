package com.crediya.requests.api.mapper;

import com.crediya.requests.api.dto.CreateSolicitudeDto;
import com.crediya.requests.model.solicitude.Solicitude;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SolicitudeDtoMapper {

    Solicitude toResponse(CreateSolicitudeDto dto);

    List<Solicitude> toResponseList(List<Solicitude> solicitude);

}
