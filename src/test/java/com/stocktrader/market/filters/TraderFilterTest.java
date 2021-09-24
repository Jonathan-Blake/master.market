package com.stocktrader.market.filters;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.repo.TraderRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraderFilterTest {

    @InjectMocks
    TraderFilter traderFilter;
    @Mock
    TraderRepo traderRepo;
    @Mock
    TraderDao traderDao;
    @Mock
    FilterChain chain;
    @Mock
    ServletRequest request;
    @Mock
    ServletResponse response;

    @Test
    void testTraderFilterAddsTraderToRequestAndContinues_WhenValidAuth() throws IOException, ServletException {
        when(traderRepo.findById(anyString())).thenReturn(Optional.of(traderDao));

        traderFilter.doFilter(request, response, chain);

        verify(request).setAttribute(TraderFilter.TRADER_SESSION_ATTRIBUTE, traderDao);
        verify(chain).doFilter(request, response);
    }

    @Test
    void testTraderFilterDoesNotContinue() throws IOException, ServletException {
        when(traderRepo.findById(anyString())).thenReturn(Optional.empty());

        traderFilter.doFilter(request, response, chain);

        verifyNoInteractions(request, response, chain);
    }

}