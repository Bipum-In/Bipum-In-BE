package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "users")
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String nickname;

    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(String username, String password, String email, String nickname, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
