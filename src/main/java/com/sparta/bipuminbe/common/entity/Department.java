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
@SQLDelete(sql = "UPDATE department SET deleted = false WHERE id = ?")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String deptName;

    private Boolean deleted;

    @Builder
    public Department(DepartmentDto departmentDto){
        this.deptName = departmentDto.getDeptName();
        this.deleted = false;
    }

    public void update(String deptName) {
        this.deptName = deptName;
    }
}
