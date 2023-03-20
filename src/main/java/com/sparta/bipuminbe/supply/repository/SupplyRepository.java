package com.sparta.bipuminbe.supply.repository;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findByCategory_Id(Long id);

    List<Supply> findByUser(User user);
    Page<Requests> findBySupplyInRequestStatusIn(Set<RequestStatus> requestStatusQuery, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId", nativeQuery = true)
    Long countTotal(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'USING'", nativeQuery = true)
    Long countUse(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'REPAIRING'", nativeQuery = true)
    Long countRepair(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'STOCK'", nativeQuery = true)
    Long countStock(@Param("categoryId") Long categoryId);

    Optional<List<Supply>> findAllByUserId(Long userId);
}
