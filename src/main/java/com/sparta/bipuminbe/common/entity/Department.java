package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.department.dto.DepartmentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE department SET deleted = true WHERE id = ?")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String deptName;

    private Boolean deleted;


    @Builder
    public Department(String deptName){
        this.deptName = deptName;
        this.deleted = false;
    }


    public void update(String deptName) {
        this.deptName = deptName;
    }
}
