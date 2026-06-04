package com.example.shop_app.service;

import com.example.shop_app.domain.Member;
import com.example.shop_app.domain.Order;
import com.example.shop_app.domain.OrderItem;
import com.example.shop_app.domain.Product;
import com.example.shop_app.dto.OrderCreateRequest;
import com.example.shop_app.dto.OrderResponse;
import com.example.shop_app.exception.CustomException;
import com.example.shop_app.exception.ErrorCode;
import com.example.shop_app.repository.OrderRepository;
import com.example.shop_app.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MemberService memberService;

    @Transactional
    public OrderResponse createOrder(Long memberId, OrderCreateRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 1) {
            throw new CustomException(ErrorCode.INVALID_ORDER_QUANTITY);
        }

        Member member = memberService.findMemberById(memberId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }

        product.decreaseStock(request.getQuantity());

        Order order = Order.create(member);
        OrderItem orderItem = OrderItem.create(order, product, request.getQuantity());
        order.addOrderItem(orderItem);

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long memberId) {
        return orderRepository.findByMemberId(memberId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public OrderResponse cancelOrder(Long memberId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if ("CANCELED".equals(order.getStatus())) {
            throw new CustomException(ErrorCode.ORDER_ALREADY_CANCELED);
        }

        for (OrderItem item : order.getOrderItems()) {
            item.getProduct().increaseStock(item.getQuantity());
        }

        order.cancel();
        return OrderResponse.from(order);
    }
}
