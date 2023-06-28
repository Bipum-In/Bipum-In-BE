package com.sparta.bipuminbe.common.queryDSL.category;


import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.LargeCategory;


import java.util.List;
import java.util.Set;

public interface CategoryRepositoryCustom {
    List<Category> findCategoryInLargeCategory(Set<LargeCategory> largeCategories);

    // 내가 가진 LargeCategory 리스트만 나오는 SelectBox.
    List<LargeCategory> getMyLargeCategory(User user);

    // 내가 가진 category 리스트만 나오는 SelectBox.
    List<Category> getMyCategory(LargeCategory largeCategory, User user);

    // 내 부서가 가진 공용 비품들의 largeCategory 목록.
    List<LargeCategory> getMyCommonLargeCategory(Department department);

    // 내 부서가 가진 공용 비품이 있는 Category.
    List<Category> getMyCommonCategory(LargeCategory largeCategory, Department department);
}
