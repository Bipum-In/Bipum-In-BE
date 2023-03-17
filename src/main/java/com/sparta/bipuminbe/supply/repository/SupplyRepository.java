package com.sparta.bipuminbe.supply.repository;

import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findAllByCategoryId(int categoryId);
    List<Supply> findByUser(User user);
}
