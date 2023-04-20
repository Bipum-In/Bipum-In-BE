package com.sparta.bipuminbe.common.queryDSL.partners;

import com.sparta.bipuminbe.common.entity.Partners;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartnersRepositoryCustom {
    Page<Partners> findAllByDeletedFalse(String keyword, Pageable pageable);
}
