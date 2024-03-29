package com.sparta.bipuminbe.supply.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.*;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.s3.S3Uploader;
import com.sparta.bipuminbe.department.repository.DepartmentRepository;
import com.sparta.bipuminbe.partners.repository.PartnersRepository;
import com.sparta.bipuminbe.requests.repository.ImageRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.dto.*;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class SupplyService {
    private final ImageRepository imageRepository;
    private final DepartmentRepository departmentRepository;
    private final SupplyRepository supplyRepository;
    private final UserRepository userRepository;
    private final PartnersRepository partnersRepository;
    private final RequestsRepository requestsRepository;
    private final CategoryRepository categoryRepository;

    @Value("${Naver.Client.ID}")
    private String naverClientId;
    @Value("${Naver.Client.Secret}")
    private String naverClientSecret;

    private final S3Uploader s3Uploader;

    //비품 등록
    @Transactional
    public Requests createSupply(SupplyRequestDto supplyRequestDto, User admin) throws IOException {

        if (supplyRepository.existsBySerialNum(supplyRequestDto.getSerialNum())) {
            throw new CustomException(ErrorCode.DuplicateSerialNum);
        }

        Partners partners = null;
        if (supplyRequestDto.getPartnersId() != null) {
            partners = partnersRepository.findByPartnersIdAndDeletedFalse(supplyRequestDto.getPartnersId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundPartners));
        }

        String image = supplyRequestDto.getImage();

        if (image == null || image.equals("")) {
            image = s3Uploader.uploadFiles(supplyRequestDto.getMultipartFile(), supplyRequestDto.getCategoryName());
        }

        Optional<Category> category = categoryRepository.findByCategoryName(supplyRequestDto.getCategoryName());

        Category newCategory = null;
        if (category.isPresent()) {
            newCategory = category.get();
        } else {
            newCategory = Category.builder().largeCategory(supplyRequestDto.getLargeCategory())
                    .categoryName(supplyRequestDto.getCategoryName())
                    .deleted(false).build();
            categoryRepository.save(newCategory);
        }

        //Todo 여기 조금 수정 되서 바꾸게 되었습니다.
        User user = null;
        if (supplyRequestDto.getUseType() == UseType.PERSONAL) {
            user = userRepository.findByIdAndDeletedFalse(supplyRequestDto.getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundUsers));
        }

        Department department = null;
        if (supplyRequestDto.getUseType() == UseType.COMMON) {
            department = departmentRepository.findByIdAndDeletedFalse(supplyRequestDto.getDeptId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundDepartment));
        }

        // 폐기된 비품 중 같은 SerialNum 존재 하는지 체크.
        // 존재한다면 재등록 비품 처리. (이름 및 시리얼 넘버만 변경.)
        checkDeletedSupply(supplyRequestDto.getSerialNum());

        Supply newSupply = Supply.builder()
                .serialNum(supplyRequestDto.getSerialNum())
                .modelName(supplyRequestDto.getModelName())
                .image(image)
                .status(supplyRequestDto.getUseType() == null ? SupplyStatusEnum.STOCK : SupplyStatusEnum.USING)
                .partners(partners)
                .user(user)
                .category(newCategory)
                .useType(supplyRequestDto.getUseType())
                .department(department)
                .deleted(false)
                .build();
        supplyRepository.save(newSupply);

        // user history 기록 생성.
        if (supplyRequestDto.getUseType() != null) {
            Requests requests = requestsRepository.save(Requests.builder()
                    .requestType(RequestType.SUPPLY)
                    .content("비품 사용 유저 내역을 위한 기록 생성.")
                    .acceptResult(AcceptResult.ACCEPT)
                    .requestStatus(RequestStatus.PROCESSED)
                    .supply(newSupply)
                    .user(supplyRequestDto.getUseType() == UseType.COMMON ? admin : user)
                    .category(newCategory)
                    .useType(supplyRequestDto.getUseType())
                    .department(supplyRequestDto.getUseType() == UseType.COMMON ? department : null)
                    .admin(admin)
                    .build());

            return requests;
        }

        return null;
    }

    // 폐기된 비품 중 같은 SerialNum 존재 하는지 체크.
    // 존재한다면 재등록 비품 처리. (이름 및 시리얼 넘버만 변경.)
    private void checkDeletedSupply(String serialNum) {
        Optional<Supply> deletedSupply = supplyRepository.findBySerialNumAndDeletedTrue(serialNum);
        if (deletedSupply.isPresent()) {
            deletedSupply.get().reEnroll();
        }
    }


    //비품 조회
    @Transactional(readOnly = true)
    public ResponseDto<Page<SupplyResponseDto>> getSupplyList(String keyword, Long categoryId, SupplyStatusEnum status, int page, int size) {
        Set<Long> categoryQuery = getCategoryQuerySet(categoryId);
        Set<SupplyStatusEnum> statusQuery = getStatusSet(status);
        Pageable pageable = getPageable(page, size);

        Page<Supply> supplies = supplyRepository.getSupplyList(keyword, categoryQuery, statusQuery, pageable);
        List<SupplyResponseDto> supplyResponseDtoList = converToDto(supplies.getContent());
        return ResponseDto.success(new PageImpl<>(supplyResponseDtoList, supplies.getPageable(), supplies.getTotalElements()));
    }

    // 조회할 카테고리 영역.
    private Set<Long> getCategoryQuerySet(Long categoryId) {
        Set<Long> categoryQuerySet = new HashSet<>();
        if (categoryId == null) {
            List<Category> categoryList = categoryRepository.findByDeletedFalse();
            for (Category category : categoryList) {
                categoryQuerySet.add(category.getId());
            }
        } else {
            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundCategory));
            categoryQuerySet.add(category.getId());
        }
        return categoryQuerySet;
    }

    // list 추출 조건용 requestStatus Set 리스트.
    private Set<SupplyStatusEnum> getStatusSet(SupplyStatusEnum status) {
        Set<SupplyStatusEnum> requestStatusQuery = new HashSet<>();
        if (status == null) {
            requestStatusQuery.addAll(List.of(SupplyStatusEnum.values()));
        } else {
            requestStatusQuery.add(status);
        }
        return requestStatusQuery;
    }

    private Pageable getPageable(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(page - 1, size, sort);
    }

    private List<SupplyResponseDto> converToDto(List<Supply> supplyList) {
        List<SupplyResponseDto> supplyDtoList = new ArrayList<>();
        for (Supply supply : supplyList) {
            supplyDtoList.add(SupplyResponseDto.of(supply));
        }
        return supplyDtoList;
    }


    //비품 상세
    @Transactional(readOnly = true)
    public ResponseDto<SupplyWholeResponseDto> getSupply(Long supplyId, int size, User user, UserRoleEnum role) {

        Supply supply = getSupply(supplyId);
        // 영속성 걸어야 한다. 안에서 department를 씀.
        User foundUser = getUser(user.getId());
        SupplyDetailResponseDto supplyDetail = new SupplyDetailResponseDto(supply, foundUser, role);

        // 여기 좀 힘들어 하실 것 같아서 page처리 해놨습니다.
        // 처음엔 1페이지를 가져오는 것은 고정이다.
        Page<SupplyHistoryResponseDto> userHistory = getUserHistory(supplyId, 1, size).getData();
        Page<SupplyHistoryResponseDto> repairHistory = getRepairHistory(supplyId, 1, size).getData();
        SupplyWholeResponseDto supplyWhole = SupplyWholeResponseDto.of(supplyDetail, userHistory, repairHistory);
        return ResponseDto.success(supplyWhole);
    }

    private User getUser(Long id) {
        return userRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundUser));
    }

    private Supply getSupply(Long supplyId) {
        return supplyRepository.findById(supplyId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundSupply)
        );
    }


    //비품 수정
    @Transactional
    public List<Requests> updateSupplies(Long supplyId, SupplyRequestDto supplyRequestDto, User admin) throws IOException {
        Partners partners = null;
        if (supplyRequestDto.getPartnersId() != null) {
            partners = partnersRepository.findByPartnersIdAndDeletedFalse(supplyRequestDto.getPartnersId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundPartners)
            );
        }

        Supply supply = getSupply(supplyId);

        Optional<Category> category = categoryRepository.findByCategoryName(supplyRequestDto.getCategoryName());

        Category newCategory = null;
        if (category.isPresent()) {
            newCategory = category.get();
        } else {
            newCategory = Category.builder().largeCategory(supplyRequestDto.getLargeCategory())
                    .categoryName(supplyRequestDto.getCategoryName()).deleted(false).build();
            categoryRepository.save(newCategory);
        }

        String image = supplyRequestDto.getImage();

        if (image == null) {
            image = s3Uploader.uploadFiles(supplyRequestDto.getMultipartFile(), supplyRequestDto.getCategoryName());
        }

        supply.update(partners, image);

        // 여기 조금 수정 되서 바꾸게 되었습니다.
        User user = null;
        if (supplyRequestDto.getUseType() == UseType.PERSONAL) {
            user = userRepository.findByIdAndDeletedFalse(supplyRequestDto.getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundUsers));
        }

        Department department = null;
        if (supplyRequestDto.getUseType() == UseType.COMMON) {
            department = departmentRepository.findByIdAndDeletedFalse(supplyRequestDto.getDeptId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundDepartment));
        }

        // 두 개의 requests를 담기 위한 Map 생성
        List<Requests> requests = new ArrayList<>();

        // 비품 history에 기록이 남으려면 요청서도 생성해 줘야 한다.
        // 사용자 전환 check (뒤에 있는 부분은 공용에서 공용으로 전환될때를 고려하였다.)
        if (user != supply.getUser() || department != supply.getDepartment()) {
            String content = "비품의 유저 변경에 의한 기록 생성.";
            // 반납 요청 먼저 생성. (기록용)
            if (supply.getUseType() != null) {
                Requests request = requestsRepository.save(Requests.builder()
                        .content(content)
                        .requestType(RequestType.RETURN)
                        .requestStatus(RequestStatus.PROCESSED)
                        .acceptResult(AcceptResult.ACCEPT)
                        .supply(supply)
                        .user(supply.getUseType() == UseType.COMMON ? admin : supply.getUser())
                        .useType(supply.getUseType())
                        .department(supply.getUseType() == UseType.COMMON ? supply.getDepartment() : null)
                        .admin(admin)
                        .build());

                imageRepository.save(Image.builder()
                        .image(supply.getImage())
                        .requests(request)
                        .build());

                supply.returnSupply();

                // 반납 요청 담기
                requests.add(request);
            }

            // 다음 유저 비품 요청 생성. (기록용)
            if (supplyRequestDto.getUseType() != null) {
                Requests request = requestsRepository.save(Requests.builder()
                        .content(content)
                        .requestType(RequestType.SUPPLY)
                        .requestStatus(RequestStatus.PROCESSED)
                        .acceptResult(AcceptResult.ACCEPT)
                        .supply(supply)
                        .user(supplyRequestDto.getUseType() == UseType.COMMON ? admin : user)
                        .category(newCategory)
                        .useType(supplyRequestDto.getUseType())
                        .department(supplyRequestDto.getUseType() == UseType.COMMON ? department : null)
                        .admin(admin)
                        .build());

                supply.allocateSupply(request, department);

                // 비품 요청 담기
                requests.add(request);
            }
        }

        return requests;
    }


    //비품 폐기
    @Transactional
    public Requests deleteSupply(Long supplyId, User admin) {

        Supply supply = getSupply(supplyId);

        // 비품 폐기 처리 기록 생성.
        String content = "비품 폐기 처리에 대한 기록 생성.";
        Requests request = requestsRepository.save(Requests.builder()
                .content(content)
                .requestType(RequestType.REPAIR)
                .requestStatus(RequestStatus.PROCESSED)
                .acceptResult(AcceptResult.DISPOSE)
                .supply(supply)
                .useType(supply.getUseType())
                .user(admin)
                .admin(admin)
                .build());

        imageRepository.save(Image.builder()
                .image(supply.getImage())
                .requests(request)
                .build());

        // 폐기될 비품에 걸려있는 요청 거절 처리.
        List<Requests> requestList = requestsRepository.findBySupply_SupplyIdAndRequestStatusNot(supplyId, RequestStatus.PROCESSED);
        for (Requests requests : requestList) {
            requests.processingRequest(AcceptResult.DECLINE, content, null, admin);
        }
        supplyRepository.delete(supply);
        return request;
    }


    //자신의 비품 목록(selectbox용)
    @Transactional(readOnly = true)
    public ResponseDto<List<SupplyUserDto>> getSupplyUser(Long categoryId, User user) {
        // 요청이 걸려있는 비품은 가져오지 않는다.
        List<Supply> supplyInUserList = supplyRepository.getMySupply(user, categoryId, getNotProcessedStatusQuery());
        return ResponseDto.success(convertToMySupplyDtoList(supplyInUserList));
    }

    // 다른 요청이 처리 중인 비품을 찾기 위한 statusQuery
    private Set<RequestStatus> getNotProcessedStatusQuery() {
        Set<RequestStatus> statusQuery = new HashSet<>();
        statusQuery.add(RequestStatus.UNPROCESSED);
        statusQuery.add(RequestStatus.PROCESSING);
        return statusQuery;
    }

    // MySupplyDtoList로 Convert
    private List<SupplyUserDto> convertToMySupplyDtoList(List<Supply> supplyInUserList) {
        List<SupplyUserDto> supplyUserDtoList = new ArrayList<>();
        for (Supply supply : supplyInUserList) {
            supplyUserDtoList.add(SupplyUserDto.of(supply));
        }
        return supplyUserDtoList;
    }


    // 자신의 부서의 공용 비품 목록(selectBox용)
    @Transactional(readOnly = true)
    public ResponseDto<List<SupplyUserDto>> getMyCommonSupply(Long categoryId, User user) {
        // 요청이 걸려있는 비품은 가져오지 않는다.
        List<Supply> commonSupplyList = supplyRepository.getMyCommonSupply(user.getDepartment(), categoryId, getNotProcessedStatusQuery());
        return ResponseDto.success(convertToMySupplyDtoList(commonSupplyList));
    }


    // 비품 요청 상세 페이지. 재고 SelectBox.
    @Transactional(readOnly = true)
    public ResponseDto<List<StockSupplyResponseDto>> getStockSupply(Long categoryId) {
        List<Supply> stockSupplyList = supplyRepository.findByCategory_IdAndStatusAndDeletedFalse(categoryId, SupplyStatusEnum.STOCK);
        List<StockSupplyResponseDto> stockSupplyResponseDtoList = new ArrayList<>();
        for (Supply supply : stockSupplyList) {
            stockSupplyResponseDtoList.add(StockSupplyResponseDto.of(supply));
        }
        return ResponseDto.success(stockSupplyResponseDtoList);
    }


    // naver 이미지 서치
    @Transactional(readOnly = true)
    public ResponseDto<List<ImageResponseDto>> getImageByNaver(List<String> modelNameList) throws InterruptedException {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("X-Naver-Client-Id", naverClientId);
        headers.add("X-Naver-Client-Secret", naverClientSecret);
        String body = "";

        List<ImageResponseDto> imageResponseDtoList = new ArrayList<>();
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);

        for (int i = 0; i < modelNameList.size(); i++) {
            String modelName = modelNameList.get(i);
            String errorMessage = modelName + "의 ";
            Thread.sleep(75);
            ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?display=1&query="
                    + modelName, HttpMethod.GET, requestEntity, String.class);

            HttpStatus httpStatus = responseEntity.getStatusCode();
            int status = httpStatus.value();
            log.info("NAVER API Status Code : " + status);

            String response = responseEntity.getBody();
            imageResponseDtoList.add(fromJSONtoItems(modelNameList.size() != 1, errorMessage, response));
        }

        return ResponseDto.success(imageResponseDtoList);
    }


    // Naver 이미지 Json 처리.
    private ImageResponseDto fromJSONtoItems(Boolean isExcel, String errorMessage, String response) {
        JSONObject rjson = new JSONObject(response);
        JSONArray items = rjson.getJSONArray("items");
        if (items.length() == 0) {
            if (isExcel) {
                throw new CustomException.ExcelError(errorMessage, ErrorCode.InValidRequest);
            }
            throw new CustomException(ErrorCode.InValidRequest);
        }
        return ImageResponseDto.of(items.getJSONObject(0));
    }


    // 비품 재고 현황 (User 페이지)
    @Transactional(readOnly = true)
    public ResponseDto<Page<SupplyResponseDto>> getStockList(String keyword, Long categoryId, int page, int size) {
        return getSupplyList(keyword, categoryId, SupplyStatusEnum.STOCK, page, size);
    }


    // 비품 사용 유저 내역
    @Transactional(readOnly = true)
    public ResponseDto<Page<SupplyHistoryResponseDto>> getUserHistory(Long supplyId, int page, int size) {
        Set<RequestType> requestTypeQuery = new HashSet<>();
        requestTypeQuery.add(RequestType.SUPPLY);
        requestTypeQuery.add(RequestType.RETURN);
        Pageable pageable = getPageable(page, size);

        return ResponseDto.success(getHistoryDtoPage(supplyId, requestTypeQuery, pageable));
    }


    // 비품 수리 내역
    @Transactional(readOnly = true)
    public ResponseDto<Page<SupplyHistoryResponseDto>> getRepairHistory(Long supplyId, int page, int size) {
        Set<RequestType> requestTypeQuery = new HashSet<>();
        requestTypeQuery.add(RequestType.REPAIR);
        requestTypeQuery.add(RequestType.REPORT);
        Pageable pageable = getPageable(page, size);

        return ResponseDto.success(getHistoryDtoPage(supplyId, requestTypeQuery, pageable));
    }


    // 비품 history 조회
    private Page<SupplyHistoryResponseDto> getHistoryDtoPage(Long supplyId, Set<RequestType> requestTypeQuery, Pageable pageable) {
        Page<Requests> historyPage = requestsRepository.
                findBySupply_SupplyIdAndRequestTypeInAndAcceptResult(supplyId, requestTypeQuery, AcceptResult.ACCEPT, pageable);
        List<SupplyHistoryResponseDto> historyDtoPage = convertToHistoryDto(historyPage.getContent());
        return new PageImpl<>(historyDtoPage, historyPage.getPageable(), historyPage.getTotalElements());
    }


    // 비품 상세 history Dto 변환
    private List<SupplyHistoryResponseDto> convertToHistoryDto(List<Requests> historyList) {
        List<SupplyHistoryResponseDto> historyDtoPage = new ArrayList<>();
        for (Requests request : historyList) {
            historyDtoPage.add(SupplyHistoryResponseDto.of(request));
        }
        return historyDtoPage;
    }


    //비품 복수 등록
    @Transactional
    public List<Requests> createSupplies(ExcelCoverDto excelCoverDto, User admin) throws IOException {
        List<String> supplyExcelDtos = excelCoverDto.getJsonObjectList();
        List<MultipartFile> multipartFileList = excelCoverDto.getMultipartFileList();

        if (supplyExcelDtos == null || supplyExcelDtos.size() <= 1) {
            throw new CustomException(ErrorCode.ExcelAmountLessThanTwo);
        }

        List<Requests> requestsList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        int index = 0;
        for (int i = 0; i < supplyExcelDtos.size(); i++) {
            SupplyExcelDto supplyExcelDto = mapper.readValue(supplyExcelDtos.get(i), SupplyExcelDto.class);
            // error 메시지 비품 구분용.
            String numberMessage = supplyExcelDto.getModelName() + "의 ";

            checkDeletedSupply(supplyExcelDto.getSerialNum());
            if (supplyRepository.existsBySerialNum(supplyExcelDto.getSerialNum())) {
                throw new CustomException.ExcelError(numberMessage, ErrorCode.DuplicateSerialNum);
            }

            Category category = categoryRepository.findByCategoryNameAndDeletedFalse(supplyExcelDto.getCategory()).orElseThrow(
                    () -> new CustomException.ExcelError(numberMessage, ErrorCode.NotFoundCategory));

            Partners partners = null;
            if (supplyExcelDto.getPartners() != null && !supplyExcelDto.getPartners().equals("")) {
                partners = partnersRepository.findByPartnersNameAndDeletedFalse(supplyExcelDto.getPartners())
                        .orElseThrow(() -> new CustomException.ExcelError(numberMessage, ErrorCode.NotFoundPartners));
            }

            Department department = null;
            if (supplyExcelDto.getDeptName() != null && !supplyExcelDto.getDeptName().equals("")) {
                department = departmentRepository.findByDeptNameAndDeletedFalse(supplyExcelDto.getDeptName()).orElseThrow(
                        () -> new CustomException.ExcelError(numberMessage, ErrorCode.NotFoundDepartment));
            }

            User user = null;
            if (supplyExcelDto.getEmpName() != null && !supplyExcelDto.getEmpName().equals("")) {
                user = userRepository.findByEmpNameAndDepartment_DeptNameAndDeletedFalse
                        (supplyExcelDto.getEmpName(), supplyExcelDto.getDeptName()).orElseThrow(
                        () -> new CustomException.ExcelError(numberMessage, ErrorCode.NotFoundUser));
            }

            String image = supplyExcelDto.getImage();
            if (image == null || image.equals("")) {
                if (multipartFileList == null || index == multipartFileList.size()) {
                    throw new CustomException(ErrorCode.NotMatchedAmountImages);
                }
                s3Uploader.uploadFiles(multipartFileList.get(index++), supplyExcelDto.getCategory());
            }

            LocalDateTime createdAt = LocalDateTime.now();
            if (supplyExcelDto.getCreatedAt() != null && !supplyExcelDto.getCreatedAt().equals("")) {
                try {
                    createdAt = LocalDate.parse(supplyExcelDto.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
                } catch (Exception e) {
                    throw new CustomException.ExcelError(numberMessage, ErrorCode.InValidTimePattern);
                }
            }

            Supply supply = supplyRepository.save(Supply.builder()
                    .serialNum(supplyExcelDto.getSerialNum())
                    .modelName(supplyExcelDto.getModelName())
                    .image(image)
                    .partners(partners)
                    .user(user)
                    .category(category)
                    .useType(user == null ? department == null ? null : UseType.COMMON : UseType.PERSONAL)
                    .status(user == null && department == null ? SupplyStatusEnum.STOCK : SupplyStatusEnum.USING)
                    .department(user == null && department != null ? department : null)
                    .deleted(false)
                    .build());
            supply.changeCreatedAt(createdAt);

            // user history 기록 생성.
            if (supply.getUseType() != null) {
                Requests requests = requestsRepository.save(Requests.builder()
                        .requestType(RequestType.SUPPLY)
                        .content("비품 사용 유저 내역을 위한 기록 생성.")
                        .acceptResult(AcceptResult.ACCEPT)
                        .requestStatus(RequestStatus.PROCESSED)
                        .supply(supply)
                        .user(supply.getUseType() == UseType.COMMON ? admin : user)
                        .category(category)
                        .useType(supply.getUseType())
                        .department(supply.getUseType() == UseType.COMMON ? department : null)
                        .admin(admin)
                        .build());

                requestsList.add(requests);
            }
        }

        return requestsList;
    }
}
