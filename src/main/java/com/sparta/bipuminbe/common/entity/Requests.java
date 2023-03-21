package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
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


    public void processingRequest(AcceptResult acceptResult, String comment) {
        // 처리중 상태 처리.
        if (this.requestStatus.equals(RequestStatus.UNPROCESSED) && acceptResult.equals(AcceptResult.ACCEPT)) {
            this.requestStatus = RequestStatus.PROCESSING;
        } else {
            this.acceptResult = acceptResult;
            this.requestStatus = RequestStatus.PROCESSED;
            this.comment = comment;
        }
    }

    @Builder
    public Requests(String content, String image, RequestType requestType, RequestStatus requestStatus,
                    Supply supply, User user, Category category){
        this.content = content;
        this.image = image;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.supply = supply;
        this.user = user;
        this.category = supply.getCategory();
    }
}
