package com.sparta.bipuminbe.requests.repository;

import com.sparta.bipuminbe.common.entity.SupplyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long> {

}
