package com.sparta.bipuminbe.supply.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
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

@Service
@RequiredArgsConstructor
public class SupplyService {
    private final SupplyRepository supplyRepository;
    private final UserRepository userRepository;
    private final PartnersRepository partnersRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    //Todo 나중에 볼게요.
    @Transactional
    public ResponseDto<ImageResponseDto> uploadFile(
            MultipartFile file
    ) {
        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return ResponseDto.success(ImageResponseDto.of("" + fileName));
    }

    @Transactional
    public void deleteFile(String fileName, UserDetailsImpl userDetailsImpl) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException("잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

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



    @Transactional(readOnly = true)
    public ResponseDto<List<SupplyResponseDto>> getSupplyList() {
        List<Supply> supplyList = supplyRepository.findAll();
        List<SupplyResponseDto> supplyDtoList = new ArrayList<>();
        for (Supply supply : supplyList) {

            //Todo 이거 나중에 of 메서드 만드시면 옮기시면 됩니다.
            supplyDtoList.add(new SupplyResponseDto(supply));
        }
        return ResponseDto.success(supplyDtoList);
    }


//    @Transactional(readOnly = true)
//    public SupplyWholeResponseDto getSupply(
//            Long supplyId){
//
//        Supply supply = supplyRepository.findById(supplyId).orElseThrow(
//                () -> new EntityNotFoundException("비품을 찾을 수 없습니다.")
//        );
//
//        List<Requests> requests = requestsRepository.findBySupplyId
//        List<SupplyHistoryResponseDto>
//        return SupplyWholeResponseDto(supply, );
//    }

    @Transactional
    public ResponseDto<String> updateSupply
            (Long supplyId,
             Long userId) {
        Supply supply = supplyRepository.findById(supplyId).orElseThrow(
                () -> new IllegalArgumentException("해당 비품이 존재하지 않습니다.")
        );
        //Todo Supply Entity update 메서드를 만들고, user를 집어넣고, status 사용중
        return ResponseDto.success("비품 수정 성공")
    }


    @Transactional
    public void deleteSupply(
            Long supplyId) {

        Supply supply = supplyRepository.findById(supplyId).orElseThrow(
                () -> new EntityNotFoundException("비품을 찾을 수 없습니다.")
        );

        if (supply.getUser() != user) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        supplyRepository.delete(supply);
    }



}
