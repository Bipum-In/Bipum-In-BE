package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.TimeStamped;
import com.sparta.bipuminbe.common.entity.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Notification extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    //알림의 내용
    @Column(nullable = false)
    private String content;

    //알림을 누르면 이동할 주소
    @Column(nullable = false)
    private String url;

    //알림을 읽었는지 여부 체크
    @Column(nullable = false)
    private Boolean isRead;

    //알림 종류
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private NotificationType notificationType;

    //회원 정보에 대한 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sender;

    @Builder
    public Notification(User sender, User receiver, String content, String url, Boolean isRead) {
        this.sender = sender;
        this.receiver = receiver;
//        this.notificationType = notificationType;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }
}
