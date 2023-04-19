package com.sparta.bipuminbe.common.queryDSL.supply;

import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface SupplyRepositoryCustom {
    Long countTotal(Long categoryId);
    Long countBySupplyStatus(Long categoryId, SupplyStatusEnum supplyStatusEnum);

    // Supply 검색.
    Page<Supply> getSupplyList(String keyword, Set<Long> categoryIdQuery, Set<SupplyStatusEnum> statusQuery, Pageable pageable);

    // 다른 요청을 처리 중이라 신청을 할 수 없는 비품은 출력하지 않는 로직.
    List<Supply> getMySupply(User user, Long categoryId, Set<RequestStatus> statusQuery);

    // 다른 요청을 처리 중이라 신청을 할 수 없는 비품은 출력하지 않는 로직. (공용 비품 버전)
    List<Supply> getMyCommonSupply(Department department, Long categoryId, Set<RequestStatus> statusQuery);
}
