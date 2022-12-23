package com.example.shoppingmall.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

}
