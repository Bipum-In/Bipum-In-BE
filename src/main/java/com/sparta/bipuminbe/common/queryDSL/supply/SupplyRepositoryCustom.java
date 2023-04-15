package com.sparta.bipuminbe.common.queryDSL.supply;

import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;

public interface SupplyRepositoryCustom {
    Long countTotal(Long categoryId);
    Long countBySupplyStatus(Long categoryId, SupplyStatusEnum supplyStatusEnum);

}
