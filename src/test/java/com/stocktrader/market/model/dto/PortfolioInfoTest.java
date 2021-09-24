package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.ref.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioInfoTest {
    public static final BigInteger CURRENT_PRICE = BigInteger.ONE;
    public static final BigInteger ORIGINAL_PRICE = BigInteger.TWO;
    private static final String STOCK = "AAA";
    private static final BigInteger QUANTITY = BigInteger.TEN;

    @Mock
    private Transaction mockTransaction;
    @Mock
    private StockHistory mockStockHistory;

    @Test
    void linkNewTrade_ProvidesCorrectValuesForSingleTrade() {
        when(mockTransaction.getTransactionType()).thenReturn(TransactionType.BUY);
        when(mockTransaction.getStockTraded()).thenReturn(mockStockHistory);
        when(mockTransaction.getQuantity()).thenReturn(QUANTITY);

        when(mockStockHistory.getPrice()).thenReturn(CURRENT_PRICE).thenReturn(ORIGINAL_PRICE);

        PortfolioInfo portfolioInfo = new PortfolioInfo(mockStockHistory);
        PortfolioInfo ret = portfolioInfo.linkNewTrade(mockTransaction);
        ObjectMapper mapper = new ObjectMapper(
        );
        try {
            System.out.println("RET " + mapper.writeValueAsString(ret));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertEquals(CURRENT_PRICE, ret.price);
        assertEquals(ORIGINAL_PRICE, ret.averagePurchase);
        assertEquals(QUANTITY, ret.quantity);
    }

    @Test
    void linkNewTrade_ProvidesCorrectValuesForMultipleIdenticalTrades() {
        when(mockStockHistory.getPrice()).thenReturn(CURRENT_PRICE);
        when(mockTransaction.getTransactionType()).thenReturn(TransactionType.BUY);
        when(mockTransaction.getQuantity()).thenReturn(QUANTITY);
        when(mockTransaction.getStockTraded()).thenReturn(mockStockHistory);

        PortfolioInfo ret = null;
        PortfolioInfo portfolioInfo = new PortfolioInfo(mockStockHistory);
        for (int each : List.of(1, 2, 3, 4, 5)) {
            when(mockStockHistory.getPrice()).thenReturn(ORIGINAL_PRICE);
            ret = portfolioInfo.linkNewTrade(mockTransaction);
        }
        ObjectMapper mapper = new ObjectMapper(
        );
        try {
            System.out.println("RET " + mapper.writeValueAsString(ret));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertEquals(CURRENT_PRICE, ret.price);
        assertEquals(ORIGINAL_PRICE, ret.averagePurchase);
        assertEquals(QUANTITY.multiply(BigInteger.valueOf(5L)), ret.quantity);
    }

    @Test
    void linkNewTrade_ProvidesCorrectValuesForMultipleDifferentTrades() {
        when(mockTransaction.getTransactionType()).thenReturn(TransactionType.BUY);
        when(mockTransaction.getStockTraded()).thenReturn(mockStockHistory);
        when(mockTransaction.getQuantity()).thenReturn(QUANTITY);

        when(mockStockHistory.getPrice()).thenReturn(CURRENT_PRICE);

        PortfolioInfo portfolioInfo = new PortfolioInfo(mockStockHistory);
        PortfolioInfo ret = null;
        for (long price : List.of(20, 30, 40, 50, 60, 70)) {
            when(mockStockHistory.getPrice()).thenReturn(BigInteger.valueOf(price));
            ret = portfolioInfo.linkNewTrade(mockTransaction);
        }
        ObjectMapper mapper = new ObjectMapper(
        );
        try {
            System.out.println("RET " + mapper.writeValueAsString(ret));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertEquals(CURRENT_PRICE, ret.price);
        assertEquals(BigInteger.valueOf(45L), ret.averagePurchase);
        assertEquals(QUANTITY.multiply(BigInteger.valueOf(6L)), ret.quantity);
    }
}