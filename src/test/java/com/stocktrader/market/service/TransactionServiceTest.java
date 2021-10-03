package com.stocktrader.market.service;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.dto.TransactionRequest;
import com.stocktrader.market.model.ref.TransactionType;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import com.stocktrader.market.repo.TraderRepo;
import com.stocktrader.market.repo.TransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    private final static BigInteger QUANTITY = BigInteger.valueOf(100L);
    private static final String STOCK_SYMBOL = "STOCK";
    private TransactionRequest transactionRequest;
    private TraderDao traderDao;
    @Mock
    TraderRepo mockTraderRepo;
    @Mock
    StockHistoryRepo mockStockHistoryRepo;
    @Mock
    TransactionRepo mockTransactionRepo;
    @InjectMocks
    TransactionService transactionService;
    @Mock
    private StockService mockStockService;
    @Mock
    private StockRepo mockStockRepo;
    @Mock
    private StockHistory mockStockHistory;
    @Mock
    private Stock mockStock;
    @Mock
    private Validator mockValidator;
    @Mock
    private ConstraintViolation<TraderPortfolio> mockConstraintViolation;

    @BeforeEach
    void setup() {
        transactionRequest = new TransactionRequest(STOCK_SYMBOL, TransactionType.BUY, QUANTITY);
        traderDao = new TraderDao("Test", BigInteger.valueOf(1000L));
    }

    @Test
    void handleTransaction_approvesValidTransaction_andSavesToBD() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.of(mockStockHistory));
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));
        when(mockStockService.getCurrentlyTradeableStockQuantity(any(Stock.class))).thenReturn(BigInteger.valueOf(100L));
        when(mockValidator.validate(any(TraderPortfolio.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockValidator.validate(any(Transaction.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.ONE);
        when(mockStock.getTotalQuantity()).thenReturn(BigInteger.valueOf(100L));
        when(mockStock.getSymbol()).thenReturn(STOCK_SYMBOL);

        TransactionType transactionType = TransactionType.BUY;

        transactionService.handleTransaction(transactionRequest, traderDao);

        verify(mockStockRepo).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo).findFirst1ByStockOrderByTime(mockStock);
        verify(mockValidator).validate(any(TraderPortfolio.class));
        assertEquals(1, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(transactionType, trade)).count());
        verify(mockTraderRepo).save(traderDao);
    }

    @Test
    void handleTransaction_BuyThenSell() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.of(mockStockHistory));
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));
        when(mockStockService.getCurrentlyTradeableStockQuantity(any(Stock.class))).thenReturn(BigInteger.valueOf(100L));
        when(mockValidator.validate(any(TraderPortfolio.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockValidator.validate(any(Transaction.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.ONE);
        when(mockStock.getTotalQuantity()).thenReturn(BigInteger.valueOf(100L));
        when(mockStock.getSymbol()).thenReturn(STOCK_SYMBOL);

        assertTrue(transactionService.handleTransaction(transactionRequest, traderDao));
        transactionRequest = new TransactionRequest(STOCK_SYMBOL, TransactionType.SELL, QUANTITY);
        assertTrue(transactionService.handleTransaction(transactionRequest, traderDao));

        verify(mockStockRepo, times(2)).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo, times(2)).findFirst1ByStockOrderByTime(mockStock);
        verify(mockValidator, times(2)).validate(any(TraderPortfolio.class));
        assertEquals(1, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(TransactionType.BUY, trade)).count());
        assertEquals(1, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(TransactionType.SELL, trade)).count());
        verify(mockTraderRepo, times(2)).save(traderDao);
    }

    @Test
    void handleTransaction_BuyThenSellTwo_FailsOnce() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.of(mockStockHistory));
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));
        when(mockStockService.getCurrentlyTradeableStockQuantity(any(Stock.class))).thenReturn(BigInteger.valueOf(100L));
        when(mockValidator.validate(any(TraderPortfolio.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockValidator.validate(any(Transaction.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.ONE);
        when(mockStock.getTotalQuantity()).thenReturn(BigInteger.valueOf(100L));
        when(mockStock.getSymbol()).thenReturn(STOCK_SYMBOL);

        assertTrue(transactionService.handleTransaction(transactionRequest, traderDao));
        transactionRequest = new TransactionRequest(STOCK_SYMBOL, TransactionType.SELL, QUANTITY);
        assertTrue(transactionService.handleTransaction(transactionRequest, traderDao));
        assertFalse(transactionService.handleTransaction(transactionRequest, traderDao));

        verify(mockStockRepo, times(3)).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo, times(3)).findFirst1ByStockOrderByTime(mockStock);
        verify(mockValidator, times(3)).validate(any(TraderPortfolio.class));
        assertEquals(1, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(TransactionType.BUY, trade)).count());
        assertEquals(2, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(TransactionType.SELL, trade)).count());
        verify(mockTraderRepo, times(2)).save(traderDao);
    }

    @Test
    void handleTransaction_rejectsInvalidTransaction_insufficentTradeableQuantity_andDoesNotSaveToBD() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.of(mockStockHistory));
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));
        when(mockStock.getSymbol()).thenReturn(STOCK_SYMBOL);
        when(mockStockService.getCurrentlyTradeableStockQuantity(any(Stock.class))).thenReturn(BigInteger.valueOf(50L));
        when(mockValidator.validate(any(TraderPortfolio.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockValidator.validate(any(Transaction.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.TEN);

        TransactionType transactionType = TransactionType.BUY;

        transactionService.handleTransaction(transactionRequest, traderDao);

        verify(mockStockRepo).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo).findFirst1ByStockOrderByTime(mockStock);
        verify(mockStockService).getCurrentlyTradeableStockQuantity(mockStock);
        assertEquals(1, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(transactionType, trade)).count());
        verify(mockTraderRepo, never()).save(traderDao);
    }

    @Test
    void handleTransaction_rejectsInvalidTransaction_insufficentFunds_andDoesNotSaveToBD() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.of(mockStockHistory));
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));
        when(mockStock.getSymbol()).thenReturn(STOCK_SYMBOL);
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.TEN);

        when(mockValidator.validate(any(Transaction.class), any(Class.class))).thenReturn(Collections.emptySet());
        when(mockValidator.validate(any(TraderPortfolio.class), any(Class.class))).thenReturn(Set.of(mockConstraintViolation));

        TransactionType transactionType = TransactionType.BUY;

        transactionService.handleTransaction(transactionRequest, traderDao);

        verify(mockStockRepo).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo).findFirst1ByStockOrderByTime(mockStock);
