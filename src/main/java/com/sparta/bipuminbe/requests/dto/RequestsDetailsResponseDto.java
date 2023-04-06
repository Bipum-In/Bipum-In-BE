package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class RequestsDetailsResponseDto {
    private Long requestId;
    private String requestType;
    private String acceptResult;
    private String requestStatus;

    private String categoryName;
    private String useType;
    private String modelName;
    private String serialNum;
    private String content;
    private List<String> imageList;

    private String comment;
    private String allocatedModel;
    private String allocatedImage;

    private String adminName;
    private String adminDeptName;
    private String adminPhone;
    private String adminImage;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static RequestsDetailsResponseDto of(Requests request) {
        Supply supply = request.getSupply();
        Category category = request.getCategory();
        User admin = request.getAdmin();
        Department adminDept = admin == null ? null : admin.getDepartment();

        List<String> imageList = new ArrayList<>();
        for (Image image : request.getImageList()) {
            imageList.add(image.getImage());
        }

        RequestsDetailsResponseDtoBuilder builder = RequestsDetailsResponseDto.builder()
                .requestId(request.getRequestId())
                .requestType(request.getRequestType().getKorean())
                .acceptResult(request.getAcceptResult() == null ? null : request.getAcceptResult().getKorean())
                .requestStatus(request.getRequestStatus().getKorean())

                .content(request.getContent())
                .imageList(imageList)

                .comment(request.getComment())

                .adminName(admin == null ? null : admin.getEmpName())
                .adminDeptName(admin == null ? null : adminDept.getDeptName())
                .adminPhone(admin == null ? null : admin.getPhone())
                .adminImage(admin == null ? null : admin.getImage())

                .createdAt(request.getCreatedAt())
                .modifiedAt(request.getModifiedAt());

        if (request.getRequestType().equals(RequestType.SUPPLY)) {
            builder.categoryName(category.getCategoryName())
                    .useType(request.getUseType().getKorean());

            if (request.getAcceptResult() == AcceptResult.ACCEPT) {
                Supply allocatedSupply = request.getSupply();
                builder.allocatedModel(allocatedSupply.getModelName())
                        .allocatedImage(allocatedSupply.getImage());
            }
        } else {
            category = supply.getCategory();
            builder.categoryName(category.getCategoryName())
                    .useType(supply.getUseType().getKorean())
                    .modelName(supply.getModelName())
                    .serialNum(supply.getSerialNum());
        }

        return builder.build();
    }
}
