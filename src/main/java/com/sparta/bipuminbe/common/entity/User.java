package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity(name = "users")
@NoArgsConstructor
@Getter
public class User extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private String image;

    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private Boolean alarm;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

}
