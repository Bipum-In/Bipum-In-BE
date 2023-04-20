package com.sparta.bipuminbe.partners.repository;

import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.queryDSL.partners.PartnersRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import java.util.List;
import java.util.Optional;

public interface PartnersRepository extends JpaRepository<Partners, Long>, PartnersRepositoryCustom {
    Optional<Partners> findByPartnersIdAndDeletedFalse(Long partnersId);

    List<Partners> findByDeletedFalse();

    Optional<Partners> findByPartnersNameAndDeletedFalse(String partners);

    // 삭제된 협력업체에서 동일 이름 체크.
    Optional<Partners> findByPartnersNameAndDeletedTrue(String partnersName);

    Boolean existsByPartnersNameAndDeletedFalse(String partnersName);
}
