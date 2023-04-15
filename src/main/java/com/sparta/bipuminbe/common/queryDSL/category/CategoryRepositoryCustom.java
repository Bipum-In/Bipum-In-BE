package com.sparta.bipuminbe.common.queryDSL.category;


import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.enums.LargeCategory;

import java.util.List;
import java.util.Set;

public interface CategoryRepositoryCustom {
    List<Category> findCategoryInLargeCategory(Set<LargeCategory> largeCategories);
}
