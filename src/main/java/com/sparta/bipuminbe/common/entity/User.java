package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Entity(name = "users")
@NoArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE users SET deleted = true, phone = null, image = null, " +
        "username = CONCAT('탈퇴한 유저#', id), google_id = uuid(), access_token = uuid() WHERE id = ?")
public class User extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleId;

    // 아이디
    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    // 이름
    private String empName;

    private String phone;

    private String image;

    // 구글 액세스 토큰
    @Column(nullable = false)
    private String accessToken;

    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    // SMS 알림 ON/OFF
    @Column(nullable = false)
    private Boolean alarm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false)
    private Boolean deleted;


    @Builder
    public User(String googleId, String username, String password, String empName, String phone,
                String image, String accessToken, UserRoleEnum role, Boolean alarm, Department department, Boolean deleted) {
        this.googleId = googleId;
        this.username = username;
        this.password = password;
        this.empName = empName;
        this.phone = phone;
        this.image = image;
        this.accessToken = accessToken;
        this.role = role;
        this.alarm = alarm;
        this.department = department;
        this.deleted = deleted;
    }


    public void update(String empName, Department department, String phone, Boolean alarm, String image, String password) {
        this.empName = empName;
        this.department = department;
        this.phone = phone;
        this.alarm = alarm;
        this.image = image;
        this.password = password;
    }


    public void changePassword(String password) {
        this.password = password;
    }


    // 구글 액세스 토큰. 재발급.
    public void refreshGoogleToken(String accessToken) {
        this.accessToken = accessToken;
    }


    // 권한 부여
    public void changeRole(UserRoleEnum role) {
        // 부여하는 권한을 이미 가지고 있는 경우라면, 권한을 취소하는 경우이다.
        this.role = this.role == role ? UserRoleEnum.USER : role;
    }
}
