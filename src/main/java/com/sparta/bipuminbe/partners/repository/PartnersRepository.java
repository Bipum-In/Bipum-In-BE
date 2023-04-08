package com.sparta.bipuminbe.partners.repository;

import com.sparta.bipuminbe.common.entity.Partners;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import java.util.List;
import java.util.Optional;

public interface PartnersRepository extends JpaRepository<Partners, Long> {
    Optional<Partners> findByPartnersIdAndDeletedFalse(Long partnersId);

    List<Partners> findByDeletedFalse();

    Boolean existsByPartnersName(String partnersName);

    Optional<Partners> findByPartnersNameAndDeletedFalse(String partners);

    Page<Partners> findAllByDeletedFalse(Pageable pageable);
}
