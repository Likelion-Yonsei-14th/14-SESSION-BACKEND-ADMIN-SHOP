package com.example.shop_app.controller;

import com.example.shop_app.dto.OrderCreateRequest;
import com.example.shop_app.dto.OrderResponse;
import com.example.shop_app.exception.CustomException;
import com.example.shop_app.exception.ErrorCode;
import com.example.shop_app.service.MemberService;
import com.example.shop_app.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private static final String SESSION_KEY = "LOGIN_MEMBER_ID";

    private final OrderService orderService;
    private final MemberService memberService;

    @Operation(summary = "주문 생성")
    @PostMapping
    public OrderResponse createOrder(
            @RequestBody OrderCreateRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest
    ) {
        Long memberId = resolveLoginMemberId(authHeader, httpRequest);
        return orderService.createOrder(memberId, request);
    }

    @Operation(summary = "주문 단건 조회")
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @PathVariable Long orderId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest
    ) {
        resolveLoginMemberId(authHeader, httpRequest);
        return orderService.getOrder(orderId);
    }

    @Operation(summary = "내 주문 목록 조회")
    @GetMapping("/me")
    public List<OrderResponse> getMyOrders(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest
    ) {
        Long memberId = resolveLoginMemberId(authHeader, httpRequest);
        return orderService.getMyOrders(memberId);
    }

    @Operation(summary = "주문 취소")
    @PatchMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest
    ) {
        Long memberId = resolveLoginMemberId(authHeader, httpRequest);
        return orderService.cancelOrder(memberId, orderId);
    }

    private Long resolveLoginMemberId(String authHeader, HttpServletRequest httpRequest) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return memberService.extractMemberIdFromToken(authHeader);
        }
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            Long memberId = (Long) session.getAttribute(SESSION_KEY);
            if (memberId != null) {
                return memberId;
            }
        }
        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
