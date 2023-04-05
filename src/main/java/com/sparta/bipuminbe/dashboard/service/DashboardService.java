package com.sparta.bipuminbe.dashboard.service;

import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForAdmin;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import com.sparta.bipuminbe.common.sse.repository.NotificationRepository;
import com.sparta.bipuminbe.dashboard.dto.*;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseDto<AdminMainResponseDto> getAdminMain(User admin, LargeCategory largeCategory) {
        Set<LargeCategory> categoryQuery = getCategoryQuery(largeCategory);
        List<Category> categoryList = categoryRepository.findByLargeCategoryInOrderByCategoryName(categoryQuery);

        List<SupplyCountDto> responseDtos = new ArrayList();
        // 카테고리별 총 수량, 사용중, 수리중, 재고량 계산
        for (Category category : categoryList) {

            Long totalCount = supplyRepository.countTotal(category.getId());
            Long useCount = supplyRepository.countUse(category.getId());
            Long repairCount = supplyRepository.countRepair(category.getId());
            Long stockCount = supplyRepository.countStock(category.getId());

            if (totalCount == 0) {
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

//        Pageable pageable = getPageable(page, size);
//
//        // 유저의 요청을 알림 발생시간 최신순으로 4개 검색함
//        Page<NotificationResponseForAdmin> notifications = notificationRepository.findUserNotification(admin.getId(),pageable);

        // 비품 카테고리, 비품 현황, 알림 합쳐서 리턴
        return ResponseDto.success(AdminMainResponseDto.of(responseDtos, requestsCountDto));
    }

    @Transactional(readOnly = true)
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
        userCountMap.put("UnProcessedUserRequests",
                requestsRepository.userCountSupply(user.getId()) + requestsRepository.userCountReturn(user.getId()) +
                        requestsRepository.userCountRepair(user.getId()) + requestsRepository.userCountReport(user.getId()));

        // 요청 종류별 최신 수정일자
        Map<String, LocalDateTime> modifiedAtMap = new HashMap<>();
        modifiedAtMap.put("supplyUserModifiedAt", requestsRepository.supplyUserModifiedAt(user.getId()));
        modifiedAtMap.put("returnUserModifiedAt", requestsRepository.returnUserModifiedAt(user.getId()));
        modifiedAtMap.put("repairUserModifiedAt", requestsRepository.repairUserModifiedAt(user.getId()));
        modifiedAtMap.put("ReportUserModifiedAt", requestsRepository.reportUserModifiedAt(user.getId()));

        RequestsCountDto requestsCountDto = RequestsCountDto.of
                (userCountMap, modifiedAtMap);

        // 사용 중인 비품 조회
        List<UserSupplyDto> userSupplyDtos = new ArrayList<>();

        List<Supply> supplies = supplyRepository.findByUser_IdAndCategory_LargeCategoryInAndDeletedFalse(user.getId(), categoryQuery)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFoundSupply));
        for (Supply supply : supplies) {
            userSupplyDtos.add(UserSupplyDto.of(supply));
        }

//        List<NotificationResponseForUser> notifications = notificationRepository.findAdminNotification(user.getId());

        // 요청 현황, 사용 중인 비품 현황 합쳐서 리턴
        return ResponseDto.success(UserMainResponseDto.of(userSupplyDtos, requestsCountDto));
    }

    @Transactional(readOnly = true)
    public Set<LargeCategory> getCategoryQuery(LargeCategory largeCategory) {
        Set<LargeCategory> categoryQuery = new HashSet<>();
        if (largeCategory == null) {
            categoryQuery.addAll(List.of(LargeCategory.values()));
        } else {
            categoryQuery.add(largeCategory);
        }
        return categoryQuery;
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<NotificationResponseForAdmin>> getAdminAlarm(User admin, int page, int size) {
        Pageable pageable = getPageable(page, size);
        Page<NotificationResponseForAdmin> notifications = notificationRepository.findUserNotification(admin.getId(), pageable);
        return ResponseDto.success(notifications);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<NotificationResponseForUser>> getUserAlarm(User user, int page, int size) {
        Pageable pageable = getPageable(page, size);
        Page<NotificationResponseForUser> notifications = notificationRepository.findAdminNotification(user.getId(), pageable);
        return ResponseDto.success(notifications);
    }


    private Pageable getPageable(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created_at");
        return PageRequest.of(page - 1, size, sort);
    }

    @Transactional
    public ResponseDto<String> notificationRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundNotification));
        notification.read();
        return ResponseDto.success("알림 읽기 완료");
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<UserSupplyDto>> getCommonSupply(User user, LargeCategory largeCategory) {
        Set<LargeCategory> categoryQuery = getCategoryQuery(largeCategory);
        List<Supply> commonSupplyList = supplyRepository.
                findByDepartmentAndCategory_LargeCategoryInAndDeletedFalseOrderByCategory_CategoryNameAsc(user.getDepartment(), categoryQuery);
        return ResponseDto.success(convertToDtoList(commonSupplyList));
    }

    private List<UserSupplyDto> convertToDtoList(List<Supply> commonSupplyList) {
        List<UserSupplyDto> supplyDtoList = new ArrayList<>();
        for (Supply supply : commonSupplyList) {
            supplyDtoList.add(UserSupplyDto.of(supply));
        }
        return supplyDtoList;
    }
}
