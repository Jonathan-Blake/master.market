package com.stocktrader.market.controller;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.TransactionRequest;
import com.stocktrader.market.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;
    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionRequest mockTransactionRequest;
    @Mock
    private TraderDao traderDao;

    @Test
    void transaction_Succeeds() {
        when(transactionService.handleTransaction(any(TransactionRequest.class), any(TraderDao.class))).thenReturn(true);

        HttpEntity<String> result = transactionController.transaction(mockTransactionRequest);

        assertEquals(HttpStatus.OK, ((ResponseEntity<String>) result).getStatusCode());
        verify(transactionService).handleTransaction(mockTransactionRequest, traderDao);
    }

    @Test
    void transaction_Fails() {
        when(transactionService.handleTransaction(any(TransactionRequest.class), any(TraderDao.class))).thenReturn(false);

        HttpEntity<String> result = transactionController.transaction(mockTransactionRequest);

        assertEquals(HttpStatus.BAD_REQUEST, ((ResponseEntity<String>) result).getStatusCode());
        verify(transactionService).handleTransaction(mockTransactionRequest, traderDao);
    }
}