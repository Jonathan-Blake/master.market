package com.stocktrader.market.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.ref.TransactionType;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraderControllerTest {
    private static final String STOCK_CODE = "STOCK";
    @InjectMocks
    TraderController traderController;
    @Mock
    StockRepo mockStockRepo;
    @Mock
    StockHistoryRepo mockStockHistoryRepo;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private TraderDao mockTrader;
    @Mock
    Stock mockStock;
    @Mock
    private Transaction mockTransaction;
    @Mock
    private StockHistory mockStockPriceA;
    @Mock
    private StockHistory mockStockPriceB;
    @Mock
    private List<Stock> mockStockList;
    @Captor
    private ArgumentCaptor<Iterable<String>> stockSetCaptor;

    @BeforeEach
    void injectTrader() {
        when(mockRequest.getAttribute(TRADER_SESSION_ATTRIBUTE)).thenReturn(mockTrader);
        traderController.getTrader(mockRequest);
    }

    @Test
    void getPortfolio_EmptyPortfolio() throws JsonProcessingException {
        when(mockTrader.getFunds()).thenReturn(BigInteger.ONE);
        when(mockTrader.getTrades()).thenReturn(Collections.emptyList());

        HttpEntity<TraderPortfolio> result = traderController.getPortfolio();

        assertEquals(HttpStatus.OK, ((ResponseEntity<TraderPortfolio>) result).getStatusCode());

        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.writeValueAsString(TraderPortfolio.buildPortfolio(mockTrader).get()), mapper.writeValueAsString(result.getBody().get()));
    }

    @Test
    void getPortfolio_TradedPortfolio() throws JsonProcessingException {
        when(mockTrader.getFunds()).thenReturn(BigInteger.ONE);
        when(mockTrader.getTrades()).thenReturn(List.of(mockTransaction));
        when(mockTransaction.getStockTraded()).thenReturn(mockStockPriceA);
        when(mockTransaction.getQuantity()).thenReturn(BigInteger.ONE);
        when(mockTransaction.getTransactionType()).thenReturn(TransactionType.BUY);
        when(mockTransaction.getStockCode()).thenReturn(STOCK_CODE);
        when(mockStockRepo.findAllById(anyCollection())).thenReturn(List.of(mockStock));
        when(mockStockHistoryRepo.findFirst1ByStockOrderByTimeDesc(any())).thenReturn(Optional.of(mockStockPriceB));
        when(mockStockPriceB.getStock()).thenReturn(mockStock);
        when(mockStock.getSymbol()).thenReturn(STOCK_CODE);
        when(mockStockPriceB.getPrice()).thenReturn(BigInteger.TEN);
        when(mockStockPriceA.getPrice()).thenReturn(BigInteger.ONE);

        HttpEntity<TraderPortfolio> result = traderController.getPortfolio();

        assertEquals(HttpStatus.OK, ((ResponseEntity<TraderPortfolio>) result).getStatusCode());

        ObjectMapper mapper = new ObjectMapper();
        assertEquals(BigInteger.valueOf(11), result.getBody().getTotalValue());
        assertEquals(BigInteger.TEN, result.getBody().get().get(STOCK_CODE).getTotalValue());
    }
}