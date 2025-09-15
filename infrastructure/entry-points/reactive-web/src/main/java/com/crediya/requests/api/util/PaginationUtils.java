package com.crediya.requests.api.util;

import com.crediya.requests.api.dto.PaginatedResponse;
import com.crediya.requests.api.dto.SolicitudeResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginationUtils {

    public PaginatedResponse<SolicitudeResponseDto> buildPaginatedResponse(List<SolicitudeResponseDto> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);

        PaginatedResponse<SolicitudeResponseDto> response = new PaginatedResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setHasNext(page < totalPages);
        response.setHasPrevious(page > 1);
        return response;
    }

}
