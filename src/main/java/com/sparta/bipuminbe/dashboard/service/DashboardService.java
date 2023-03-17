package com.sparta.bipuminbe.dashboard.service;

import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.dashboard.dto.*;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final CategoryRepository categoryRepository;
    private final SupplyRepository supplyRepository;

    private final RequestsRepository requestsRepository;

    // 관리자 대시보드
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
        Map<String, Long> countMap = new HashMap<>();
        countMap.put("supplyRequests", requestsRepository.countSupply());
        countMap.put("returnRequests", requestsRepository.countReturn());
        countMap.put("repairRequests", requestsRepository.countRepair());
        countMap.put("inRepairRequests", requestsRepository.countInRepair());

        // 요청 종류별 최신 수정일자
        Map<String, LocalDateTime> modifiedAtMap = new HashMap<>();
        modifiedAtMap.put("supplyModifiedAt", requestsRepository.supplyModifiedAt());
        modifiedAtMap.put("returnModifiedAt", requestsRepository.returnModifiedAt());
        modifiedAtMap.put("repairModifiedAt", requestsRepository.repairModifiedAt());
        modifiedAtMap.put("inRepairModifiedAt", requestsRepository.inRepairModifiedAt());

        RequestsCountDto requestsCountDto = RequestsCountDto.of
                (countMap, modifiedAtMap);

        // 비품 카테고리, 비품 현황 합쳐서 리턴
        return ResponseDto.success(AdminMainResponseDto.of(responseDtos, requestsCountDto));
    }

    // 사용자 대시보드
    public ResponseDto<UserMainResponseDto> getUserMain(User user) {
        // 사용 중인 비품 조회
        List<Supply> supplieList = supplyRepository.findAllByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NotFoundSupply));

        List<UserSupplyDto> userSupplyDtos = new ArrayList<>();

        for(Supply supply : supplieList){
            userSupplyDtos.add(UserSupplyDto.of(supply));
        }

        // 요청 현황 조회
        Map<String, Long> userCountMap = new HashMap<>();
        userCountMap.put("userCountSupply", requestsRepository.userCountInRepair(user.getId()));

        return ResponseDto.success(UserMainResponseDto.of(userSupplyDtos, userCountMap));
    }
}
