package com.example.shop_app.service;

import com.example.shop_app.domain.Member;
import com.example.shop_app.dto.LoginRequest;
import com.example.shop_app.dto.LoginResponse;
import com.example.shop_app.dto.MemberResponse;
import com.example.shop_app.dto.SignupRequest;
import com.example.shop_app.exception.CustomException;
import com.example.shop_app.exception.ErrorCode;
import com.example.shop_app.repository.MemberRepository;
import com.example.shop_app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Member findMemberById(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public MemberResponse signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Member member = Member.create(request.getEmail(), encodedPassword, request.getNickname());
        return MemberResponse.from(memberRepository.save(member));
    }

    public Member login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
        return member;
    }

    public LoginResponse jwtLogin(LoginRequest request) {
        Member member = login(request);
        String token = jwtUtil.generateToken(member.getId());
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();
    }

    public Long extractMemberIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return jwtUtil.extractMemberId(authHeader.substring(7));
    }
}
