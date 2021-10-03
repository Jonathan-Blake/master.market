package com.stocktrader.market.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@EnableAsync
public class EmailService {

    @Value("${spring.mail.username}")
    private String ADMIN_EMAIL;

    @Autowired
    private JavaMailSender emailSender;

    @Async
    public void sendStackTrace(ErrorResponse errorResponse, Exception e) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(ADMIN_EMAIL);
        message.setTo(ADMIN_EMAIL);
        message.setSubject("Confirm Password");
        StringBuilder text = new StringBuilder();
        try {
            text.append(mapper.writeValueAsString(errorResponse));
        } catch (JsonProcessingException ex) {
            text.append("Could Not JSONify Error Response");
        } finally {
            text.append(System.lineSeparator());
            text.append(Arrays.toString(e.getStackTrace()));
            message.setText(text.toString());
        }
        emailSender.send(message);
        System.out.println("Email Sent");
    }
}