package com.example.shop_app.controller;

import com.example.shop_app.domain.Member;
import com.example.shop_app.dto.MemberResponse;
import com.example.shop_app.exception.CustomException;
import com.example.shop_app.exception.ErrorCode;
import com.example.shop_app.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private static final String SESSION_KEY = "LOGIN_MEMBER_ID";

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회 (세션 또는 JWT)")
    @GetMapping("/me")
    public MemberResponse getMe(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        // JWT 방식 우선
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            Long memberId = memberService.extractMemberIdFromToken(authHeader);
            Member member = memberService.findMemberById(memberId);
            return MemberResponse.from(member);
        }

        // 세션 방식
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long memberId = (Long) session.getAttribute(SESSION_KEY);
            if (memberId != null) {
                Member member = memberService.findMemberById(memberId);
                return MemberResponse.from(member);
            }
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
