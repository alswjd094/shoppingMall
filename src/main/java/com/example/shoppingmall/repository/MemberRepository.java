package com.example.shoppingmall.repository;
import com.example.shoppingmall.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity,Long> {
    Optional<MemberEntity> findByUserId(String userId);

    Optional<MemberEntity> findByMemberEmail(String memberEmail);
}
