package com.sparta.bipuminbe.common.queryDSL.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.*;
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

    @Override
    public List<LargeCategory> getMyLargeCategory(User user) {
        QCategory category = QCategory.category;
        QSupply supply = QSupply.supply;
        return queryFactory.selectDistinct(category.largeCategory)
                .from(supply)
                .innerJoin(supply.category, category)
                .where(supply.user.eq(user), supply.deleted.eq(false))
                .fetch();
    }

    @Override
    public List<Category> getMyCategory(LargeCategory largeCategory, User user) {
        QCategory category = QCategory.category;
        QSupply supply = QSupply.supply;
        return queryFactory.selectDistinct(category)
                .from(supply)
                .innerJoin(supply.category, category)
                .where(supply.user.eq(user), category.largeCategory.eq(largeCategory), supply.deleted.eq(false))
                .fetch();
    }

    @Override
    public List<LargeCategory> getMyCommonLargeCategory(Department department) {
        QCategory category = QCategory.category;
        QSupply supply = QSupply.supply;
        return queryFactory.selectDistinct(category.largeCategory)
                .from(supply)
                .innerJoin(supply.category, category)
                .where(supply.department.eq(department), supply.deleted.eq(false))
                .fetch();
    }

    @Override
    public List<Category> getMyCommonCategory(LargeCategory largeCategory, Department department) {
        QCategory category = QCategory.category;
        QSupply supply = QSupply.supply;
        return queryFactory.selectDistinct(category)
                .from(supply)
                .innerJoin(supply.category, category)
                .where(supply.department.eq(department), category.largeCategory.eq(largeCategory), supply.deleted.eq(false))
                .fetch();
    }
}

