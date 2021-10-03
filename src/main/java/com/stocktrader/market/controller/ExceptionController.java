package com.stocktrader.market.controller;

import com.stocktrader.market.model.dto.ErrorResponse;
import com.stocktrader.market.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ExceptionController {

    @Autowired
    private EmailService emailSender;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpEntity<ErrorResponse> serverError(HttpServletRequest req, Exception e) {
        ErrorResponse ret = new ErrorResponse();
        ret.setTimestamp(Instant.now());
        ret.setError("Exception");
        ret.setMessage(e.getMessage());
        emailSender.sendStackTrace(ret, e);
        return new ResponseEntity<>(ret, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
