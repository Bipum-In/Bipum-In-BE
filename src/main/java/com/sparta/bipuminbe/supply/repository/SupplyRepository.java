package com.sparta.bipuminbe.supply.repository;

import com.sparta.bipuminbe.common.entity.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findAllByCategoryId(int categoryId);
}
