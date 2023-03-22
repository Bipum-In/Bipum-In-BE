package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.s3.S3Uploader;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.dto.RequestsResponseDto;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RequestsService {
    private final RequestsRepository requestsRepository;
    private final CategoryRepository categoryRepository;
    private final SupplyRepository supplyRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public ResponseDto<String> createRequests(RequestsRequestDto requestsRequestDto, User user) throws IOException {
        // s3 폴더 이름
        String dirName = requestsRequestDto.getRequestType().name() + "images";

//
//        //아래 코드 중복되는 것 합치기
//        if(requestsRequestDto.getRequestType().equals(RequestType.SUPPLY)){
//            Category category = categoryRepository.findById(requestsRequestDto.getCategoryId())
//                    .orElseThrow(() -> new CustomException(ErrorCode.NotFoundCategory));
//        }else{
//            Supply supply = supplyRepository.findById(requestsRequestDto.getSupplyId())
//                    .orElseThrow(() -> new CustomException(ErrorCode.NotFoundSupply));
//        }
//
//
//        requestsRepository.save(Requests.builder()
//                .content(requestsRequestDto.getContent())
//                .requestType(requestsRequestDto.getRequestType())
//                .requestStatus(RequestStatus.UNPROCESSED)
//                .user(user).build()
//        );

        if(requestsRequestDto.getRequestType().equals(RequestType.SUPPLY)){
            Category category = categoryRepository.findById(requestsRequestDto.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NotFoundCategory));

            requestsRepository.save(Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .requestType(requestsRequestDto.getRequestType())
                    .requestStatus(RequestStatus.UNPROCESSED)
                    .category(category)
                    .user(user)
                    .build());
        }else{
            Supply supply = supplyRepository.findById(requestsRequestDto.getSupplyId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NotFoundSupply));

            String image = s3Uploader.uploadFiles(requestsRequestDto.getMultipartFile(), dirName);

            requestsRepository.save(Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .requestType(requestsRequestDto.getRequestType())
                    .requestStatus(RequestStatus.UNPROCESSED)
                    .user(user)
                    .supply(supply)
                    .category(supply.getCategory())
                    .image(image)
                    .build());
        }

        String message = requestsRequestDto.getRequestType().equals(RequestType.REPORT) ?
                "보고서 제출 완료" :
                requestsRequestDto.getRequestType().getKorean() + " 완료";
        return ResponseDto.success(message);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RequestsResponseDto>> getRequests(String keyword, String type, String status, int page, int size) {
        Set<RequestType> requestTypeQuery = getTypeSet(type);
        Set<RequestStatus> requestStatusQuery = getStatusSet(status);
        Pageable pageable = getPageable(page, size);
        Page<Requests> requestsList = requestsRepository.
                    getRequestsList("%"+keyword+"%", requestTypeQuery, requestStatusQuery, pageable);

        List<RequestsResponseDto> requestsDtoList = convertToDto(requestsList.getContent());

        return ResponseDto.success(new PageImpl<>(requestsDtoList, requestsList.getPageable(), requestsList.getTotalElements()));
    }

    // list 추출 조건용 requestType Set 리스트.
    private Set<RequestType> getTypeSet(String type) {
        Set<RequestType> requestTypeQuery = new HashSet<>();
        if (type.equals("ALL")) {
            requestTypeQuery.addAll(List.of(RequestType.values()));
        } else {
            requestTypeQuery.add(RequestType.valueOf(type));
        }
        return requestTypeQuery;
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

    private List<RequestsResponseDto> convertToDto(List<Requests> requestsList) {
        List<RequestsResponseDto> requestsDtoList = new ArrayList<>();
        for (Requests requests : requestsList) {
            requestsDtoList.add(RequestsResponseDto.of(requests));
        }
        return requestsDtoList;
    }
}
