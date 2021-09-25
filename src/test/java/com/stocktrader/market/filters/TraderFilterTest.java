package com.stocktrader.market.filters;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.repo.TraderRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraderFilterTest {

    private static final String USER_NAME = "USER";
    private static final String ANONYMOUS_USER = "anonymousUser";
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
    @Mock
    private SecurityContext mockSecurityContext;
    @Mock
    private Authentication mockAuthentication;

    @Test
    void testTraderFilterAddsTraderToRequestAndContinues_WhenValidAuth() throws IOException, ServletException {
        when(traderRepo.findById(anyString())).thenReturn(Optional.of(traderDao));
        SecurityContextHolder.setContext(mockSecurityContext);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn(USER_NAME);

        traderFilter.doFilter(request, response, chain);

        verify(request).setAttribute(TraderFilter.TRADER_SESSION_ATTRIBUTE, traderDao);
        verify(chain).doFilter(request, response);
    }

    @Test
    void testTraderFilterDoesNotSetTraderWhenNoAuthentication() throws IOException, ServletException {
        SecurityContextHolder.setContext(mockSecurityContext);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.isAuthenticated()).thenReturn(false);

        traderFilter.doFilter(request, response, chain);

        verify(request).setAttribute(TraderFilter.TRADER_SESSION_ATTRIBUTE, null);
        verify(chain).doFilter(request, response);
        verifyNoMoreInteractions(request, response);
    }

    @Test
    void testTraderFilterDoesNotSetTraderWhenNoAuthenticationAnonymousUser() throws IOException, ServletException {
        SecurityContextHolder.setContext(mockSecurityContext);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn(ANONYMOUS_USER);

        traderFilter.doFilter(request, response, chain);

        verify(request).setAttribute(TraderFilter.TRADER_SESSION_ATTRIBUTE, null);
        verify(chain).doFilter(request, response);
        verifyNoMoreInteractions(request, response);
    }

    @Test
    void testTraderFilterSetsTraderWhenNoUserButAuthenticated() throws IOException, ServletException {
        when(traderRepo.findById(anyString())).thenReturn(Optional.empty());
        when(traderRepo.save(any(TraderDao.class))).thenReturn(traderDao);

        SecurityContextHolder.setContext(mockSecurityContext);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn(USER_NAME);

        traderFilter.doFilter(request, response, chain);

        ArgumentCaptor<TraderDao> captor = ArgumentCaptor.forClass(TraderDao.class);
        verify(traderRepo).save(captor.capture());
        TraderDao createdTrader = captor.getValue();
        assertEquals(BigInteger.valueOf(1000000L), createdTrader.getFunds());
        assertEquals(USER_NAME, createdTrader.getId());
        assertNull(createdTrader.getTrades());

        verify(request).setAttribute(TraderFilter.TRADER_SESSION_ATTRIBUTE, traderDao);
        verify(chain).doFilter(request, response);
        verifyNoMoreInteractions(request, response);
    }
}