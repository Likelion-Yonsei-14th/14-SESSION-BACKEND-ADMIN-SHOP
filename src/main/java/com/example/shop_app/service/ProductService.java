package com.example.shop_app.service;

import com.example.shop_app.domain.Member;
import com.example.shop_app.domain.Product;
import com.example.shop_app.dto.ProductCreateRequest;
import com.example.shop_app.dto.ProductResponse;
import com.example.shop_app.dto.ProductUpdateRequest;
import com.example.shop_app.exception.CustomException;
import com.example.shop_app.exception.ErrorCode;
import com.example.shop_app.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MemberService memberService;

    public ProductResponse createProduct(Long memberId, ProductCreateRequest request) {
        validateProduct(request.getName(), request.getDescription(), request.getPrice());
        Member member = memberService.findMemberById(memberId);
        Product product = Product.create(member, request.getName(), request.getDescription(), request.getPrice(), request.getStockQuantity());
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponse.from(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse updateProduct(Long memberId, Long productId, ProductUpdateRequest request) {
        validateProduct(request.getName(), request.getDescription(), request.getPrice());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        checkOwner(product, memberId);
        product.updateProduct(request.getName(), request.getDescription(), request.getPrice());
        return ProductResponse.from(productRepository.save(product));
    }

    public void deleteProduct(Long memberId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        checkOwner(product, memberId);
        productRepository.delete(product);
    }

    private void checkOwner(Product product, Long memberId) {
        if (!product.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateProduct(String name, String description, Integer price) {
        if (name == null || name.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_NAME);
        }
        if (description == null || description.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_DESCRIPTION);
        }
        if (price == null || price <= 0) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_PRICE);
        }
    }
}
