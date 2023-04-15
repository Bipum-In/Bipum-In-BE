package com.sparta.bipuminbe.common.queryDSL.requests;

import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;
import java.util.Set;

public interface RequestsRepositoryCustom {
    Long countRequestByType(RequestType requestType, RequestStatus requestStatus);
    LocalDateTime requestsModifiedAt(RequestType requestType);
    Long countMyRequestByType(RequestType requestType, RequestStatus requestStatus, Long userId);
    LocalDateTime myRequestsModifiedAt(RequestType requestType, Long userId);

}
