package com.example.shop_app.controller;

import com.example.shop_app.dto.DeleteResponse;
import com.example.shop_app.dto.ProductCreateRequest;
import com.example.shop_app.dto.ProductResponse;
import com.example.shop_app.dto.ProductUpdateRequest;
import com.example.shop_app.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 생성")
    @PostMapping
    public ProductResponse createProduct(@RequestBody ProductCreateRequest request) {
        return productService.createProduct(request);
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
            @RequestBody ProductUpdateRequest request
    ) {
        return productService.updateProduct(productId, request);
    }

    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{productId}")
    public DeleteResponse deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new DeleteResponse("상품이 삭제되었습니다.");
    }
}
