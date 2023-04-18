package com.sparta.bipuminbe.common.queryDSL.requests;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Set;

public interface RequestsRepositoryCustom {
    Long countRequestByType(RequestType requestType, RequestStatus requestStatus);
    LocalDateTime requestsModifiedAt(RequestType requestType);
    Long countMyRequestByType(RequestType requestType, RequestStatus requestStatus, Long userId);
    LocalDateTime myRequestsModifiedAt(RequestType requestType, Long userId);

    Page<Requests> getRequestsList(String keyword, Set<RequestType> requestTypeQuery,
                                   Set<RequestStatus> requestStatusQuery, Set<Long> userIdQuery, Pageable pageable);

}
