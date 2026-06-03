package com.example.shop_app.controller;

import com.example.shop_app.dto.DeleteResponse;
import com.example.shop_app.dto.ProductCreateRequest;
import com.example.shop_app.dto.ProductResponse;
import com.example.shop_app.dto.ProductUpdateRequest;
import com.example.shop_app.exception.CustomException;
import com.example.shop_app.exception.ErrorCode;
import com.example.shop_app.service.MemberService;
import com.example.shop_app.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/products")
public class ProductController {

    private static final String SESSION_KEY = "LOGIN_MEMBER_ID";

    private final ProductService productService;
    private final MemberService memberService;

    @Operation(summary = "상품 생성")
    @PostMapping
    public ProductResponse createProduct(
            @RequestBody ProductCreateRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest
    ) {
        Long memberId = resolveLoginMemberId(authHeader, httpRequest);
        return productService.createProduct(memberId, request);
    }

    @Operation(summary = "상품 전체 조회")
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "상품 단건 조회")
    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return productService.getProduct(productId);
    }

    @Operation(summary = "상품 수정")
    @PatchMapping("/{productId}")
    public ProductResponse updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest
    ) {
        Long memberId = resolveLoginMemberId(authHeader, httpRequest);
        return productService.updateProduct(memberId, productId, request);
    }

    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{productId}")
    public DeleteResponse deleteProduct(
            @PathVariable Long productId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest
    ) {
        Long memberId = resolveLoginMemberId(authHeader, httpRequest);
        productService.deleteProduct(memberId, productId);
        return new DeleteResponse("상품이 삭제되었습니다.");
    }

    private Long resolveLoginMemberId(String authHeader, HttpServletRequest httpRequest) {
        // JWT 방식 우선
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return memberService.extractMemberIdFromToken(authHeader);
        }

        // 세션 방식
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
