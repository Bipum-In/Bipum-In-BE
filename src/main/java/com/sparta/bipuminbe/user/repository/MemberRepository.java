package com.sparta.bipuminbe.user.repository;

import com.sparta.bipuminbe.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String email);
}
