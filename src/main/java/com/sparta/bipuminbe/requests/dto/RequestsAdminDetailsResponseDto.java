package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class RequestsAdminDetailsResponseDto {
    private Long requestId;
    private String requestType;
    private String requestStatus;
    private String acceptResult;

    private Long categoryId;
    private String categoryName;
    private String useType;
    private String modelName;
    private String serialNum;
    private String content;
    private List<String> imageList;

    private String userImage;
    private String deptName;
    private String empName;
    private String username;
    private String phone;

    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static RequestsAdminDetailsResponseDto of(Requests request) {
        User user = request.getUser();
        Department department = user.getDepartment();
        Supply supply = request.getSupply();
        Category category = request.getCategory();

        List<String> imageList = new ArrayList<>();
        for (Image image : request.getImageList()) {
            imageList.add(image.getImage());
        }

        RequestsAdminDetailsResponseDtoBuilder builder = RequestsAdminDetailsResponseDto.builder()
                .requestId(request.getRequestId())
                .requestType(request.getRequestType().getKorean())
                .requestStatus(request.getRequestStatus().getKorean())
                .acceptResult(request.getAcceptResult() == null ? null : request.getAcceptResult().getKorean())

                .useType(request.getUseType().getKorean())
                .content(request.getContent())
                .imageList(imageList)

                .userImage(user.getImage())
                .deptName(department.getDeptName())
                .empName(user.getEmpName())
                .username(user.getUsername())
                .phone(user.getPhone())

                .comment(request.getComment())
                .createdAt(request.getCreatedAt())
                .modifiedAt(request.getModifiedAt());

        if (request.getRequestType().equals(RequestType.SUPPLY)) {
            builder.categoryId(category == null ? null : category.getId())
                    .categoryName(category == null ? null : category.getCategoryName())
                    .modelName(request.getAcceptResult() == AcceptResult.ACCEPT ? supply.getModelName() : null)
                    .serialNum(request.getAcceptResult() == AcceptResult.ACCEPT ? supply.getSerialNum() : null);
        } else {
            category = supply.getCategory();
            builder.categoryName(category.getCategoryName())
                    .modelName(supply.getModelName())
                    .serialNum(supply.getSerialNum());
        }

        return builder.build();
    }
}
