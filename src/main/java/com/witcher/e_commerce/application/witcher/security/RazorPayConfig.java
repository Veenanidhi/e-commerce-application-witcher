package com.witcher.e_commerce.application.witcher.security;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

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


    public JSONObject createRazorpayOrder(RazorpayClient client, double amount) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient("rzp_test_GCgY3o3OlXx0HN", "q4GkXW0SRLZ7MBzK1sRO2Pcc");
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1); // auto-capture

        return client.orders.create(orderRequest).toJson();
    }

    /**
     * Helper method to verify Razorpay payment signature
     */
    public boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
        try {
            // The data that was used to generate the signature
            String data = orderId + "|" + paymentId;

            // Generate the expected signature using HMAC-SHA256
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(data.getBytes());
            String calculatedSignature = Base64.getEncoder().encodeToString(hash);

            // Compare signatures
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
