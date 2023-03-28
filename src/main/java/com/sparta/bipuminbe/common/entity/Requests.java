package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private String comment;

    @Enumerated(EnumType.STRING)
    private AcceptResult acceptResult;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @OneToMany(mappedBy = "requests", cascade = CascadeType.ALL)
    private List<Image> imageList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplyId")
    private Supply supply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnersId")
    private Partners partners;

    @Builder
    public Requests(String content, List<Image> imageList, RequestType requestType, RequestStatus requestStatus,
                    Supply supply, User user, Category category, AcceptResult acceptResult, Partners partners) {
        this.content = content;
        this.imageList = imageList;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.supply = supply;
        this.user = user;
        this.category = supply == null ? category : supply.getCategory();
        this.acceptResult = acceptResult;
        this.partners = partners;
    }

    public void processingRequest(AcceptResult acceptResult, String comment, Supply supply) {
        // 처리중 상태 처리.
        if (this.requestType.equals(RequestType.REPAIR) && acceptResult.equals(AcceptResult.ACCEPT)
                && this.requestStatus.equals(RequestStatus.UNPROCESSED)) {
            this.requestStatus = RequestStatus.PROCESSING;
            this.partners = supply.getPartners();
            return;
        }

        // 비품 요청 에 할당한 supply 기록.
        if (this.requestType.equals(RequestType.SUPPLY) && acceptResult.equals(AcceptResult.ACCEPT)) {
            this.supply = supply;
        }

        this.acceptResult = acceptResult;
        this.requestStatus = RequestStatus.PROCESSED;
        this.comment = comment;
    }

    public void update(Requests requests) {
        this.content = requests.getContent();
        this.supply = requests.getSupply();
        this.category = requests.getSupply().getCategory();
    }
}
