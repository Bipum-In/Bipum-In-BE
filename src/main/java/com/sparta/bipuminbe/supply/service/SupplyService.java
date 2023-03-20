package com.sparta.bipuminbe.supply.service;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.partners.repository.PartnersRepository;
import com.sparta.bipuminbe.requests.dto.RequestsResponseDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.dto.*;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class SupplyService {
    private final SupplyRepository supplyRepository;
    private final UserRepository userRepository;
    private final PartnersRepository partnersRepository;
    private final RequestsRepository requestsRepository;
    private final CategoryRepository categoryRepository;


    //비품 등록
    @Transactional
    public ResponseDto<String> createSupply(SupplyRequestDto supplyRequestDto){

        Partners partners = null;
        if(supplyRequestDto.getPartnersId() != null) {
            partners = partnersRepository.findById(supplyRequestDto.getPartnersId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundPartners)
            );
        }

        User user = null;
        if(supplyRequestDto.getUserId() != null) {
            user = userRepository.findById(supplyRequestDto.getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundUsers)
            );
        }

        Category category = categoryRepository.findById(supplyRequestDto.getCategoryId()).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundCategory)
        );

        Supply newSupply = new Supply(supplyRequestDto, partners, category, user);
        supplyRepository.save(newSupply);

        return ResponseDto.success("비품 등록 성공");
    }

    //비품 조회
    @Transactional(readOnly = true)
    public ResponseDto<Page<SupplyResponseDto>> getSupplyList(Long categoryId, String status, ing page, ing size) {
        List<Supply> supplyList = supplyRepository.findByCategory_Id(categoryId);
        List<SupplyResponseDto> supplyDtoList = new ArrayList<>();
        Set<RequestStatus> requestStatusQuery = getStatusSet(status);
        Pageable pageable = getPageable(page, size);
        Page<Supply> supplies = supplyRepository.findBySupplyInRequestStatusIn(requestStatusQuery, pageable);
        List<RequestsResponseDto> requestsDtoList = converToDto(requestsList.getContent());
        for (Supply supply : supplyList) {
            supplyDtoList.add(SupplyResponseDto.of(supply));
        }
        return ResponseDto.success(supplyDtoList);
    }

    // list 추출 조건용 requestStatus Set 리스트.
    private Set<RequestStatus> getStatusSet(String status) {
        Set<RequestStatus> requestStatusQuery = new HashSet<>();
        if(status.equals("ALL")){
            requestStatusQuery.addAll(List.of(RequestStatus.values()));
        }else{
            requestStatusQuery.add(RequestStatus.valueOf(status));
        }
        return requestStatusQuery;
    }

    private Pageable getPageable(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(page - 1, size, sort);
    }

    private List<RequestsResponseDto> converToDto(List<Requests> requestsList) {
        List<RequestsResponseDto> requestsDtoList = new ArrayList<>();
        for (Requests requests : requestsList) {
            requestsDtoList.add(RequestsResponseDto.of(requests));
        }
        return requestsDtoList;
    }

//    //비품 조회
//    @Transactional(readOnly = true)
//    public ResponseDto<SupplyCategoryDto> getSupplyCategory(Long categoryId) {
//        List<Category> categoryList = categoryRepository.findAll();
//        List<Supply> supplyList = supplyRepository.findByCategory_Id(categoryId);
//        List<SupplyResponseDto> supplyDtoList = new ArrayList<>();
//        for (Supply supply : supplyList) {
//            supplyDtoList.add(SupplyResponseDto.of(supply));
//        }
//        List<CategoryDto> categoryDtoList = new ArrayList<>();
//        for (Category category : categoryList) {
//            categoryDtoList.add(CategoryDto.of(category));
//        }
//
//        SupplyCategoryDto supplyCategory = SupplyCategoryDto.of(categoryDtoList,supplyDtoList);
//        return ResponseDto.success(supplyCategory);
//    }


    //비품 상세
    @Transactional(readOnly = true)
    public ResponseDto<SupplyWholeResponseDto> getSupply(Long supplyId){

        Supply supply = supplyRepository.findById(supplyId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundSupply)
        );
        SupplyDetailResponseDto supplyDetail = new SupplyDetailResponseDto(supply);
        List<SupplyHistoryResponseDto> historyList = new ArrayList<>();
        List<Requests> requests = requestsRepository.findBySupply(supply);
        for (Requests request : requests) {
            historyList.add(new SupplyHistoryResponseDto(request.getSupply()));
        }
        SupplyWholeResponseDto supplyWhole = SupplyWholeResponseDto.of(supplyDetail, historyList);
        return ResponseDto.success(supplyWhole);
    }

    //유저 할당
    @Transactional
    public ResponseDto<String> updateSupply(Long supplyId, Long userId) {

        Supply supply = supplyRepository.findById(supplyId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundSupply)
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundUsers)
        );

        //Todo 여기 관리자 권한을 이미 Controller에서 Secured로 확인 했어서 필요없어 보입니다.
//        if (supply.getUser() != user) {
//            throw new CustomException(ErrorCode.NoPermission);
//        }

        supply.allocateSupply(user);
        return ResponseDto.success("비품 수정 성공");
    }


    //비품 폐기
    @Transactional
    public ResponseDto<String> deleteSupply(Long supplyId) {

        Supply supply = supplyRepository.findById(supplyId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundSupply)
        );
        supplyRepository.delete(supply);
        return ResponseDto.success("비품 삭제 성공");
    }


    //자신의 비품 목록(selectbox용)
    @Transactional(readOnly = true)
    public ResponseDto<List<SupplyUserDto>> getSupplyUser(User user) {
        List<Supply> supplyInUserList = supplyRepository.findByUser(user);
        List<SupplyUserDto> supplyUserDtoList = new ArrayList<>();
        for (Supply supply : supplyInUserList) {
            supplyUserDtoList.add(SupplyUserDto.of(supply));
        }
        return ResponseDto.success(supplyUserDtoList);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundUsers));
    }
}
