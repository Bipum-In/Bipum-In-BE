package com.sparta.bipuminbe.dashboard.service;

import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.dashboard.dto.AdminMainResponseDto;
import com.sparta.bipuminbe.dashboard.dto.RequestsCountDto;
import com.sparta.bipuminbe.dashboard.dto.SupplyCountDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final CategoryRepository categoryRepository;
    private final SupplyRepository supplyRepository;

    private final RequestsRepository requestsRepository;

    @Transactional(readOnly = true)
    public ResponseDto<AdminMainResponseDto> getAdminMain() {

        // 비품 카테고리별 현황
        List<Category> categoryList = categoryRepository.findAll();

        List<SupplyCountDto> responseDtos = new ArrayList();
        // 카테고리별 총 수량, 사용중, 수리중, 재고량 계산
        for(Category category : categoryList){

            Long totalCount = supplyRepository.countTotal(category.getId());
            Long useCount = supplyRepository.countUse(category.getId());
            Long repairCount = supplyRepository.countRepair(category.getId());
            Long stockCount = supplyRepository.countStock(category.getId());

            responseDtos.add(SupplyCountDto.of(
                    category, totalCount, useCount, repairCount, stockCount));
        }

        // 요청 현황
        Long supplyRequests = requestsRepository.countSupply();
        Long returnRequests = requestsRepository.countReturn();
        Long repairRequests = requestsRepository.countRepair();
        Long inRepair = requestsRepository.countInRepair();

        // 요청 종류별 최신 수정일자
        LocalDateTime supplyModifiedAt = requestsRepository.supplyModifiedAt();
        LocalDateTime returnModifiedAt = requestsRepository.returnModifiedAt();
        LocalDateTime repairModifiedAt = requestsRepository.repairModifiedAt();
        LocalDateTime inRepairModifiedAt = requestsRepository.inRepairModifiedAt();

        RequestsCountDto requestsCountDto = RequestsCountDto.of
                (supplyRequests, returnRequests, repairRequests, inRepair,
                        supplyModifiedAt, returnModifiedAt, repairModifiedAt, inRepairModifiedAt);

        // 비품 카테고리, 비품 현황 합쳐서 리턴
        return ResponseDto.success(AdminMainResponseDto.of(responseDtos, requestsCountDto));
    }
}
