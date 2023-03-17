package com.sparta.bipuminbe.supply.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.partners.repository.PartnersRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.dto.*;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import com.sparta.bipuminbe.user.dto.UserResponseDto;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sparta.bipuminbe.common.enums.SupplyStatusEnum.USING;

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
    public ResponseDto<List<SupplyResponseDto>> getSupplyList(Long categoryId) {
        List<Supply> supplyList = supplyRepository.findByCategory_Id(categoryId);
        List<SupplyResponseDto> supplyDtoList = new ArrayList<>();
        for (Supply supply : supplyList) {
            supplyDtoList.add(SupplyResponseDto.of(supply));
        }
        return ResponseDto.success(supplyDtoList);
    }


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

        if (supply.getUser() != user) {
            throw new CustomException(ErrorCode.NoPermission);
        }

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
    public ResponseDto<List<SupplyUserDto>> getSupplyUser(Long userId) {
        List<Supply> supplyInUserList = supplyRepository.findByUser(getUser(userId));
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
