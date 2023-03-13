package com.sparta.bipuminbe.user.repository;

import com.sparta.bipuminbe.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
