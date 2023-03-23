package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RequestsDetailsResponseDto {
    private Long requestId;
    private Boolean isAdmin;

    private String requestType;
    private String requestStatus;
    private String acceptResult;

    private Long categoryId;
    private String categoryName;
    private String modelName;
    private String serialNum;
    private String content;
    private String image;

    private String userImage;
    private String deptName;
    private String empName;
    private String username;

    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static RequestsDetailsResponseDto of(Requests request, UserRoleEnum role) {
        User user = request.getUser();
        Department department = user.getDepartment();
        Supply supply = request.getSupply();
        Category category = request.getCategory();

        RequestsDetailsResponseDtoBuilder builder = RequestsDetailsResponseDto.builder()
                .requestId(request.getRequestId())
                .isAdmin(role.equals(UserRoleEnum.ADMIN))

                .requestType(request.getRequestType().getKorean())
                .requestStatus(request.getRequestStatus().getKorean())
                .acceptResult(request.getAcceptResult() == null ? null : request.getAcceptResult().getKorean())

                .content(request.getContent())
//                .image(request.getImageList())

                .userImage(user.getImage())
                .deptName(department.getDeptName())
                .empName(user.getEmpName())
                .username(user.getUsername())

                .comment(request.getComment())
                .createdAt(request.getCreatedAt())
                .modifiedAt(request.getModifiedAt());

        if (request.getRequestType().equals(RequestType.SUPPLY)) {
            builder.categoryId(category.getId())
                    .categoryName(category.getCategoryName());
        } else {
            category = supply.getCategory();
            builder.categoryName(category.getCategoryName())
                    .modelName(supply.getModelName())
                    .serialNum(supply.getSerialNum());
        }

        return builder.build();
    }
}
