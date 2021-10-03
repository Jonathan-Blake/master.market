package com.stocktrader.market.service;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockBrokerServiceTest {
    @InjectMocks
    StockBrokerService service;
    @Mock
    StockRepo stockRepo;
    @Mock
    StockHistoryRepo stockHistoryRepo;
    @Mock
    private List<Stock> mockStockList;
    @Mock
    private Stock mockStock;
    @Captor
    private ArgumentCaptor<StockHistory> stockHistoryCaptor;
    @Mock
    private StockHistory mockStockHistory;

    @Test
    void updatePrices() {
        when(stockRepo.findAll()).thenReturn(mockStockList);
        service.updatePrices();
        verify(mockStockList).forEach(any());
    }

    @Test
    void calculateNewPrice() {
        when(stockHistoryRepo.findFirst3ByStockOrderByTime(mockStock)).thenReturn(List.of(mockStockHistory));
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.ONE);

        service.calculateNewPrice(mockStock);

        verify(stockHistoryRepo).save(stockHistoryCaptor.capture());
        StockHistory savedStockHistory = stockHistoryCaptor.getValue();
        assertEquals(mockStock, savedStockHistory.getStock());
    }


    @Nested
    class CalculateTrend {
        private StockHistory highPrice;
        private StockHistory midPrice;
        private StockHistory lowPrice;

        @BeforeEach
        void setup() {
            highPrice = new StockHistory();
            highPrice.price = BigInteger.TEN;
            highPrice.stock = new Stock("AAA", BigInteger.ZERO);
            midPrice = new StockHistory();
            midPrice.price = BigInteger.TWO;
            midPrice.stock = new Stock("BBB", BigInteger.ZERO);
            lowPrice = new StockHistory();
            lowPrice.price = BigInteger.ONE;
            lowPrice.stock = new Stock("CCC", BigInteger.ZERO);
        }

        @Test
        void calculateTrend_ForEmpty() {
            assertEquals(0, service.calculateTrend(Collections.emptyList()));
        }

        @Test
        void calculateTrend_ForSingleValue() {
            for (StockHistory history : List.of(highPrice, midPrice, lowPrice)) {
                assertEquals(0, service.calculateTrend(List.of(history)));
            }
        }

        @Test
        void calculateTrend_ForDoubleValue() {
            assertEquals(-1, service.calculateTrend(List.of(highPrice, midPrice)));
            assertEquals(1, service.calculateTrend(List.of(midPrice, highPrice)));
            assertEquals(0, service.calculateTrend(List.of(lowPrice, lowPrice)));
        }

        @Test
        void calculateTrend_ForThreeValues() {
            assertEquals(-2, service.calculateTrend(List.of(highPrice, midPrice, lowPrice)));
            assertEquals(2, service.calculateTrend(List.of(lowPrice, midPrice, highPrice)));
        }
    }

}