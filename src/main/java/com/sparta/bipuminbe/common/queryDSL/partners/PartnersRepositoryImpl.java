package com.sparta.bipuminbe.common.queryDSL.partners;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.entity.QPartners;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PartnersRepositoryImpl implements PartnersRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Partners> getPartnersList(String keyword, Pageable pageable) {
        QPartners partners = QPartners.partners;
        JPAQuery<Partners> query = queryFactory.selectFrom(partners)
                .distinct()
                .where((partners.partnersName.containsIgnoreCase(keyword)
                        .or(partners.phone.containsIgnoreCase(keyword))
                        .or(partners.email.containsIgnoreCase(keyword))
                ).and(partners.deleted).eq(false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Partners> partnersList = query.fetch();
        long totalCount = query.fetchCount();

        return new PageImpl<>(partnersList, pageable, totalCount);
    }
}
