package com.example.shop_app.controller;

import com.example.shop_app.domain.Member;
import com.example.shop_app.dto.DeleteResponse;
import com.example.shop_app.dto.LoginRequest;
import com.example.shop_app.dto.LoginResponse;
import com.example.shop_app.dto.MemberResponse;
import com.example.shop_app.dto.SignupRequest;
import com.example.shop_app.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String SESSION_KEY = "LOGIN_MEMBER_ID";

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public MemberResponse signup(@RequestBody SignupRequest request) {
        return memberService.signup(request);
    }

    @Operation(summary = "세션 로그인")
    @PostMapping("/session/login")
    public MemberResponse sessionLogin(@RequestBody LoginRequest request, HttpSession session) {
        Member member = memberService.login(request);
        session.setAttribute(SESSION_KEY, member.getId());
        return MemberResponse.from(member);
    }

    @Operation(summary = "세션 로그아웃")
    @PostMapping("/session/logout")
    public DeleteResponse sessionLogout(HttpSession session) {
        session.invalidate();
        return new DeleteResponse("로그아웃되었습니다.");
    }

    @Operation(summary = "JWT 로그인")
    @PostMapping("/jwt/login")
    public LoginResponse jwtLogin(@RequestBody LoginRequest request) {
        return memberService.jwtLogin(request);
    }
}
