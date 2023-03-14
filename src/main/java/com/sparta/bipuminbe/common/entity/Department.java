package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.department.dto.DepartmentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String deptName;

    @Builder
    public Department(DepartmentDto departmentDto){
        this.deptName = departmentDto.getDeptName();
    }

    public void update(DepartmentDto departmentDto) {
        this.deptName = departmentDto.getDeptName();
    }
}
