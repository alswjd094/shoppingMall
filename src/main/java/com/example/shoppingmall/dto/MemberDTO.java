package com.example.shoppingmall.dto;
import com.example.shoppingmall.entity.MemberEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class MemberDTO {
    private Long id;
    private String UserId;
    private String memberPassword;
    private String memberEmail;
    private String memberName;
    private String memberMobile;
    private String memberAddress;
    private LocalDateTime memberCreatedTime;
    private LocalDateTime memberUpdatedTime;

    public static MemberDTO toDTO(MemberEntity memberEntity){
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setUserId(memberEntity.getUserId());
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setMemberMobile(memberEntity.getMemberMobile());
        memberDTO.setMemberAddress(memberEntity.getMemberAddress());
        memberDTO.setMemberCreatedTime(memberEntity.getCreatedTime());
        memberDTO.setMemberUpdatedTime(memberEntity.getUpdatedTime());
        return memberDTO;
    }




}
