package com.sparta.bipuminbe.common.queryDSL.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.QCategory;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findCategoryInLargeCategory(Set<LargeCategory> largeCategories) {
        QCategory category = QCategory.category;
        return queryFactory.selectFrom(category)
                .where(category.largeCategory.in(largeCategories)
                        .and(category.deleted.eq(false)))
                .orderBy(category.categoryName.asc())
                .fetch();
    }
}

