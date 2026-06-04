package com.example.shop_app.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String name;

    private String description;

    private Integer price;

    private Integer stockQuantity;

    public static Product create(Member member, String name, String description, Integer price, Integer stockQuantity) {
        Product product = new Product();
        product.member = member;
        product.name = name;
        product.description = description;
        product.price = price;
        product.stockQuantity = stockQuantity;
        return product;
    }

    public void decreaseStock(int quantity) {
        this.stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void updateProduct(String name, String description, Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
