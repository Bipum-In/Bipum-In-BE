package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.dto.SupplyRequestResponseDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplyRequestService {

    private final RequestsRepository requestsRepository;
    private final SupplyRepository supplyRepository;
    private final CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public ResponseDto<SupplyRequestResponseDto> getSupplyRequest(Long requestId, User user) {
        Requests request = getRequest(requestId);
        checkSupplyRequest(request, user);
        return ResponseDto.success(SupplyRequestResponseDto.of(request, user.getRole()));
    }

    @Transactional
    public ResponseDto<String> processingSupplyRequest(Long requestId, AcceptResult acceptResult, Long supplyId) {
        Requests request = getRequest(requestId);
        request.processingRequest(acceptResult);
        if (acceptResult.equals(AcceptResult.DECLINE)) {
            return ResponseDto.success("승인 거부 완료.");
        }
        if (supplyId == null) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }
        Supply supply = getSupply(supplyId);
        supply.allocateSupply(request.getUser());
        return ResponseDto.success("승인 처리 완료.");
    }

    private void checkSupplyRequest(Requests request, User user) {
        if (!request.getRequestType().equals(RequestType.SUPPLY)) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }

        if (!request.getUser().getId().equals(user.getId()) && !user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NoPermission);
        }
    }

    private Requests getRequest(Long requestId) {
        return requestsRepository.findById(requestId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest));
    }

    private Supply getSupply(Long supplyId) {
        return supplyRepository.findById(supplyId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundSupply));
    }

    public ResponseDto<String> supplyRequest(RequestsRequestDto requestsRequestDto, User user) {
        Category category = categoryRepository.findById(requestsRequestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.NotFoundCategory));

        requestsRepository.save(Requests
                .builder()
                        .content(requestsRequestDto.getContent())
                        .requestType(requestsRequestDto.getRequestType())
                        .requestStatus(RequestStatus.UNPROCESSED)
                        .category(category)
                        .user(user)
                .build());

        return ResponseDto.success("비품 요청 완료");
    }
}
