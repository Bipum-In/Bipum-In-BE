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
import com.sparta.bipuminbe.common.s3.S3Uploader;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.dto.ReturnRequestResponseDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;

import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ReturnRequestService {
    private final RequestsRepository requestsRepository;
    private final SupplyRepository supplyRepository;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public ResponseDto<ReturnRequestResponseDto> getReturnRequest(Long requestId, User user) {
        Requests request = getRequests(requestId);
        checkReturnRequest(request, user);
        return ResponseDto.success(ReturnRequestResponseDto.of(request, user.getRole()));
    }

    @Transactional
    public ResponseDto<String> processingReturnRequest(Long requestId, AcceptResult acceptResult) {
        Requests request = getRequests(requestId);
        request.processingRequest(acceptResult);
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

    public ResponseDto<String> returnRequest(RequestsRequestDto requestsRequestDto, User user) throws IOException {
        Supply supply = supplyRepository.findById(requestsRequestDto.getSupplyId())
                .orElseThrow(() -> new CustomException(ErrorCode.NotFoundSupply));
        String image = s3Uploader.uploadFiles(requestsRequestDto.getMultipartFile(), "return-images");

        requestsRepository.save(Requests.builder()
                .content(requestsRequestDto.getContent())
                .requestType(requestsRequestDto.getRequestType())
                .requestStatus(RequestStatus.UNPROCESSED)
                .user(user)
                .supply(supply)
                .category(supply.getCategory())
                .image(image)
                .build());

        return ResponseDto.success("반납 요청 완료");
    }
}
