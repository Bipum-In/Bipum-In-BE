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

    @Query(value = "SELECT r FROM Requests r " +
            "INNER JOIN users u ON r.user = u " +
            "INNER JOIN Department d ON u.department = d " +
            "LEFT JOIN Category c ON r.category  = c " +
            "LEFT JOIN Supply s ON r.supply = s " +
            "WHERE (u.empName LIKE :keyword OR d.deptName LIKE :keyword OR c.categoryName LIKE :keyword " +
            "OR s.modelName LIKE :keyword OR s.serialNum LIKE :keyword) " +
            "AND r.requestType IN :requestTypeQuery " +
            "AND r.requestStatus IN :requestStatusQuery " +
            "AND u.id IN :userIdQuery " +
            "order by r.createdAt desc")
    Page<Requests> getRequestsList(@Param("keyword") String keyword, @Param("requestTypeQuery") Set<RequestType> requestTypeQuery,
                                   @Param("requestStatusQuery") Set<RequestStatus> requestStatusQuery,
                                   @Param("userIdQuery") Set<Long> userIdQuery, Pageable pageable);

//    long countByRequestTypeAndRequestStatusNot(RequestType requestType, RequestStatus requestStatus);

//    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'SUPPLY'", nativeQuery = true)
//    LocalDateTime supplyModifiedAt();
//
//    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'RETURN'", nativeQuery = true)
//    LocalDateTime returnModifiedAt();
//
//    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'REPAIR'", nativeQuery = true)
//    LocalDateTime repairModifiedAt();
//
//    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_status = 'REPORT'", nativeQuery = true)
//    LocalDateTime reportModifiedAt();

    Page<Requests> findBySupply_SupplyIdAndRequestTypeInAndAcceptResult(Long supplyId, Set<RequestType> requestTypeQuery, AcceptResult accept, Pageable pageable);

    // 비품 폐기 전 요청들 처리.
    List<Requests> findBySupply_SupplyIdAndRequestStatusNot(Long supplyId, RequestStatus requestStatus);

    List<Requests> findByUser_IdAndRequestStatusNot(Long id, RequestStatus requestStatus);
}
