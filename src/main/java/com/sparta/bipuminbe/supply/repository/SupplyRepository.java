package com.sparta.bipuminbe.supply.repository;

import com.sparta.bipuminbe.common.entity.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplyRepository extends JpaRepository<Supply, Long> {

    List<Supply> findAllByCategoryId(int categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId", nativeQuery = true)
    Long countTotal(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'USING'", nativeQuery = true)
    Long countUse(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'REPAIRING'", nativeQuery = true)
    Long countRepair(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'STOCK'", nativeQuery = true)
    Long countStock(@Param("categoryId") Long categoryId);
}
