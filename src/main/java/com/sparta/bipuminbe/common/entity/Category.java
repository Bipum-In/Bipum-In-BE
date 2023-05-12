package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.LargeCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@SQLDelete(sql = "UPDATE category SET deleted = true WHERE id = ?")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    private LargeCategory largeCategory;

    private Boolean deleted;


    @Builder
    public Category(String categoryName, LargeCategory largeCategory, Boolean deleted) {
        this.categoryName = categoryName;
        this.largeCategory = largeCategory;
        this.deleted = deleted;
    }


    public void update(String categoryName, LargeCategory largeCategory) {
        this.categoryName = categoryName;
        this.largeCategory = largeCategory;
    }


    // 재등록 되었을 때 이름 변경.
    public void reEnroll() {
        this.categoryName = this.categoryName + "(삭제됨#" + this.id + ")";
    }
}
