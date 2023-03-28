package com.sparta.bipuminbe.dashboard.service;

import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.entity.Notification;
import com.sparta.bipuminbe.common.sse.repository.NotificationRepository;
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
    private final NotificationRepository notificationRepository;

    // 관리자 대시보드
    @Transactional(readOnly = true)
    public ResponseDto<AdminMainResponseDto> getAdminMain(Long userId, LargeCategory largeCategory) {
        Set<LargeCategory> categoryQuery = getCategoryQuery(largeCategory);
        List<Category> categoryList = categoryRepository.findByLargeCategoryInOrderByCategoryName(categoryQuery);

        List<SupplyCountDto> responseDtos = new ArrayList();
        // 카테고리별 총 수량, 사용중, 수리중, 재고량 계산
        for(Category category : categoryList){

            Long totalCount = supplyRepository.countTotal(category.getId());
            Long useCount = supplyRepository.countUse(category.getId());
            Long repairCount = supplyRepository.countRepair(category.getId());
            Long stockCount = supplyRepository.countStock(category.getId());

            if(totalCount == 0){
                continue;
            }

            responseDtos.add(SupplyCountDto.of(
                    category, totalCount, useCount, repairCount, stockCount));
        }

        // 요청 현황
        Map<String, Long> countMap = new HashMap<>();
        countMap.put("supplyRequests", requestsRepository.countSupply());
        countMap.put("returnRequests", requestsRepository.countReturn());
        countMap.put("repairRequests", requestsRepository.countRepair());
        countMap.put("ReportRequests", requestsRepository.countReport());
        countMap.put("UnProcessedRequests",
                requestsRepository.countSupply() + requestsRepository.countReturn() +
                requestsRepository.countRepair() + requestsRepository.countReport());

        // 요청 종류별 최신 수정일자
        Map<String, LocalDateTime> modifiedAtMap = new HashMap<>();
        modifiedAtMap.put("supplyModifiedAt", requestsRepository.supplyModifiedAt());
        modifiedAtMap.put("returnModifiedAt", requestsRepository.returnModifiedAt());
        modifiedAtMap.put("repairModifiedAt", requestsRepository.repairModifiedAt());
        modifiedAtMap.put("ReportModifiedAt", requestsRepository.reportModifiedAt());

        RequestsCountDto requestsCountDto = RequestsCountDto.of
                (countMap, modifiedAtMap);
//
//        // 알림 최신 순으로 4개 가져오기 알림에 검색기준이 되는
//        List<Notification> notifications = notificationRepository.findTop4ByReceiver_idOrderByCreatedAtDesc(userId);

        // 비품 카테고리, 비품 현황, 알림 합쳐서 리턴
        return ResponseDto.success(AdminMainResponseDto.of(responseDtos, requestsCountDto));
    }

    // 사용자 대시보드
    public ResponseDto<UserMainResponseDto> getUserMain(User user, LargeCategory largeCategory) {
        Set<LargeCategory> categoryQuery = getCategoryQuery(largeCategory);
        List<Category> categoryList = categoryRepository.findByLargeCategoryInOrderByCategoryName(categoryQuery);

        // 요청 현황 조회
        Map<String, Long> userCountMap = new HashMap<>();
        userCountMap.put("userCountSupply", requestsRepository.userCountSupply(user.getId()));
        userCountMap.put("userCountReturn", requestsRepository.userCountReturn(user.getId()));
        userCountMap.put("userCountRepair", requestsRepository.userCountRepair(user.getId()));
        userCountMap.put("userCountReport", requestsRepository.userCountReport(user.getId()));

        // 사용 중인 비품 조회
        List<UserSupplyDto> userSupplyDtos = new ArrayList<>();

        List<Supply> supplies = supplyRepository.findByUser_IdAndCategory_LargeCategoryIn(user.getId(), categoryQuery)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFoundSupply));
        for(Supply supply : supplies){
            userSupplyDtos.add(UserSupplyDto.of(supply));
        }
//        for(Category category : categoryList){
//        }

        // 요청 현황, 사용 중인 비품 현황 합쳐서 리턴
        return ResponseDto.success(UserMainResponseDto.of(userSupplyDtos, userCountMap));
    }

    private Set<LargeCategory> getCategoryQuery(LargeCategory largeCategory) {
        Set<LargeCategory> categoryQuery = new HashSet<>();
        if (largeCategory == null) {
            categoryQuery.addAll(List.of(LargeCategory.values()));
        } else {
            categoryQuery.add(largeCategory);
        }
        return categoryQuery;
    }
}
