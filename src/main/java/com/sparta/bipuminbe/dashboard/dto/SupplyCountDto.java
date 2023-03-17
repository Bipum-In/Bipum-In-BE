package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplyCountDto {
    private Long categoryId;
    private String categoryName;
    private String categoryImage;
    private Long totalCount;
    private Long useCount;
    private Long repairCount;
    private Long stockCount;

    public static SupplyCountDto of(
            Category category, Long totalCount,
            Long useCount, Long repairCount, Long stockCount) {

        return SupplyCountDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .categoryImage(category.getCategoryImage())
                .totalCount(totalCount)
                .useCount(useCount)
                .repairCount(repairCount)
                .stockCount(stockCount)
                .build();
    }
}
