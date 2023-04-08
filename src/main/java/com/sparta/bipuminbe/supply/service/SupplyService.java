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


    //비품 조회
    @Transactional(readOnly = true)
    public ResponseDto<Page<SupplyResponseDto>> getSupplyList(String keyword, Long categoryId, SupplyStatusEnum status, int page, int size) {
        Set<Long> categoryQuery = getCategoryQuerySet(categoryId);
        Set<SupplyStatusEnum> statusQuery = getStatusSet(status);
        Pageable pageable = getPageable(page, size);

        Page<Supply> supplies = supplyRepository.getSupplyList("%" + keyword + "%", categoryQuery, statusQuery, pageable);
        List<SupplyResponseDto> supplyResponseDtoList = converToDto(supplies.getContent());
        return ResponseDto.success(new PageImpl<>(supplyResponseDtoList, supplies.getPageable(), supplies.getTotalElements()));
    }

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
    public ResponseDto<SupplyWholeResponseDto> getSupply(Long supplyId, int size, User user, UserRoleEnum role) {

        Supply supply = getSupply(supplyId);
        SupplyDetailResponseDto supplyDetail = new SupplyDetailResponseDto(supply, user, role);
//        List<SupplyHistoryResponseDto> historyList = new ArrayList<>();
//        List<SupplyRepairHistoryResponseDto> repairHistoryList = new ArrayList<>();
//        List<Requests> requests = requestsRepository.findBySupply(supply);
//        for (Requests request : requests) {
//            historyList.add(SupplyHistoryResponseDto.of(request));
//            repairHistoryList.add(new SupplyRepairHistoryResponseDto(request.getSupply()));
//        }
//        SupplyWholeResponseDto supplyWhole = SupplyWholeResponseDto.of(supplyDetail, historyList, repairHistoryList);

        // Todo 여기 좀 힘들어 하실 것 같아서 page처리 해봤습니다.
        // 1페이지를 가져오는 것은 고정이다.
        Page<SupplyHistoryResponseDto> userHistory = getUserHistory(supplyId, 1, size).getData();
        Page<SupplyHistoryResponseDto> repairHistory = getRepairHistory(supplyId, 1, size).getData();
        SupplyWholeResponseDto supplyWhole = SupplyWholeResponseDto.of(supplyDetail, userHistory, repairHistory);
        return ResponseDto.success(supplyWhole);
    }

    private Supply getSupply(Long supplyId) {
        return supplyRepository.findById(supplyId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundSupply)
        );
    }

//    //유저 할당
//    @Transactional
//    public ResponseDto<String> updateSupply(Long supplyId, Long userId) {
//
//        Supply supply = getSupply(supplyId);
//
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new CustomException(ErrorCode.NotFoundUsers)
//        );
//
//        //Todo 여기 관리자 권한을 이미 Controller에서 Secured로 확인 했어서 필요없어 보입니다.
////        if (supply.getUser() != user) {
////            throw new CustomException(ErrorCode.NoPermission);
////        }
//
//        supply.allocateSupply(user);
//
//        return ResponseDto.success("비품 수정 성공");
//    }

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
                supply.returnSupply();

                // 반납 요청 담기
                requests.add(request);
            }

            // 다음 유저 비품 요청 생성. (기록용)
            if (supplyRequestDto.getUseType() != null) {
                Requests request = Requests.builder()
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
                        .build();
                requestsRepository.save(request);
                supply.allocateSupply(request, department);

                // 비품 요청 담기
                requests.add(request);
            }
        }

        return requests;
    }


    //비품 폐기
    @Transactional
    public Requests deleteSupply(Long supplyId, User user) {

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
                .user(user)
                .admin(user)
                .build());

        supplyRepository.delete(supply);
        return request;
    }


    //자신의 비품 목록(selectbox용)
    @Transactional(readOnly = true)
    public ResponseDto<List<SupplyUserDto>> getSupplyUser(Long categoryId, User user) {
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
    public ResponseDto<List<ImageResponseDto>> getImageByNaver(List<String> modelNameList) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("X-Naver-Client-Id", naverClientId);
        headers.add("X-Naver-Client-Secret", naverClientSecret);
        String body = "";

        List<ImageResponseDto> imageResponseDtoList = new ArrayList<>();
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);

        for (String modelName : modelNameList) {
            ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?display=1&query=" + modelName, HttpMethod.GET, requestEntity, String.class);

            HttpStatus httpStatus = responseEntity.getStatusCode();
            int status = httpStatus.value();
            log.info("NAVER API Status Code : " + status);

            String response = responseEntity.getBody();
            imageResponseDtoList.add(fromJSONtoItems(response));
        }

        return ResponseDto.success(imageResponseDtoList);
    }


    // Naver 이미지 Json 처리.
    private ImageResponseDto fromJSONtoItems(String response) {
        JSONObject rjson = new JSONObject(response);
        JSONArray items = rjson.getJSONArray("items");
        if (items.length() == 0) {
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

        List<Requests> requestsList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        int index = 0;
        for (String supplyExcelString : supplyExcelDtos) {
            SupplyExcelDto supplyExcelDto = mapper.readValue(supplyExcelString, SupplyExcelDto.class);
            Category category = categoryRepository.findByCategoryNameAndDeletedFalse(supplyExcelDto.getCategory()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundCategory));

            Partners partners = null;
            if (supplyExcelDto.getPartners() != null && !supplyExcelDto.getPartners().equals("")) {
                partners = partnersRepository.findByPartnersNameAndDeletedFalse(supplyExcelDto.getPartners())
                        .orElseThrow(() -> new CustomException(ErrorCode.NotFoundPartners));
            }

            Department department = null;
            if (supplyExcelDto.getDeptName() != null && supplyExcelDto.getDeptName().equals("")) {
                department = departmentRepository.findByDeptNameAndDeletedFalse(supplyExcelDto.getDeptName()).orElseThrow(
                        () -> new CustomException(ErrorCode.NotFoundDepartment));
            }

            User user = null;
            if (supplyExcelDto.getEmpName() != null && !supplyExcelDto.getEmpName().equals("")) {
                user = userRepository.findByEmpNameAndDepartment_DeptNameAndDeletedFalse
                        (supplyExcelDto.getEmpName(), supplyExcelDto.getDeptName()).orElseThrow(
                        () -> new CustomException(ErrorCode.NotFoundUser));
            }

            String image = supplyExcelDto.getImage() == null || supplyExcelDto.getImage().equals("") ?
                    s3Uploader.uploadFiles(multipartFileList.get(index++), supplyExcelDto.getCategory()) :
                    supplyExcelDto.getImage();

            LocalDateTime createdAt = LocalDateTime.now();
            if (supplyExcelDto.getCreatedAt() != null && !supplyExcelDto.getCreatedAt().equals("")) {
                try {
                    createdAt = LocalDate.parse(supplyExcelDto.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.InValidTimePattern);
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
                    .createdAt(createdAt)
                    .deleted(false)
                    .build());

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

        if (multipartFileList != null && index != multipartFileList.size()) {
            throw new CustomException(ErrorCode.NotMatchedAmountImages);
        }

        return requestsList;
    }
}
