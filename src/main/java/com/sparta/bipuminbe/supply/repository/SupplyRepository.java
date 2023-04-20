package com.sparta.bipuminbe.supply.repository;

import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import com.sparta.bipuminbe.common.queryDSL.supply.SupplyRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SupplyRepository extends JpaRepository<Supply, Long>, SupplyRepositoryCustom {

    Optional<List<Supply>> findByUser_IdAndCategory_LargeCategoryInAndDeletedFalse(Long id, Set<LargeCategory> categoryQuery);

    List<Supply> findByPartners_PartnersId(Long partnersId);

    Optional<Supply> findBySupplyIdAndDeletedFalse(Long supplyId);

    List<Supply> findByCategory_IdAndStatusAndDeletedFalse(Long categoryId, SupplyStatusEnum stock);

    List<Supply> findByUser_IdAndDeletedFalse(Long id);

    // 부서 삭제시 공용 비품 리스트 호출.
    List<Supply> findByDepartment_Id(Long id);

    // 카테고리 삭제 전 비품 체크.
    boolean existsByCategory_IdAndDeletedFalse(Long categoryId);

    // 유저 대쉬보드 공용 비품 보기 전환
    List<Supply> findByDepartmentAndCategory_LargeCategoryInAndDeletedFalseOrderByCategory_CategoryNameAsc
            (Department department, Collection<LargeCategory> largeCategories);

    boolean existsBySerialNum(String serialNum);

    Optional<Supply> findBySerialNumAndDeletedTrue(String serialNum);
}
