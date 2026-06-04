package com.example.shop_app.config;

import com.example.shop_app.domain.Member;
import com.example.shop_app.domain.Product;
import com.example.shop_app.repository.MemberRepository;
import com.example.shop_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        if (memberRepository.count() == 0) {
            Member member = memberRepository.save(Member.create(
                    "lion@example.com",
                    passwordEncoder.encode("1234"),
                    "멋쟁이"
            ));

            productRepository.save(Product.create(member, "멋사 후드티", "멋쟁이사자처럼 로고가 들어간 후드티입니다.", 39000, 10));
            productRepository.save(Product.create(member, "멋사 맨투맨", "멋사 로고가 새겨진 맨투맨입니다.", 35000, 1));
        }
    }
}
