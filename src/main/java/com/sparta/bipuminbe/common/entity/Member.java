package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.user.dto.GoogleUserInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Member extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    @Builder
    public Member(String encodedPassword,
                  GoogleUserInfoDto googleUserInfoDto, UserRoleEnum role, Boolean alarm) {

        this.password = encodedPassword;
        this.username = googleUserInfoDto.getEmail();
        this.image = googleUserInfoDto.getPicture();
        this.role = role;
        this.alarm = alarm;
        this.empName = googleUserInfoDto.getName();
    }

    public void update(String empName, Department department, String phone) {
        this.department = department;
        this.phone = phone;
    }

    public void switchAlarm(Boolean alarm) {
        this.alarm = !alarm;
    }
}
