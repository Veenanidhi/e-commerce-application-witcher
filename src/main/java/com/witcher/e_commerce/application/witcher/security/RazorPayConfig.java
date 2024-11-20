package com.witcher.e_commerce.application.witcher.security;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RazorPayConfig {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Bean
    @Primary
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient(apiKey, apiSecret);
    }
}
