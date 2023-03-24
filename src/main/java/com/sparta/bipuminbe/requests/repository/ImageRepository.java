package com.sparta.bipuminbe.requests.repository;

import com.sparta.bipuminbe.common.entity.Image;
import com.sparta.bipuminbe.common.entity.Requests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image,Long> {
    Optional<List<Image>> findImagesByRequests(Requests requests);
}
