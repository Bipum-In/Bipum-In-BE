package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UseType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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

    // 요청서 타입.
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    // 요청 내용.
    @Column(nullable = false)
    private String content;

    // Admin 메시지
    private String comment;

    // 승인 결과.
    @Enumerated(EnumType.STRING)
    private AcceptResult acceptResult;

    // 처리 상태.
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "requests", cascade = CascadeType.ALL)
    private List<Image> imageList = new ArrayList<>();

    // 비품 요청은 supply가 null.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplyId")
    private Supply supply;

    // 요청인.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // 비품 요청의 경우 category를 받는다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    // 비품 요청의 경우 개인/공용을 고른다.(Dto에서 추출)
    // 그 외 요청도 저장은 모두가 한다. (supply에서 추출)
    @Enumerated(EnumType.STRING)
    private UseType useType;

    // 공용일 경우, 비품/반납 요청시 부서를 저장한다. (history 불변을 위해...)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId")
    private Department department;

    // 수리 요청시 history 생성을 위해 requests 자체에 저장해 둬야한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnersId")
    private Partners partners;

    // 처리 담당자.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId")
    private User admin;

    @Builder
    public Requests(String content, List<Image> imageList, RequestType requestType, RequestStatus requestStatus, Supply supply, User user,
                    Category category, AcceptResult acceptResult, UseType useType, Department department, Partners partners, User admin) {
        this.content = content;
        this.imageList = imageList;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.supply = supply;
        this.user = user;
        this.category = category;
        this.acceptResult = acceptResult;
        this.useType = useType;
        this.department = department;
        this.partners = partners;
        this.admin = admin;
    }


    // 요청 처리.
    public void processingRequest(AcceptResult acceptResult, String comment, Supply supply, User admin) {
        // 처리중 상태 처리.
        if (this.requestType.equals(RequestType.REPAIR) && acceptResult.equals(AcceptResult.ACCEPT)
                && this.requestStatus.equals(RequestStatus.UNPROCESSED)) {
            this.requestStatus = RequestStatus.PROCESSING;
            this.partners = supply.getPartners();
            this.admin = admin;
            return;
        }

        // 비품 요청 에 할당한 supply 기록.
        if (this.requestType.equals(RequestType.SUPPLY) && acceptResult.equals(AcceptResult.ACCEPT)) {
            this.supply = supply;
        }

        this.acceptResult = acceptResult;
        this.requestStatus = RequestStatus.PROCESSED;
        this.comment = comment;
        this.admin = admin;
    }


    public void update(Requests requests) {
        this.content = requests.getContent();
    }
}
