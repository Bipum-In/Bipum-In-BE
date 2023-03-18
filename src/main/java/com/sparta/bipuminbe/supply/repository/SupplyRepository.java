package com.sparta.bipuminbe.supply.repository;

import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findByCategory_Id(Long id);

    List<Supply> findByUser(User user);

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
