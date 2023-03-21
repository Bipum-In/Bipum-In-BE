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
import com.sparta.bipuminbe.requests.dto.RepairRequestResponseDto;

import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RepairRequestService {
    private final RequestsRepository requestsRepository;
    private final SupplyRepository supplyRepository;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public ResponseDto<RepairRequestResponseDto> getRepairRequest(Long requestId, User user) {
        Requests request = getRequest(requestId);
        checkRepairRequest(request, user);
        return ResponseDto.success(RepairRequestResponseDto.of(request, user.getRole()));
    }

    @Transactional
    public ResponseDto<String> processingRepairRequest(Long requestId, AcceptResult acceptResult) {
        Requests request = getRequest(requestId);
        request.processingRequest(acceptResult, "");
        if (acceptResult.equals(AcceptResult.DECLINE)) {
            return ResponseDto.success("승인 거부 완료.");
        }
        Supply supply = request.getSupply();
        if (acceptResult.equals(AcceptResult.DISPOSE)) {
            supplyRepository.delete(supply);
            return ResponseDto.success("비품 폐기 처리 완료.");
        }
        supply.repairSupply();
        return ResponseDto.success("승인 처리 완료.");
    }

//    void readRequest(Requests request) {
//        if (!request.getIsRead()) {
//            request.read();
//        }
//    }

    public ResponseDto<String> repairRequest(RequestsRequestDto requestsRequestDto, User user) throws IOException {
        Supply supply = supplyRepository.findById(requestsRequestDto.getSupplyId())
                .orElseThrow(() -> new CustomException(ErrorCode.NotFoundSupply));
        String image = s3Uploader.uploadFiles(requestsRequestDto.getMultipartFile(), "repair-images");

        requestsRepository.save(Requests.builder()
                .content(requestsRequestDto.getContent())
                .requestType(requestsRequestDto.getRequestType())
                .requestStatus(RequestStatus.UNPROCESSED)
                .user(user)
                .supply(supply)
                .category(supply.getCategory())
                .image(image)
                .build());

        return ResponseDto.success("수리 요청 성공");
    }

    private void checkRepairRequest(Requests request, User user) {
        if (!request.getRequestType().equals(RequestType.REPAIR)) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }

        if (!request.getUser().getId().equals(user.getId()) && !user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NoPermission);
        }
    }

    private Requests getRequest(Long requestId) {
        return requestsRepository.findById(requestId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest)
        );
    }
}