//        verify(mockValidator).validate(samePropertyValuesAs(trader.buildPortfolio()), TraderPortfolio.class);
        assertEquals(1, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(transactionType, trade)).count());
        verify(mockTraderRepo, never()).save(traderDao);
    }

    @Test
    void handleTransaction_rejectsInvalidTransaction_NonExistentStock_andDoesNotSaveToBD() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.empty());
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));

        transactionService.handleTransaction(transactionRequest, traderDao);

        verify(mockStockRepo).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo).findFirst1ByStockOrderByTime(mockStock);
        assertNull(traderDao.getTrades());
        verify(mockTraderRepo, never()).save(traderDao);
    }

    @Test
    void handleTransaction_rejectsInvalidTransaction_NotValid_andDoesNotSaveToBD() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.empty());
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));

        transactionService.handleTransaction(transactionRequest, traderDao);

        verify(mockStockRepo).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo).findFirst1ByStockOrderByTime(mockStock);
        assertNull(traderDao.getTrades());
        verify(mockTraderRepo, never()).save(traderDao);
    }


    private boolean VerifyTransactionParams(TransactionType transactionType, Transaction trade) {
        return trade.getStockTraded() == mockStockHistory
                && trade.getQuantity().equals(QUANTITY)
                && trade.getTransactionType() == transactionType;
    }
}