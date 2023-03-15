package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.requests.dto.RequestsResponseDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestsService {
    private final RequestsRepository requestsRepository;

    public ResponseDto<Page<RequestsResponseDto>> getRequests(String type, String status, int page) {
        RequestStatus requestStatus = RequestStatus.valueOf(status);
        Pageable pageable = getPageable(page);
        List<Requests> requestsList;

        if (type == null) {
            requestsList = requestsRepository.findByRequestStatusOrderByCreatedAtDesc(requestStatus, pageable);
        } else {
            RequestType requestType = RequestType.valueOf(type);
            requestsList = requestsRepository.
                    findByRequestTypeAndRequestStatusOrderByCreatedAtDesc(requestType, requestStatus, pageable);
        }

        List<RequestsResponseDto> requestsDtoList = converToDto(requestsList);

        return ResponseDto.success(new PageImpl<>(requestsDtoList, pageable, requestsDtoList.size()));
    }

    private Pageable getPageable(int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(page - 1, 10, sort);
    }

    private List<RequestsResponseDto> converToDto(List<Requests> requestsList) {
        List<RequestsResponseDto> requestsDtoList = new ArrayList<>();
        for (Requests requests : requestsList) {
            requestsDtoList.add(RequestsResponseDto.of(requests));
        }
        return requestsDtoList;
    }

}
