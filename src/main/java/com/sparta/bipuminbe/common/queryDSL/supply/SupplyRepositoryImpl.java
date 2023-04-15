package com.sparta.bipuminbe.common.queryDSL.supply;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.QSupply;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SupplyRepositoryImpl implements SupplyRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    public Long countTotal(Long categoryId) {
        QSupply supply = QSupply.supply;

        return (long) queryFactory.selectFrom(supply)
                .where(supply.category.id.eq(categoryId).and(supply.deleted.eq(false)))
                .fetch().size();
    }

    public Long countBySupplyStatus(Long categoryId, SupplyStatusEnum supplyStatusEnum) {
        QSupply supply = QSupply.supply;

        return (long) queryFactory.selectFrom(supply)
                .where(supply.category.id.eq(categoryId)
                        .and(supply.status.eq(supplyStatusEnum))
                        .and(supply.deleted.eq(false))).fetch().size();
    }

}
