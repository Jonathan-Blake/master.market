package com.stocktrader.market.service;

import com.stocktrader.market.TestDataUtil;
import com.stocktrader.market.controller.StockController;
import com.stocktrader.market.controller.TraderController;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.dto.TraderPortfolio;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    void sendReport_SingleStockResponse() throws Exception {
        when(mockTrader.getId()).thenReturn(MOCK_ID);

        StockResponse sr = new StockResponse();
        sr.setQuantity(BigInteger.TEN);
        sr.setCloseValue(BigInteger.TWO);
        sr.setOpenValue(BigInteger.ONE);
        sr.setPrice(BigInteger.TEN);
        sr.setStockCode("STOCK");
        sr.setUpdatedOn(Instant.now());
        sr.add(linkTo(methodOn(StockController.class).getStock("STOCK")).withSelfRel());

        reportService.sendReport(new PageImpl<>(List.of(sr)), ReportFormat.CSV, mockTrader);
        verify(emailService).sendReport(any(), matches(MOCK_ID));
    }


    @Test
    void sendReport_StockResponseList() throws Exception {
        when(mockTrader.getId()).thenReturn(MOCK_ID);

        StockResponse sr = new StockResponse();
        sr.setQuantity(BigInteger.TEN);
        sr.setCloseValue(BigInteger.TWO);
        sr.setOpenValue(BigInteger.ONE);
        sr.setPrice(BigInteger.TEN);
        sr.setStockCode("STOCK");
        sr.setUpdatedOn(Instant.now());
        sr.add(linkTo(methodOn(StockController.class).getStock("STOCK")).withSelfRel());

        reportService.sendReport(List.of(sr), ReportFormat.CSV, mockTrader);
        verify(emailService).sendReport(any(), matches(MOCK_ID));
    }

    @Test
    void sendReport_TraderPortfolioEmpty() throws Exception {
        when(mockTrader.getId()).thenReturn(MOCK_ID);

        TraderDao traderDao = new TraderDao("TRADER_ID", BigInteger.valueOf(10000));
        TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(traderDao);
        portfolio.add(linkTo(methodOn(TraderController.class).getPortfolio()).withSelfRel());

        reportService.sendReport(portfolio, ReportFormat.CSV, mockTrader);
        verify(emailService).sendReport(any(), matches(MOCK_ID));
    }

    @Test
    void sendReport_TraderPortfolioTrades() throws Exception {
        when(mockTrader.getId()).thenReturn(MOCK_ID);

        TraderDao traderDao = new TraderDao("TRADER_ID", BigInteger.valueOf(10000));
        TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(traderDao);
        portfolio.add(linkTo(methodOn(TraderController.class).getPortfolio()).withSelfRel());
        TestDataUtil testDataUtil = new TestDataUtil();

        testDataUtil.generateTransactions(1).forEach(traderDao::addNewTrade);

        reportService.sendReport(portfolio, ReportFormat.CSV, mockTrader);
        verify(emailService).sendReport(any(), matches(MOCK_ID));
    }
}