package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.requests.dto.RequestsDetailsResponseDto;
import com.sparta.bipuminbe.requests.dto.RequestsProcessRequestDto;
import com.sparta.bipuminbe.requests.dto.RequestsPageResponseDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RequestsService {
    private final RequestsRepository requestsRepository;
    private final SupplyRepository supplyRepository;

    @Transactional(readOnly = true)
    public ResponseDto<Page<RequestsPageResponseDto>> getRequestsPage(String keyword, String type, String status, int page, int size) {
        Set<RequestType> requestTypeQuery = getTypeSet(type);
        Set<RequestStatus> requestStatusQuery = getStatusSet(status);
        Pageable pageable = getPageable(page, size);
        Page<Requests> requestsList = requestsRepository.
                getRequestsList("%" + keyword + "%", requestTypeQuery, requestStatusQuery, pageable);

        List<RequestsPageResponseDto> requestsDtoList = convertToDto(requestsList.getContent());

        return ResponseDto.success(new PageImpl<>(requestsDtoList, requestsList.getPageable(), requestsList.getTotalElements()));
    }

    // list 추출 조건용 requestType Set 리스트.
    private Set<RequestType> getTypeSet(String type) {
        Set<RequestType> requestTypeQuery = new HashSet<>();
        if (type.equals("ALL")) {
            requestTypeQuery.addAll(List.of(RequestType.values()));
        } else {
            requestTypeQuery.add(RequestType.valueOf(type));
        }
        return requestTypeQuery;
    }

    // list 추출 조건용 requestStatus Set 리스트.
    private Set<RequestStatus> getStatusSet(String status) {
        Set<RequestStatus> requestStatusQuery = new HashSet<>();
        if (status.equals("ALL")) {
            requestStatusQuery.addAll(List.of(RequestStatus.values()));
        } else {
            requestStatusQuery.add(RequestStatus.valueOf(status));
        }
        return requestStatusQuery;
    }

    private Pageable getPageable(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(page - 1, size, sort);
    }

    private List<RequestsPageResponseDto> convertToDto(List<Requests> requestsList) {
        List<RequestsPageResponseDto> requestsDtoList = new ArrayList<>();
        for (Requests requests : requestsList) {
            requestsDtoList.add(RequestsPageResponseDto.of(requests));
        }
        return requestsDtoList;
    }

    @Transactional(readOnly = true)
    public ResponseDto<RequestsDetailsResponseDto> getRequestsDetails(Long requestId, User user) {
        Requests request = getRequest(requestId);
        checkPermission(request, user);
        return ResponseDto.success(RequestsDetailsResponseDto.of(request, user.getRole()));
    }

    // 해당 요청을 볼 권한 확인.
    private void checkPermission(Requests request, User user) {
        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !request.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NoPermission);
        }
    }

    private Requests getRequest(Long requestId) {
        return requestsRepository.findById(requestId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest));
    }

    @Transactional
    public ResponseDto<String> processingRequests(RequestsProcessRequestDto requestsProcessRequestDto) {
        Requests request = getRequest(requestsProcessRequestDto.getRequestId());
        AcceptResult acceptResult = AcceptResult.valueOf(requestsProcessRequestDto.getAcceptResult());
        checkAcceptResult(acceptResult, request.getRequestType());
        request.processingRequest(acceptResult, requestsProcessRequestDto.getComment());

        String message = request.getRequestType().getKorean();
        if (acceptResult.equals(AcceptResult.DECLINE)) {
            checkNullComment(requestsProcessRequestDto.getComment());
            return ResponseDto.success(message + "거절 완료.");
        }

        Supply supply = request.getSupply();

        if (acceptResult.equals(AcceptResult.DISPOSE)) {
            supplyRepository.delete(supply);
            return ResponseDto.success("비품 폐기 처리 완료.");
        }

        if (request.getRequestType().equals(RequestType.SUPPLY)) {
            checkSupplyId(requestsProcessRequestDto.getSupplyId());
            supply = getSupply(requestsProcessRequestDto.getSupplyId());
            supply.allocateSupply(request.getUser());
        } else if (request.getRequestType().equals(RequestType.REPAIR)) {
            supply.repairSupply();
        } else if (request.getRequestType().equals(RequestType.RETURN)) {
            supply.returnSupply();
        }

        return ResponseDto.success(message + "처리 완료.");
    }

    // 거절시 거절 사유 작성은 필수다.
    private void checkNullComment(String comment) {
        if (comment == null || comment.equals("")) {
            throw new CustomException(ErrorCode.NullComment);
        }
    }

    // 폐기는 수리 요청에만 존재해야 한다.
    private void checkAcceptResult(AcceptResult acceptResult, RequestType requestType) {
        if (acceptResult.equals(AcceptResult.DISPOSE) && !requestType.equals(RequestType.REPAIR)) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }
    }

    // 비품 요청에는 supplyId도 같이 넘겨줘야 한다.
    private void checkSupplyId(Long supplyId) {
        if (supplyId == null) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }
    }

    private Supply getSupply(Long supplyId) {
        return supplyRepository.findById(supplyId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundSupply));
    }
}
