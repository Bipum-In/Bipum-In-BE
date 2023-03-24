package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.s3.S3Uploader;
import com.sparta.bipuminbe.common.util.sms.SmsUtil;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.requests.dto.RequestsDetailsResponseDto;
import com.sparta.bipuminbe.requests.dto.RequestsProcessRequestDto;
import com.sparta.bipuminbe.requests.dto.RequestsPageResponseDto;
import com.sparta.bipuminbe.requests.repository.ImageRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;
    private final SmsUtil smsUtil;
    private final UserRepository userRepository;

    @Transactional
    public ResponseDto<String> createRequests(RequestsRequestDto requestsRequestDto, User user) throws Exception {

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

        if (requestsRequestDto.getRequestType().equals(RequestType.SUPPLY)) {
            Category category = getCategory(requestsRequestDto.getCategoryId());

            requestsRepository.save(Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .requestType(requestsRequestDto.getRequestType())
                    .requestStatus(RequestStatus.UNPROCESSED)
                    .category(category)
                    .user(user)
                    .build());
        } else {
            Supply supply = getSupply(requestsRequestDto.getSupplyId());

            // 요청 중인 건이면 예외 발생
            if (requestsRepository.existsBySupply_SupplyIdAndRequestStatusNot(supply.getSupplyId(), RequestStatus.PROCESSED)) {
                throw new CustomException(ErrorCode.isProcessingRequest);
            }
            // s3 폴더 이름
            String dirName = requestsRequestDto.getRequestType().name().toLowerCase() + "images";

            List<MultipartFile> multipartFiles = requestsRequestDto.getMultipartFile();

            // image Null check
            checkNullImageList(multipartFiles);

            Requests requests = Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .requestType(requestsRequestDto.getRequestType())
                    .requestStatus(RequestStatus.UNPROCESSED)
                    .user(user)
                    .supply(supply)
                    .category(supply.getCategory())
                    .build();

            requestsRepository.save(requests);

            //s3에 저장
            for (MultipartFile multipartFile : multipartFiles) {
                String image = s3Uploader.uploadFiles(multipartFile, dirName);
                imageRepository.save(Image.builder()
                        .image(image)
                        .requests(requests)
                        .build());
            }
        }

        String message = requestsRequestDto.getRequestType().equals(RequestType.REPORT) ?
                "보고서 제출 완료" :
                requestsRequestDto.getRequestType().getKorean() + " 완료";

        List<User> adminList = userRepository.findByRoleAndAlarm(UserRoleEnum.ADMIN, true);
        if (adminList != null) {
            List<String> phoneList = new ArrayList<>();
            for (User admin : adminList) {
                phoneList.add(admin.getPhone());
            }
            String mail = "[비품인]\n" + requestsRequestDto.getRequestType() + " 건이 도착했습니다.";
//            smsUtil.sendMail(mail, phoneList);
        }

        return ResponseDto.success(message);
    }

    @Transactional
    public ResponseDto<String> updateRequests(Long requestId, RequestsRequestDto requestsRequestDto, User user) throws IOException {
        Requests requests = getRequest(requestId);
        Category category = getCategory(requestsRequestDto.getCategoryId());

        // 본인의 요청인지 확인
        checkPermission(requests, user);

        // 처리 전 요청인지 확인
        checkProcessing(requests);

        if(requests.getRequestType().name().equals("SUPPLY")){
            Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .category(category)
                    .build();
        }else{
            // 해당 요청의 이미지 리스트 가져오기
            List<Image> imageList = imageRepository.findImagesByRequests(requests).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundImages));

            // 이미지들 삭제
            for(Image image : imageList){
//                //S3 내 이미지 파일 삭제
//                s3Uploader.deleteFile(image);

                // DB 내 이미지 삭제
                imageRepository.deleteById(image.getId());
            }

            Supply supply = getSupply(requestsRequestDto.getSupplyId());
            String dirName = requestsRequestDto.getRequestType().name().toLowerCase() + "images";

            List<MultipartFile> multipartFiles = requestsRequestDto.getMultipartFile();

            // image Null check
            checkNullImageList(multipartFiles);

            requests.update(Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .supply(supply)
                    .category(supply.getCategory())
                    .build());

            //s3에 저장
            for (MultipartFile multipartFile : multipartFiles) {
                String image = s3Uploader.uploadFiles(multipartFile, dirName);
                imageRepository.save(Image.builder()
                        .image(image)
                        .requests(requests)
                        .build());
            }
        }

        return ResponseDto.success("요청 수정 완료");
    }

    @Transactional
    public ResponseDto<String> deleteRequests(Long requestId, User user) {
        Requests requests = getRequest(requestId);

        // 본인의 요청인지 확인
        checkPermission(requests, user);

        // 처리 전 요청인지 확인
        checkProcessing(requests);

        requestsRepository.deleteById(requestId);

        return ResponseDto.success("요청 삭제 완료");
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RequestsPageResponseDto>> getRequestsPage(String keyword, String type, String status, int page, int size) {
        Set<RequestType> requestTypeQuery = getTypeSet(type);
        Set<RequestStatus> requestStatusQuery = getStatusSet(status);
        Pageable pageable = getPageable(page, size);
        Page<Requests> requestsList = requestsRepository.
                getRequestsList("%" + keyword + "%", requestTypeQuery, requestStatusQuery, pageable);

        List<RequestsPageResponseDto> requestsDtoList = convertToDto(requestsList.getContent());

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
        if (status.equals("ALL")) {
            requestStatusQuery.addAll(List.of(RequestStatus.values()));
        } else {
            requestStatusQuery.add(RequestStatus.valueOf(status));
        }
        return requestStatusQuery;
    }

    private Pageable getPageable(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(page - 1, size, sort);
    }

    private List<RequestsPageResponseDto> convertToDto(List<Requests> requestsList) {
        List<RequestsPageResponseDto> requestsDtoList = new ArrayList<>();
        for (Requests requests : requestsList) {
            requestsDtoList.add(RequestsPageResponseDto.of(requests));
        }
        return requestsDtoList;
    }

    @Transactional(readOnly = true)
    public ResponseDto<RequestsDetailsResponseDto> getRequestsDetails(Long requestId, User user) {
        Requests request = getRequest(requestId);
        checkPermission(request, user);
        return ResponseDto.success(RequestsDetailsResponseDto.of(request, user.getRole()));
    }

    // 해당 요청을 볼 권한 확인.
    private void checkPermission(Requests request, User user) {
        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !request.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NoPermission);
        }
    }

    // 해당 요청이 본인의 요청인지 확인.

    private Requests getRequest(Long requestId) {
        return requestsRepository.findById(requestId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest));
    }

    @Transactional
    public ResponseDto<String> processingRequests(RequestsProcessRequestDto requestsProcessRequestDto) throws Exception {
        Requests request = getRequest(requestsProcessRequestDto.getRequestId());
        AcceptResult acceptResult = AcceptResult.valueOf(requestsProcessRequestDto.getAcceptResult());
        checkAcceptResult(acceptResult, request.getRequestType());

        // 요청 상태 처리.
        request.processingRequest(acceptResult, requestsProcessRequestDto.getComment());

        String message = "[비품인]\n" + request.getUser().getEmpName() +
                " 님이 요청 하신 " + request.getRequestType().getKorean();
        Supply supply = request.getSupply();

        // 비품 상태 처리.
        if (acceptResult.equals(AcceptResult.DECLINE)) {
            checkNullComment(requestsProcessRequestDto.getComment());
            message += " 건이 거부 처리 되었습니다.";
        } else if (acceptResult.equals(AcceptResult.DISPOSE)) {
            supplyRepository.delete(supply);
            message += " 건의 비품이 폐기 처리 되었습니다.";
        } else {
            if (request.getRequestType().equals(RequestType.SUPPLY)) {
                checkSupplyId(requestsProcessRequestDto.getSupplyId());
                supply = getSupply(requestsProcessRequestDto.getSupplyId());
                supply.allocateSupply(request.getUser());
            } else if (request.getRequestType().equals(RequestType.REPAIR)) {
                supply.repairSupply();
            } else if (request.getRequestType().equals(RequestType.RETURN)) {
                supply.returnSupply();
            }

            if (request.getRequestType().equals(RequestType.REPAIR) && request.getRequestStatus().equals(RequestStatus.PROCESSED)) {
                message += " 건의 비품이 수리 완료 되었습니다.";
            } else {
                message += " 건이 승인 처리 되었습니다.";
            }
        }

        if (request.getUser().getAlarm()) {
            List<String> phoneList = new ArrayList<>();
            phoneList.add(request.getUser().getPhone());
//            smsUtil.sendMail(message, phoneList);
        }
        return ResponseDto.success(message);
    }

    // 거절시 거절 사유 작성은 필수다.
    private void checkNullComment(String comment) {
        if (comment == null || comment.equals("")) {
            throw new CustomException(ErrorCode.NullComment);
        }
    }

    // 폐기는 수리 요청에만 존재해야 한다.
    private void checkAcceptResult(AcceptResult acceptResult, RequestType requestType) {
        if (acceptResult.equals(AcceptResult.DISPOSE) && !requestType.equals(RequestType.REPAIR)) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }
    }

    // 비품 요청에는 supplyId도 같이 넘겨줘야 한다.
    private void checkSupplyId(Long supplyId) {
        if (supplyId == null) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }
    }

    private Supply getSupply(Long supplyId) {
        return supplyRepository.findById(supplyId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundSupply));
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundCategory));
    }

    private void checkProcessing(Requests requests) {
        if (!(requests.getRequestStatus().name().equals("UNPROCESSED"))) {
            throw new CustomException(ErrorCode.NotUnProcessedRequest);
        }
    }

    private void checkNullImageList(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            throw new CustomException(ErrorCode.NullImageList);
        }
    }
}
