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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(mockValidator.validate(any(TraderPortfolio.class), any(Class.class))).thenReturn(Set.of());
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
    void handleTransaction_rejectsInvalidTransaction_insufficentTradeableQuantity_andDoesNotSaveToBD() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.of(mockStockHistory));
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));
        when(mockStockService.getCurrentlyTradeableStockQuantity(any(Stock.class))).thenReturn(BigInteger.valueOf(50L));
        when(mockValidator.validate(any(TraderPortfolio.class), any(Class.class))).thenReturn(Set.of());
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.TEN);
//        when(mockStock.getTotalQuantity()).thenReturn(BigInteger.valueOf(50l));

        TransactionType transactionType = TransactionType.BUY;

        transactionService.handleTransaction(transactionRequest, traderDao);

        verify(mockStockRepo).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo).findFirst1ByStockOrderByTime(mockStock);
        verify(mockStockService).getCurrentlyTradeableStockQuantity(mockStock);
//        verify(mockValidator).validate(samePropertyValuesAs(trader.buildPortfolio()), TraderPortfolio.class);
        assertEquals(1, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(transactionType, trade)).count());
        verify(mockTraderRepo, never()).save(traderDao);
    }


    @Test
    void handleTransaction_rejectsInvalidTransaction_insufficentFunds_andDoesNotSaveToBD() {
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.of(mockStockHistory));
        when(mockStockRepo.findById(anyString())).thenReturn(Optional.of(mockStock));
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.TEN);

        when(mockValidator.validate(any(TraderPortfolio.class), any(Class.class))).thenReturn(Set.of(mockConstraintViolation));

        TransactionType transactionType = TransactionType.BUY;

        transactionService.handleTransaction(transactionRequest, traderDao);

        verify(mockStockRepo).findById(STOCK_SYMBOL);
        verify(mockStockHistoryRepo).findFirst1ByStockOrderByTime(mockStock);
//        verify(mockValidator).validate(samePropertyValuesAs(trader.buildPortfolio()), TraderPortfolio.class);
        assertEquals(1, traderDao.getTrades().stream().filter(trade -> VerifyTransactionParams(transactionType, trade)).count());
        verify(mockTraderRepo, never()).save(traderDao);
    }


    private boolean VerifyTransactionParams(TransactionType transactionType, Transaction trade) {
        return trade.getStockTraded() == mockStockHistory
                && trade.getQuantity().equals(QUANTITY)
                && trade.getTransactionType() == transactionType;
    }
}