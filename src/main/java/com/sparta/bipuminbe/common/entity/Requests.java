package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
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

    private Boolean isSelf;

    private Boolean isRead;

    private Boolean isAccepted;

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

    public void read() {
        this.isRead = true;
    }

    public void processingRequest(Boolean isAccepted) {
        this.isAccepted = isAccepted;

        if (isAccepted) {
            this.requestStatus = requestType.equals(RequestType.REPAIR) && requestStatus.equals(RequestStatus.UNPROCESSED)
                    ? RequestStatus.PROCESSING : RequestStatus.PROCESSED;
        } else {
            this.requestStatus = RequestStatus.PROCESSED;
        }
    }
}
