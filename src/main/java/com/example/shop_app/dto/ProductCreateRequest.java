package com.example.shop_app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    // 2주차 인증 수업에서는 sellerId를 제거하고 로그인 사용자 정보로 대체합니다.
    private Long sellerId;

    private String name;

    private String description;

    private Integer price;
}
