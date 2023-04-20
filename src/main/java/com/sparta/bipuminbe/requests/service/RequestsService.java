package com.sparta.bipuminbe.requests.service;

import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.*;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.s3.S3Uploader;
import com.sparta.bipuminbe.common.sse.repository.NotificationRepository;
import com.sparta.bipuminbe.common.util.sms.SmsUtil;
import com.sparta.bipuminbe.requests.dto.*;
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
    private final NotificationRepository notificationRepository;

    @Transactional
    public RequestsResponseDto createRequests(RequestsRequestDto requestsRequestDto, User user) throws Exception {
        Long requestId;
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
        if (requestsRequestDto.getRequestType().name().equals("SUPPLY")) {
            Category category = getCategory(requestsRequestDto.getCategoryId());
            if (requestsRequestDto.getUseType() == null) {
                throw new CustomException(ErrorCode.NotAllowedMethod);
            }
            Requests createRequests = requestsRepository.save(Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .requestType(requestsRequestDto.getRequestType())
                    .requestStatus(RequestStatus.UNPROCESSED)
                    .category(category)
                    .user(user)
                    .useType(requestsRequestDto.getUseType())
                    .department(requestsRequestDto.getUseType() == UseType.COMMON ? user.getDepartment() : null)
                    .build());

            requestId = createRequests.getRequestId();
        } else {
            Supply supply = getSupply(requestsRequestDto.getSupplyId());

            // 요청 중인 건이면 예외 발생
            if (requestsRepository.existsBySupply_SupplyIdAndRequestStatusNot(supply.getSupplyId(), RequestStatus.PROCESSED)) {
                throw new CustomException(ErrorCode.isProcessingRequest);
            }
            // s3 폴더 이름
            String dirName = requestsRequestDto.getRequestType().name().toLowerCase() + "images";

            List<MultipartFile> multipartFiles = requestsRequestDto.getMultipartFile();

            Requests requests = Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .requestType(requestsRequestDto.getRequestType())
                    .requestStatus(RequestStatus.UNPROCESSED)
                    .user(user)
                    .supply(supply)
                    .useType(supply.getUseType())
                    .department(requestsRequestDto.getRequestType() == RequestType.RETURN ? supply.getDepartment() : null)
                    .build();

            Requests createRequests = requestsRepository.save(requests);
            requestId = createRequests.getRequestId();

            // image Null check. 요청 등록 시에는 이미지가 필수이다.
            checkNullImageList(multipartFiles, null);
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

        List<User> adminList = userRepository.findByRoleAndAlarmAndDeletedFalse(UserRoleEnum.ADMIN, true);
        if (adminList != null) {
            List<String> phoneList = new ArrayList<>();
            for (User admin : adminList) {
                phoneList.add(admin.getPhone());
            }
            String mail = "[비품인]\n" + requestsRequestDto.getRequestType() + " 건이 도착했습니다.";
//            smsUtil.sendMail(mail, phoneList);
        }

        return RequestsResponseDto.builder()
                .requestsId(requestId)
                .message(message)
                .build();
    }

    @Transactional
    public ResponseDto<String> updateRequests(Long requestId, RequestsRequestDto requestsRequestDto, User user) throws IOException {
        Requests requests = getRequest(requestId);

        // 본인의 요청인지 확인
        checkPermission(requests, user);

        // 처리 전 요청인지 확인
        checkProcessing(requests);

        if (requestsRequestDto.getRequestType().name().equals("SUPPLY")) {
            requests.update(Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .build());
        } else {
            // 해당 요청의 이미지 리스트 가져오기
            List<Image> imageList = imageRepository.findImagesByRequests(requests).orElseThrow(
                    () -> new CustomException(ErrorCode.NullImageList));

            List<String> storedImageURLs = requestsRequestDto.getStoredImageURLs();
            List<MultipartFile> multipartFiles = requestsRequestDto.getMultipartFile();
            checkNullImageList(multipartFiles, storedImageURLs);

            // 이미지들 삭제
            if (storedImageURLs != null) {
                for (Image image : imageList) {
                    // DB에 들어있는 이미지 URL은 삭제하지 않는다.
                    if (storedImageURLs.contains(image.getImage())) {
                        continue;
                    }
//                //S3 내 이미지 파일 삭제
//                s3Uploader.deleteFile(image);

                    // DB 내 이미지 삭제
                    imageRepository.deleteById(image.getId());
                }
            } else {
                imageRepository.deleteAll(imageList);
            }

            String dirName = requestsRequestDto.getRequestType().name().toLowerCase() + "images";

            requests.update(Requests.builder()
                    .content(requestsRequestDto.getContent())
                    .build());

            // 추가하는 이미지가 있을 경우에만 s3에 저장한다.
            if (multipartFiles != null) {
                //s3에 저장
                for (MultipartFile multipartFile : multipartFiles) {
                    String image = s3Uploader.uploadFiles(multipartFile, dirName);
                    imageRepository.save(Image.builder()
                            .image(image)
                            .requests(requests)
                            .build());
                }
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

        List<Notification> notificationList = notificationRepository.findByRequest_RequestId(requestId);
        notificationRepository.deleteAll(notificationList);
        requestsRepository.deleteById(requestId);

        return ResponseDto.success("요청 삭제 완료");
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RequestsPageResponseDto>> getRequestsPage
            (String keyword, RequestType type, RequestStatus status, int page, int size, User user, UserRoleEnum role) {
        Set<RequestType> requestTypeQuery = getTypeSet(type);
        Set<RequestStatus> requestStatusQuery = getStatusSet(status);
        Set<Long> userIdQuery = getUserIdSet(user, role);
        Pageable pageable = getPageable(page, size);

        Page<Requests> requestsList = requestsRepository.
                getRequestsList(keyword, requestTypeQuery, requestStatusQuery, userIdQuery, pageable);

        List<RequestsPageResponseDto> requestsDtoList = convertToDto(requestsList.getContent());

        return ResponseDto.success(new PageImpl<>(requestsDtoList, requestsList.getPageable(), requestsList.getTotalElements()));
    }

    // 유저가 admin이면 전체 조회, user라면 자기꺼만 조회.
    private Set<Long> getUserIdSet(User user, UserRoleEnum role) {
        Set<Long> userQuery = new HashSet<>();
        if (role.equals(UserRoleEnum.ADMIN)) {
            List<User> userList = userRepository.findAll();
            for (User foundUser : userList) {
                userQuery.add(foundUser.getId());
            }
        } else {
            userQuery.add(user.getId());
        }
        return userQuery;
    }

    // list 추출 조건용 requestType Set 리스트.
    private Set<RequestType> getTypeSet(RequestType type) {
        Set<RequestType> requestTypeQuery = new HashSet<>();
        if (type == null) {
            requestTypeQuery.addAll(List.of(RequestType.values()));
        } else {
            requestTypeQuery.add(type);
        }
        return requestTypeQuery;
    }

    // list 추출 조건용 requestStatus Set 리스트.
    private Set<RequestStatus> getStatusSet(RequestStatus status) {
        Set<RequestStatus> requestStatusQuery = new HashSet<>();
        if (status == null) {
            requestStatusQuery.addAll(List.of(RequestStatus.values()));
        } else {
            requestStatusQuery.add(status);
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
    public ResponseDto<RequestsAdminDetailsResponseDto> getRequestsAdminDetails(Long requestId) {
        return ResponseDto.success(RequestsAdminDetailsResponseDto.of(getRequest(requestId)));
    }

    @Transactional(readOnly = true)
    public ResponseDto<RequestsDetailsResponseDto> getRequestsDetails(Long requestId, User user) {
        Requests request = getRequest(requestId);
        checkPermission(request, user);
        return ResponseDto.success(RequestsDetailsResponseDto.of(request));
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
    public String processingRequests(Long requestId, RequestsProcessRequestDto requestsProcessRequestDto, User user) throws Exception {
        Requests request = getRequest(requestId);
        AcceptResult acceptResult = requestsProcessRequestDto.getAcceptResult();
        checkProcessedRequest(request);
        checkAcceptResult(acceptResult, request.getRequestType());

        Supply supply = request.getRequestType().equals(RequestType.SUPPLY)
                ? requestsProcessRequestDto.getSupplyId() == null ? null : getSupply(requestsProcessRequestDto.getSupplyId())
                : request.getSupply();

        // 요청 상태 처리.
        request.processingRequest(acceptResult, requestsProcessRequestDto.getComment(), supply, user);

        String message = "[비품인]\n" + request.getUser().getEmpName() +
                " 님이 요청 하신 " + request.getRequestType().getKorean();

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
                supply.allocateSupply(request, request.getUser().getDepartment());
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
            smsUtil.sendMail(message, phoneList);
        }
        return message;
    }

    private void checkProcessedRequest(Requests request) {
        if (request.getRequestStatus() == RequestStatus.PROCESSED) {
            throw new CustomException(ErrorCode.ProcessedRequest);
        }
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
        return supplyRepository.findBySupplyIdAndDeletedFalse(supplyId).orElseThrow(
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

    private void checkNullImageList(List<MultipartFile> multipartFiles, List<String> storedImageURLs) {
        if (multipartFiles == null && storedImageURLs == null) {
            throw new CustomException(ErrorCode.NullImageList);
        }
    }
}
