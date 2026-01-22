package com.user_service.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
	 private final JavaMailSender mailSender;
	    private final SpringTemplateEngine templateEngine;

	    @Value("${app.email.from}")
	    private String fromEmail;

	    @Value("${app.email.from-name}")
	    private String fromName;

	    @Value("${app.email.verification-url}")
	    private String verificationBaseUrl;

	    /**
	     * Send email verification link
	     * Uses dedicated emailExecutor from AsyncConfig
	     */
	    @Async("emailExecutor")  // 👈 Specify executor name
	    public void sendVerificationEmail(String to, String fullName, String token) {
	        try {
	            log.info("Sending verification email to: {}", to);
	            
	            String verificationUrl = verificationBaseUrl + "?token=" + token;
	            
	            Map<String, Object> variables = new HashMap<>();
	            variables.put("name", fullName);
	            variables.put("verificationUrl", verificationUrl);
	            
	            String subject = "Verify Your Email - BugTracker";
	            String htmlContent = buildEmailContent("email-verification", variables);
	            
	            sendHtmlEmail(to, subject, htmlContent);
	            
	            log.info("Verification email sent successfully to: {}", to);
	        } catch (Exception e) {
	            log.error("Failed to send verification email to: {}", to, e);
	            // In production, you might want to:
	            // - Retry with exponential backoff
	            // - Save to failed email queue
	            // - Send alert to ops team
	        }
	    }

	    /**
	     * Send OTP email
	     */
	    @Async("emailExecutor")
	    public void sendOtpEmail(String to, String fullName, String otp, long expirationMinutes) {
	        try {
	            log.info("Sending OTP email to: {}", to);
	            
	            Map<String, Object> variables = new HashMap<>();
	            variables.put("name", fullName);
	            variables.put("otp", otp);
	            variables.put("expirationMinutes", expirationMinutes);
	            
	            String subject = "Your Login OTP - BugTracker";
	            String htmlContent = buildEmailContent("otp-email", variables);
	            
	            sendHtmlEmail(to, subject, htmlContent);
	            
	            log.info("OTP email sent successfully to: {}", to);
	        } catch (Exception e) {
	            log.error("Failed to send OTP email to: {}", to, e);
	        }
	    }

	    /**
	     * Send welcome email
	     */
	    @Async("emailExecutor")
	    public void sendWelcomeEmail(String to, String fullName) {
	        try {
	            log.info("Sending welcome email to: {}", to);
	            
	            Map<String, Object> variables = new HashMap<>();
	            variables.put("name", fullName);
	            
	            String subject = "Welcome to BugTracker!";
	            String htmlContent = buildEmailContent("welcome-email", variables);
	            
	            sendHtmlEmail(to, subject, htmlContent);
	            
	            log.info("Welcome email sent successfully to: {}", to);
	        } catch (Exception e) {
	            log.error("Failed to send welcome email to: {}", to, e);
	        }
	    }

	    /**
	     * Send password reset email
	     */
	    @Async("emailExecutor")
	    public void sendPasswordResetEmail(String to, String fullName, String resetToken) {
	        try {
	            log.info("Sending password reset email to: {}", to);
	            
	            String resetUrl = verificationBaseUrl.replace("/verify-email", "/reset-password") 
	                + "?token=" + resetToken;
	            
	            Map<String, Object> variables = new HashMap<>();
	            variables.put("name", fullName);
	            variables.put("resetUrl", resetUrl);
	            
	            String subject = "Reset Your Password - BugTracker";
	            String htmlContent = buildEmailContent("password-reset", variables);
	            
	            sendHtmlEmail(to, subject, htmlContent);
	            
	            log.info("Password reset email sent successfully to: {}", to);
	        } catch (Exception e) {
	            log.error("Failed to send password reset email to: {}", to, e);
	        }
	    }

	    /**
	     * Internal method to send HTML email
	     * Not async - called by async methods above
	     */
	    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	        
//	        try {
//	            helper.setFrom(fromEmail, fromName);
//	        } catch (UnsupportedEncodingException e) {
//	            throw new RuntimeException("Invalid from name encoding", e);
//	        }
	        helper.setFrom(String.format("%s <%s>", fromName, fromEmail));
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(htmlContent, true);
	        
	        mailSender.send(message);
	    }

	    /**
	     * Build email content from Thymeleaf template
	     */
	    private String buildEmailContent(String templateName, Map<String, Object> variables) {
	        Context context = new Context();
	        context.setVariables(variables);
	        return templateEngine.process(templateName, context);
	    }
}
