package com.sparta.bipuminbe.requests.repository;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RequestsRepository extends JpaRepository<Requests, Long> {
    // 카테고리 삭제 전 카테고리 체크.
    List<Requests> findByCategory_Id(Long id);

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
                                   @Param("requestStatusQuery") Set<RequestStatus> requestStatusQuery, Set<Long> userIdQuery, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM requests " +
            "WHERE requests.request_type = 'SUPPLY' AND request_status != 'PROCESSED'", nativeQuery = true)
    Long countSupply();

    @Query(value = "SELECT COUNT(*) FROM requests " +
            "WHERE requests.request_type = 'RETURN' AND request_status != 'PROCESSED'", nativeQuery = true)
    Long countReturn();

    @Query(value = "SELECT COUNT(*) FROM requests " +
            "WHERE requests.request_type = 'REPAIR' AND request_status != 'PROCESSED'", nativeQuery = true)
    Long countRepair();

    @Query(value = "SELECT COUNT(*) FROM requests " +
            "WHERE requests.request_status = 'REPORT' AND request_status != 'PROCESSED'", nativeQuery = true)
    Long countReport();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'SUPPLY'", nativeQuery = true)
    LocalDateTime supplyModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'RETURN'", nativeQuery = true)
    LocalDateTime returnModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'REPAIR'", nativeQuery = true)
    LocalDateTime repairModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_status = 'REPORT'", nativeQuery = true)
    LocalDateTime reportModifiedAt();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_type = 'SUPPLY' AND request_status != 'PROCESSED'" , nativeQuery = true)
    Long userCountSupply(@Param("userId") Long id);

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_type = 'RETURN' AND request_status != 'PROCESSED'", nativeQuery = true)
    Long userCountReturn(@Param("userId") Long id);

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_type = 'REPAIR' AND request_status != 'PROCESSED'", nativeQuery = true)
    Long userCountRepair(@Param("userId") Long id);

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_type = 'REPORT' AND request_status != 'PROCESSED'", nativeQuery = true)
    Long userCountReport(@Param("userId") Long id);

    Page<Requests> findBySupply_SupplyIdAndRequestTypeInAndAcceptResult(Long supplyId, Set<RequestType> requestTypeQuery, AcceptResult accept, Pageable pageable);

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.user_id = :userId AND requests.request_type = 'SUPPLY'", nativeQuery = true)
    LocalDateTime supplyUserModifiedAt(@Param("userId") Long id);
    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.user_id = :userId AND requests.request_type = 'RETURN'", nativeQuery = true)
    LocalDateTime returnUserModifiedAt(@Param("userId") Long id);
    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.user_id = :userId AND requests.request_type = 'REPAIR'", nativeQuery = true)
    LocalDateTime repairUserModifiedAt(@Param("userId") Long id);
    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.user_id = :userId AND requests.request_type = 'REPORT'", nativeQuery = true)
    LocalDateTime reportUserModifiedAt(@Param("userId") Long id);
}
