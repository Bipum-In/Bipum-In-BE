package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.user.dto.KakaoUserInfoDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.user.dto.LoginRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "users")
@NoArgsConstructor
@Getter
//@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
//@Where(clause = "deleted = false")  // 조회할 때 false만 찾는 것이 default 가 된다.
public class User extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String empName;
    private String phone;
    private String image;

    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    private Boolean alarm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false)
    private Boolean deleted;

    @Builder
    public User(Long kakaoId, String encodedPassword,
                KakaoUserInfoDto kakaoUserInfoDto, UserRoleEnum role, Boolean alarm){

        this.kakaoId = kakaoId;
        this.password = encodedPassword;
        this.username = kakaoUserInfoDto.getUsername();
        this.image = kakaoUserInfoDto.getImage();
        this.role = role;
        this.alarm = alarm;
    }

    public void update(String empName, Department department, String phone) {
        this.empName = empName;
        this.department = department;
        this.phone = phone;
    }

    public void switchAlarm(Boolean alarm) {
        this.alarm =! alarm;
    }
}
