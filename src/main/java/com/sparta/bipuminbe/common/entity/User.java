package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.dto.KakaoUserInfoDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "users")
@NoArgsConstructor
@Getter
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
    private String image;

    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    private Boolean alarm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Builder
    public User(Long kakaoId, String encodedPassword,
                KakaoUserInfoDto kakaoUserInfoDto, UserRoleEnum role){

        this.kakaoId = kakaoId;
        this.password = encodedPassword;
        this.username = kakaoUserInfoDto.getUsername();
        this.image = kakaoUserInfoDto.getImage();
        this.role = role;
    }

}
