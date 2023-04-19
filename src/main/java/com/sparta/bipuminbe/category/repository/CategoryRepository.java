package com.sparta.bipuminbe.category.repository;

import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.queryDSL.category.CategoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
    boolean existsByCategoryNameAndDeletedFalse(String categoryName);

//    List<Category> findByLargeCategoryInAndDeletedFalseOrderByCategoryName(Set<LargeCategory> largeCategories);

    Optional<Category> findByCategoryNameAndDeletedFalse(String categoryName);

    Optional<Category> findByCategoryNameAndDeletedTrue(String categoryName);

    // 내가 가진 LargeCategory 리스트만 나오는 SelectBox.
    @Query(value = "select distinct c.largeCategory from Supply s " +
            "inner join Category c on s.category = c " +
            "where s.user = :user and s.deleted = false")
    List<String> getMyLargeCategory(@Param("user") User user);

    // 내가 가진 category 리스트만 나오는 SelectBox.
    @Query(value = "select distinct c from Supply s " +
            "inner join Category c on s.category = c " +
            "where c.largeCategory = :largeCategory and s.user = :user and s.deleted = false")
    List<Category> getMyCategory(@Param("largeCategory") LargeCategory largeCategory, @Param("user") User user);

    // 내 부서가 가진 공용 비품이 있는 largeCategory.
    @Query(value = "select distinct c.largeCategory from Supply s " +
            "inner join Category c on s.category = c " +
            "where s.department = :department and s.deleted = false")
    List<String> getMyCommonLargeCategory(@Param("department") Department department);

    // 내 부서가 가진 공용 비품이 있는 Category.
    @Query(value = "select distinct c from Supply s " +
            "inner join Category c on s.category = c " +
            "where c.largeCategory = :largeCategory and s.department = :department and s.deleted = false")
    List<Category> getMyCommonCategory(@Param("largeCategory") LargeCategory largeCategory,
                                       @Param("department") Department department);

    List<Category> findByDeletedFalse();

    Optional<Category> findByCategoryName(String categoryName);
}
