package com.sparta.bipuminbe.partners.repository;

import com.sparta.bipuminbe.common.entity.Partners;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnersRepository extends JpaRepository<Partners, Long> {
    boolean existsByPartnersName(String partnersName);
}
