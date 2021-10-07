package com.stocktrader.market.controller;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraderControllerTest {
    @InjectMocks
    TraderController traderController;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private TraderDao mockTrader;
    @Mock
    PortfolioService portfolioService;
    @Mock
    private TraderPortfolio mockPortfolio;

    @BeforeEach
    void injectTrader() {
        when(mockRequest.getAttribute(TRADER_SESSION_ATTRIBUTE)).thenReturn(mockTrader);
        traderController.getTrader(mockRequest);
    }

    @Test
    void getPortfolio() {
        when(portfolioService.getTraderPortfolio(any())).thenReturn(mockPortfolio);

        HttpEntity<TraderPortfolio> result = traderController.getPortfolio();

        verify(portfolioService).getTraderPortfolio(mockTrader);
        assertEquals(HttpStatus.OK, ((ResponseEntity<TraderPortfolio>) result).getStatusCode());
        assertEquals(mockPortfolio, result.getBody());
    }
}