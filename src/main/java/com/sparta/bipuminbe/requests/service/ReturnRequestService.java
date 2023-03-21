package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.requests.dto.ReturnRequestResponseDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReturnRequestService {
    private final RequestsRepository requestsRepository;

    @Transactional(readOnly = true)
    public ResponseDto<ReturnRequestResponseDto> getReturnRequest(Long requestId, User user) {
        Requests request = getRequests(requestId);
        checkReturnRequest(request, user);
        return ResponseDto.success(ReturnRequestResponseDto.of(request, user.getRole()));
    }

    @Transactional
    public ResponseDto<String> processingReturnRequest(Long requestId, AcceptResult acceptResult) {
        Requests request = getRequests(requestId);
        request.processingRequest(acceptResult, "");
        if (acceptResult.equals(AcceptResult.DECLINE)) {
            return ResponseDto.success("승인 거부 완료.");
        }
        request.getSupply().returnSupply();
        return ResponseDto.success("승인 처리 완료.");
    }

    private void checkReturnRequest(Requests request, User user) {
        if (!request.getRequestType().equals(RequestType.RETURN)) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }

        if (!request.getUser().getId().equals(user.getId()) && !user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NoPermission);
        }
    }

    private Requests getRequests(Long requestId) {
        return requestsRepository.findById(requestId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest));
    }
}
