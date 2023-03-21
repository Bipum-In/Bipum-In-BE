package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Requests extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Column(nullable = false)
    private String content;

    private String image;

    private String comment;

    @Enumerated(EnumType.STRING)
    private AcceptResult acceptResult;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplyId")
    private Supply supply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;


    @Builder
    public Requests(String content, String image, RequestType requestType, RequestStatus requestStatus,
                    Supply supply, User user, Category category) {
        this.content = content;
        this.image = image;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.supply = supply;
        this.user = user;
        this.category = supply.getCategory();
    }

    public void processingRequest(AcceptResult acceptResult, String comment) {
        checkAcceptResult(acceptResult);
        checkNullComment(acceptResult, comment);

        // 처리중 상태 처리.
        if (this.requestType.equals(RequestType.REPAIR) && acceptResult.equals(AcceptResult.ACCEPT)
                && this.requestStatus.equals(RequestStatus.UNPROCESSED)) {
            this.requestStatus = RequestStatus.PROCESSING;
            return;
        }

        this.acceptResult = acceptResult;
        this.requestStatus = RequestStatus.PROCESSED;
        this.comment = comment;
    }

    // 거절시 거절 사유 작성은 필수다.
    private void checkNullComment(AcceptResult acceptResult, String comment) {
        if (acceptResult.equals(AcceptResult.DECLINE) && (comment == null || comment.equals(""))) {
            throw new CustomException(ErrorCode.NullComment);
        }
    }

    // 폐기는 수리 요청에만 존재해야 한다.
    private void checkAcceptResult(AcceptResult acceptResult) {
        if (acceptResult.equals(AcceptResult.DISPOSE) && !this.requestType.equals(RequestType.REPAIR)) {
            throw new CustomException(ErrorCode.NotAllowedMethod);
        }
    }
}
