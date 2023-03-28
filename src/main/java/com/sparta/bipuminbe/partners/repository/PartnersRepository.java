package com.sparta.bipuminbe.partners.repository;

import com.sparta.bipuminbe.common.entity.Partners;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartnersRepository extends JpaRepository<Partners, Long> {
    Optional<Partners> findByPartnersIdAndDeletedFalse(Long partnersId);

    List<Partners> findByDeletedFalse();

    Boolean existsByPartnersName(String partnersName);
}
