package com.sparta.bipuminbe.supply.repository;

import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
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

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId", nativeQuery = true)
    Long countTotal(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'USING'", nativeQuery = true)
    Long countUse(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'REPAIRING'", nativeQuery = true)
    Long countRepair(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'STOCK'", nativeQuery = true)
    Long countStock(@Param("categoryId") Long categoryId);

    Optional<List<Supply>> findAllByUserId(Long userId);

    @Query(value = "SELECT s FROM Supply s " +
            "inner join Category c on s.category = c " +
            "left join users u on s.user = u " +
            "LEFT JOIN Partners p on s.partners = p " +
            "left join Department d on u.department = d " +
            "WHERE (u.empName LIKE :keyword OR d.deptName LIKE :keyword OR c.categoryName LIKE :keyword " +
            "OR s.modelName LIKE :keyword OR s.serialNum LIKE :keyword) " +
            "AND c.id IN :categoryQuery " +
            "AND s.status IN :statusQuery")
    Page<Supply> getSupplyList(@Param("keyword") String keyword, @Param("categoryQuery") Set<Long> categoryQuery,
                               @Param("statusQuery") Set<SupplyStatusEnum> statusQuery, Pageable pageable);
}
