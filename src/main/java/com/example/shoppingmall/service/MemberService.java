package com.example.shoppingmall.service;

import com.example.shoppingmall.dto.MemberDTO;
import com.example.shoppingmall.entity.MemberEntity;
import com.example.shoppingmall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    public Long save(MemberDTO memberDTO) {
        Long savedId = memberRepository.save(MemberEntity.toSaveEntity(memberDTO)).getId();
        return savedId;
    }

}
