package com.sparta.bipuminbe.supply.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.partners.repository.PartnersRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.dto.*;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
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


    //비품 등록
    @Transactional
    public ResponseDto<String> createSupply(SupplyRequestDto supplyRequestDto){

        Partners partners = null;
        if(supplyRequestDto.getPartnersId() != null) {
            partners = partnersRepository.findById(supplyRequestDto.getPartnersId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundPartners)
            );
        }
        Supply newSupply = new Supply(supplyRequestDto, partners);
        supplyRepository.save(newSupply);

        return ResponseDto.success("비품 등록 성공");
    }


    //비품 조회
    @Transactional(readOnly = true)
    public ResponseDto<List<SupplyResponseDto>> getSupplyList(int categoryId) {
        List<Supply> supplyList = supplyRepository.findAllByCategoryId(categoryId);
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
                () -> new EntityNotFoundException("비품을 찾을 수 없습니다.")
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
                () -> new IllegalArgumentException("해당 비품이 존재하지 않습니다.")
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")
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
                () -> new EntityNotFoundException("비품을 찾을 수 없습니다.")
        );
        supplyRepository.delete(supply);
        return ResponseDto.success("비품 삭제 성공");
    }


    //자신의 비품 목록(selectbox용)

}
