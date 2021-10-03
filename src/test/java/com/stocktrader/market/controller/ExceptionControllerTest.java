package com.stocktrader.market.controller;

import com.stocktrader.market.model.dto.ErrorResponse;
import com.stocktrader.market.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerTest {
    private static final String MOCK_MESSAGE = "MESSAGE";
    @InjectMocks
    private ExceptionController exceptionController;
    @Mock
    private EmailService mockEmailService;
    @Mock
    private Exception mockException;
    @Mock
    private HttpServletRequest mockRequest;


    @Test
    void serverError() throws Exception {
        when(mockException.getMessage()).thenReturn(MOCK_MESSAGE);

        HttpEntity<ErrorResponse> response = exceptionController.serverError(mockRequest, mockException);

        verify(mockEmailService).sendStackTrace(response.getBody(), mockException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ((ResponseEntity<ErrorResponse>) response).getStatusCode());
    }
}