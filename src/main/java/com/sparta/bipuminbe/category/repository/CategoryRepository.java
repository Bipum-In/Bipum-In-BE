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

    Optional<Category> findByCategoryNameAndDeletedFalse(String categoryName);

    Optional<Category> findByCategoryNameAndDeletedTrue(String categoryName);

    List<Category> findByDeletedFalse();

    Optional<Category> findByCategoryName(String categoryName);
}
