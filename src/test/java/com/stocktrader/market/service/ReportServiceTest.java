package com.stocktrader.market.service;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.ref.ReportFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    private static final String MOCK_ID = "TRADER";
    @InjectMocks
    ReportService reportService;
    @Mock
    private TraderDao mockTrader;
    @Mock
    private EmailService emailService;

    @Test
    void sendReport_Empty() throws Exception {
        when(mockTrader.getId()).thenReturn(MOCK_ID);
        reportService.sendReport(Page.empty(), ReportFormat.CSV, mockTrader);
        verify(emailService).sendReport(any(), matches(MOCK_ID));
    }

    @Test
    void sendReport() throws Exception {
        when(mockTrader.getId()).thenReturn(MOCK_ID);

        StockResponse sr = new StockResponse();
        sr.setQuantity(BigInteger.TEN);
        sr.setCloseValue(BigInteger.TWO);
        sr.setOpenValue(BigInteger.ONE);
        sr.setPrice(BigInteger.TEN);
        sr.setStockCode("STOCK");
        sr.setUpdatedOn(Instant.now());
        reportService.sendReport(new PageImpl<>(List.of(sr)), ReportFormat.CSV, mockTrader);
        verify(emailService).sendReport(any(), matches(MOCK_ID));
    }
}