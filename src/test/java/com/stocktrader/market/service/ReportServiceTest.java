package com.stocktrader.market.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.ref.ReportFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    ReportService reportService = new ReportService();
    @Mock
    private TraderDao mockTrader;

    @Test
    void sendReport_Empty() throws JsonProcessingException {
        reportService.sendReport(Page.empty(), ReportFormat.CSV, mockTrader);
    }

    @Test
    void sendReport() throws JsonProcessingException {
        StockResponse sr = new StockResponse();
        sr.setQuantity(BigInteger.TEN);
        sr.setCloseValue(BigInteger.TWO);
        sr.setOpenValue(BigInteger.ONE);
        sr.setPrice(BigInteger.TEN);
        sr.setStockCode("STOCK");
        sr.setUpdatedOn(Instant.now());
        reportService.sendReport(new PageImpl<>(List.of(sr)), ReportFormat.CSV, mockTrader);
    }
}