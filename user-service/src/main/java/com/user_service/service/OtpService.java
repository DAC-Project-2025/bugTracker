package com.user_service.service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OtpService {
	private static final String OTP_PREFIX = "otp:";
    private static final String OTP_ATTEMPTS_PREFIX = "otp_attempts:";
    
    private final RedisTemplate<String, String> redisTemplate;
    private final int otpLength;
    private final long otpExpirationMinutes;
    private final int maxAttempts;
    private final SecureRandom random = new SecureRandom();

    public OtpService(
            RedisTemplate<String, String> redisTemplate,
            @Value("${app.otp.length:6}") int otpLength,
            @Value("${app.otp.expiration-minutes:5}") long otpExpirationMinutes,
            @Value("${app.otp.max-attempts:3}") int maxAttempts) {
        this.redisTemplate = redisTemplate;
        this.otpLength = otpLength;
        this.otpExpirationMinutes = otpExpirationMinutes;
        this.maxAttempts = maxAttempts;
    }

    /**
     * Generate and store OTP
     */
    public String generateOtp(String email) {
        String otp = generateRandomOtp();
        String key = OTP_PREFIX + email;
        
        // Store OTP with expiration
        redisTemplate.opsForValue().set(key, otp, otpExpirationMinutes, TimeUnit.MINUTES);
        
        // Reset attempts counter
        String attemptsKey = OTP_ATTEMPTS_PREFIX + email;
        redisTemplate.delete(attemptsKey);
        
        log.info("Generated OTP for email: {}", email);
        return otp;
    }

    /**
     * Validate OTP
     */
    public boolean validateOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        String attemptsKey = OTP_ATTEMPTS_PREFIX + email;
        
        // Check attempts
        Integer attempts = getAttempts(email);
        if (attempts >= maxAttempts) {
            log.warn("Max OTP attempts exceeded for email: {}", email);
            return false;
        }
        
        // Get stored OTP
        String storedOtp = redisTemplate.opsForValue().get(key);
        
        if (storedOtp == null) {
            log.warn("OTP expired or not found for email: {}", email);
            return false;
        }
        
        // Validate OTP
        boolean isValid = storedOtp.equals(otp);
        
        if (isValid) {
            // Clear OTP and attempts on success
            redisTemplate.delete(key);
            redisTemplate.delete(attemptsKey);
            log.info("OTP validated successfully for email: {}", email);
        } else {
            // Increment attempts
            redisTemplate.opsForValue().increment(attemptsKey);
            redisTemplate.expire(attemptsKey, otpExpirationMinutes, TimeUnit.MINUTES);
            log.warn("Invalid OTP attempt for email: {}. Attempts: {}", email, attempts + 1);
        }
        
        return isValid;
    }

    /**
     * Get remaining attempts
     */
    public int getRemainingAttempts(String email) {
        int attempts = getAttempts(email);
        return Math.max(0, maxAttempts - attempts);
    }

    /**
     * Check if OTP exists
     */
    public boolean otpExists(String email) {
        String key = OTP_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Invalidate OTP
     */
    public void invalidateOtp(String email) {
        String key = OTP_PREFIX + email;
        String attemptsKey = OTP_ATTEMPTS_PREFIX + email;
        redisTemplate.delete(key);
        redisTemplate.delete(attemptsKey);
        log.info("OTP invalidated for email: {}", email);
    }

    private String generateRandomOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private int getAttempts(String email) {
        String attemptsKey = OTP_ATTEMPTS_PREFIX + email;
        String attemptsStr = redisTemplate.opsForValue().get(attemptsKey);
        return attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
    }

    public long getExpirationMinutes() {
        return otpExpirationMinutes;
    }

}
