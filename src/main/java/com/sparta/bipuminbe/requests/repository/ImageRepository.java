package com.sparta.bipuminbe.requests.repository;

import com.sparta.bipuminbe.common.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}
