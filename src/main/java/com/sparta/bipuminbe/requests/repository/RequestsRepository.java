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
    Page<Requests> findByRequestTypeInAndRequestStatusIn(Set<RequestType> requestTypeQuery, Set<RequestStatus> requestStatusQuery, Pageable pageable);
    List<Requests> findBySupply(Supply supply);

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.request_type = 'SUPPLY'", nativeQuery = true)
    Long countSupply();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.request_type = 'RETURN'", nativeQuery = true)
    Long countReturn();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.request_type = 'REPAIR'", nativeQuery = true)
    Long countRepair();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.request_status = 'REPAIRING'", nativeQuery = true)
    Long countInRepair();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'SUPPLY'", nativeQuery = true)
    LocalDateTime supplyModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'RETURN'", nativeQuery = true)
    LocalDateTime returnModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_type = 'REPAIR'", nativeQuery = true)
    LocalDateTime repairModifiedAt();

    @Query(value = "SELECT max(modified_at) FROM requests WHERE requests.request_status = 'REPAIRING'", nativeQuery = true)
    LocalDateTime inRepairModifiedAt();

    @Query(value = "SELECT COUNT(*) FROM requests WHERE requests.user_id = :userId AND requests.request_status = 'REPAIRING'", nativeQuery = true)
    Long userCountInRepair(@Param("userId") Long id);
}
