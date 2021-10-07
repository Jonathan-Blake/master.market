package com.stocktrader.market.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.ITrader;
import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.ref.TransactionType;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {
    private static final String STOCK_CODE = "STOCK";
    @InjectMocks
    PortfolioService portfolioService;
    @Mock
    private StockRepo mockStockRepo;
    @Mock
    private StockHistoryRepo mockStockHistoryRepo;
    @Mock
    private ITrader mockTrader;
    @Mock
    private Transaction mockTransaction;
    @Mock
    private StockHistory mockStockPriceA;
    @Mock
    private StockHistory mockStockPriceB;
    @Mock
    private Stock mockStock;


    @Test
    void getPortfolio_EmptyPortfolio() throws JsonProcessingException {
        when(mockTrader.getFunds()).thenReturn(BigInteger.ONE);
        when(mockTrader.getTrades()).thenReturn(Collections.emptyList());

        TraderPortfolio result = portfolioService.getTraderPortfolio(mockTrader);

        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.writeValueAsString(TraderPortfolio.buildPortfolio(mockTrader).get()), mapper.writeValueAsString(result.get()));
    }

    @Test
    void getPortfolio_TradedPortfolio() {
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

        TraderPortfolio result = portfolioService.getTraderPortfolio(mockTrader);
        assertEquals(BigInteger.valueOf(11), result.getTotalValue());
        assertEquals(BigInteger.TEN, result.get().get(STOCK_CODE).calculateTotalValue());
    }
}