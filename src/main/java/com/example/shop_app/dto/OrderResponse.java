package com.example.shop_app.dto;

import com.example.shop_app.domain.Order;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {

    private Long orderId;
    private Long memberId;
    private String status;
    private Integer totalPrice;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .memberId(order.getMember().getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .items(order.getOrderItems().stream().map(OrderItemResponse::from).toList())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
