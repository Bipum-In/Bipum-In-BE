package com.sparta.bipuminbe.category.repository;


import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryName(String categoryName);

    List<Category> findByLargeCategoryInOrderByCategoryName(Set<LargeCategory> largeCategories);

    Optional<Category> findByCategoryNameAndLargeCategory(String categoryName, LargeCategory largeCategory);
}
