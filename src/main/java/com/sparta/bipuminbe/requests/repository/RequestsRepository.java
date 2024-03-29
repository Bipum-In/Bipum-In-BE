package com.sparta.bipuminbe.requests.repository;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.queryDSL.requests.RequestsRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RequestsRepository extends JpaRepository<Requests, Long>, RequestsRepositoryCustom {

    boolean existsBySupply_SupplyIdAndRequestStatusNot(Long supplyId, RequestStatus requestStatus);

    // 비품 History 조회. (승인 건만.)
    Page<Requests> findBySupply_SupplyIdAndRequestTypeInAndAcceptResult(Long supplyId, Set<RequestType> requestTypeQuery, AcceptResult accept, Pageable pageable);

    // 비품 폐기 전 요청들 처리. (수리중 포함.)
    List<Requests> findBySupply_SupplyIdAndRequestStatusNot(Long supplyId, RequestStatus requestStatus);

    // 유저 탈퇴 전 요청들 처리. (수리중 불포함.)
    List<Requests> findByUser_IdAndRequestStatus(Long id, RequestStatus unprocessed);
}
