package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.requests.dto.ReportProcessRequestDto;
import com.sparta.bipuminbe.requests.dto.ReportRequestResponseDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReportRequestService {
    private final RequestsRepository requestsRepository;

    @Transactional(readOnly = true)
    public ResponseDto<ReportRequestResponseDto> getReportRequest(Long reportId, User user) {
        Requests request = getRequest(reportId);
        checkReportRequest(request);
        checkPermission(request, user);
        return ResponseDto.success(ReportRequestResponseDto.of(request, user.getRole()));
    }

    // 요청 유형이 맞는지 확인.
    private void checkReportRequest(Requests request) {
        if (!request.getRequestType().equals(RequestType.REPORT)) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }
    }

    // 해당 요청을 볼 권한 확인.
    private void checkPermission(Requests request, User user) {
        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !request.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NoPermission);
        }
    }

    private Requests getRequest(Long reportId) {
        return requestsRepository.findById(reportId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest));
    }

    @Transactional
    public ResponseDto<String> processingReportRequest(ReportProcessRequestDto reportProcessRequestDto) {
        Requests request = getRequest(reportProcessRequestDto.getRequestId());
        checkReportRequest(request);
        AcceptResult acceptResult = AcceptResult.valueOf(reportProcessRequestDto.getAcceptResult());
        request.processingRequest(acceptResult, reportProcessRequestDto.getComment());
        String message = acceptResult.equals(AcceptResult.ACCEPT) ? "승인 처리 완료." : "승인 거부 완료.";
        return ResponseDto.success(message);
    }
}
