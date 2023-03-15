package com.sparta.bipuminbe.requests.repository;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestsRepository extends JpaRepository<Requests, Long> {
    List<Requests> findByRequestTypeAndRequestStatusOrderByCreatedAtDesc(RequestType requestType, RequestStatus requestStatus, Pageable pageable);
    List<Requests> findByRequestStatusOrderByCreatedAtDesc(RequestStatus requestStatus, Pageable pageable);
}
