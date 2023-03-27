package com.sparta.bipuminbe.requests.repository;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
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
    List<Requests> findBySupply(Supply supply);

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
            "AND u.id IN :userIdQuery")
    Page<Requests> getRequestsList(@Param("keyword") String keyword, @Param("requestTypeQuery") Set<RequestType> requestTypeQuery,
                                   @Param("requestStatusQuery") Set<RequestStatus> requestStatusQuery, Set<Long> userIdQuery, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.request_type = 'SUPPLY'", nativeQuery = true)
    Long countSupply();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.request_type = 'RETURN'", nativeQuery = true)
    Long countReturn();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.request_type = 'REPAIR'", nativeQuery = true)
    Long countRepair();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.request_status = 'REPORT'", nativeQuery = true)
    Long countReport();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'SUPPLY'", nativeQuery = true)
    LocalDateTime supplyModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'RETURN'", nativeQuery = true)
    LocalDateTime returnModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'REPAIR'", nativeQuery = true)
    LocalDateTime repairModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_status = 'REPORT'", nativeQuery = true)
    LocalDateTime reportModifiedAt();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_status = 'SUPPLY'", nativeQuery = true)
    Long userCountSupply(@Param("userId") Long id);

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_status = 'RETURN'", nativeQuery = true)
    Long userCountReturn(@Param("userId") Long id);

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_status = 'REPAIR'", nativeQuery = true)
    Long userCountRepair(@Param("userId") Long id);

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_status = 'REPORT'", nativeQuery = true)
    Long userCountReport(@Param("userId") Long id);

    Page<Requests> findBySupply_SupplyIdAndRequestTypeIn(Long supplyId, Set<RequestType> requestTypeQuery, Pageable pageable);
}
