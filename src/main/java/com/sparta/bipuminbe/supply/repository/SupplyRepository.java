package com.sparta.bipuminbe.supply.repository;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SupplyRepository extends JpaRepository<Supply, Long> {

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND deleted = false", nativeQuery = true)
    Long countTotal(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'USING' AND deleted = false", nativeQuery = true)
    Long countUse(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'REPAIRING' AND deleted = false", nativeQuery = true)
    Long countRepair(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM supply WHERE supply.category_id = :categoryId AND supply.status = 'STOCK' AND deleted = false", nativeQuery = true)
    Long countStock(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT s FROM Supply s " +
            "inner join Category c on s.category = c " +
            "left join users u on s.user = u " +
            "LEFT JOIN Partners p on s.partners = p " +
            "left join Department d on u.department = d " +
            "WHERE (u.empName LIKE :keyword OR d.deptName LIKE :keyword OR c.categoryName LIKE :keyword " +
            "OR s.modelName LIKE :keyword OR s.serialNum LIKE :keyword) " +
            "AND c.id IN :categoryQuery " +
            "AND s.status IN :statusQuery " +
            "AND s.deleted = false " +
            "order by s.createdAt desc")
    Page<Supply> getSupplyList(@Param("keyword") String keyword, @Param("categoryQuery") Set<Long> categoryQuery,
                               @Param("statusQuery") Set<SupplyStatusEnum> statusQuery, Pageable pageable);

    Optional<List<Supply>> findByUser_IdAndCategory_LargeCategoryInAndDeletedFalse(Long id, Set<LargeCategory> categoryQuery);

    List<Supply> findByPartners_PartnersId(Long partnersId);

    Optional<Supply> findBySupplyIdAndDeletedFalse(Long supplyId);

    List<Supply> findByCategory_IdAndStatusAndDeletedFalse(Long categoryId, SupplyStatusEnum stock);

    // 다른 요청을 처리 중이라 신청을 할 수 없는 비품은 출력하지 않는 로직.
    @Query(value = "select s from Supply s " +
            "inner join Category c on s.category = c " +
            "where s.user = :user and c.id = :categoryId and s.deleted = false " +
            "and s not in (select distinct r.supply from Requests r " +
            "where r.requestStatus in :statusQuery and r.supply is not null)")
    List<Supply> getMySupply(@Param("user") User user,
                             @Param("categoryId") Long categoryId,
                             @Param("statusQuery") Set<RequestStatus> statusQuery);

    // 다른 요청을 처리 중이라 신청을 할 수 없는 비품은 출력하지 않는 로직. (공용 비품 버전)
    @Query(value = "select s from Supply s " +
            "inner join Category c on s.category = c " +
            "where s.department = :department and c.id = :categoryId and s.deleted = false " +
            "and s not in (select distinct r.supply from Requests r " +
            "where r.requestStatus in :statusQuery and r.supply is not null)")
    List<Supply> getMyCommonSupply(@Param("department") Department department,
                             @Param("categoryId") Long categoryId,
                             @Param("statusQuery") Set<RequestStatus> statusQuery);

    List<Supply> findByUser_IdAndDeletedFalse(Long id);

    // 부서 삭제시 공용 비품 리스트 호출.
    List<Supply> findByDepartment_Id(Long id);

    // 카테고리 삭제 전 비품 체크.
    boolean existsByCategory_Id(Long id);

    // 유저 대쉬보드 공용 비품 보기 전환
    List<Supply> findByDepartmentAndCategory_LargeCategoryInAndDeletedFalseOrderByCategory_CategoryNameAsc
            (Department department, Collection<LargeCategory> largeCategories);
}
