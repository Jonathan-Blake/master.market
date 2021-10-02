package com.stocktrader.market.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.TraderPortfolio;
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
import java.math.BigInteger;
import java.util.Collections;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraderControllerTest {
    @InjectMocks
    TraderController traderController;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private TraderDao mockTrader;

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
}