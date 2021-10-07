package com.stocktrader.market.service.report.fixer;

import com.stocktrader.market.TestDataUtil;
import com.stocktrader.market.controller.StockController;
import com.stocktrader.market.controller.TraderController;
import com.stocktrader.market.model.csv.CSVPortfolioInfo;
import com.stocktrader.market.model.csv.CSVStockResponse;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.PortfolioInfo;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.dto.TraderPortfolio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class CSVFixerTest {

    private CSVFixer csvFixer = new CSVFixer();

    @Nested
    class StockResponseTests {
        @Test
        void convertJSON_StockResponse() {
            StockResponse sr = new StockResponse();
            sr.setQuantity(BigInteger.TEN);
            sr.setCloseValue(BigInteger.TWO);
            sr.setOpenValue(BigInteger.ONE);
            sr.setPrice(BigInteger.TEN);
            sr.setStockCode("STOCK");
            sr.setUpdatedOn(Instant.now());
            sr.add(linkTo(methodOn(StockController.class).getStock("STOCK")).withSelfRel());

            CSVStockResponse response = (CSVStockResponse) csvFixer.fix(sr);

            assertEquals(sr.getGains(), response.getGains());
            assertEquals(sr.getStockCode(), response.getStockCode());
            assertEquals(sr.getPrice(), response.getPrice());
            assertEquals(sr.getQuantity(), response.getQuantity());
            assertEquals(sr.getCloseValue(), response.getCloseValue());
            assertEquals(sr.getLastTrade(), response.getLastTrade());
            assertEquals(sr.getOpenValue(), response.getOpenValue());
            assertEquals(sr.getUpdatedOn(), response.getUpdatedOn());
        }

        @Test
        void convertJSON_StockResponsePage() {
            StockResponse sr = new StockResponse();
            sr.setQuantity(BigInteger.TEN);
            sr.setCloseValue(BigInteger.TWO);
            sr.setOpenValue(BigInteger.ONE);
            sr.setPrice(BigInteger.TEN);
            sr.setStockCode("STOCK");
            sr.setUpdatedOn(Instant.now());
            sr.add(linkTo(methodOn(StockController.class).getStock("STOCK")).withSelfRel());

            Page<StockResponse> page = new PageImpl<>(List.of(sr));

            List<CSVStockResponse> response = (List<CSVStockResponse>) csvFixer.fix(page);
            CSVStockResponse response1 = response.get(0);
            assertEquals(sr.getGains(), response1.getGains());
            assertEquals(sr.getStockCode(), response1.getStockCode());
            assertEquals(sr.getPrice(), response1.getPrice());
            assertEquals(sr.getQuantity(), response1.getQuantity());
            assertEquals(sr.getCloseValue(), response1.getCloseValue());
            assertEquals(sr.getLastTrade(), response1.getLastTrade());
            assertEquals(sr.getOpenValue(), response1.getOpenValue());
            assertEquals(sr.getUpdatedOn(), response1.getUpdatedOn());
        }

        @Test
        void convertJSON_StockResponseArray() {
            StockResponse sr = new StockResponse();
            sr.setQuantity(BigInteger.TEN);
            sr.setCloseValue(BigInteger.TWO);
            sr.setOpenValue(BigInteger.ONE);
            sr.setPrice(BigInteger.TEN);
            sr.setStockCode("STOCK");
            sr.setUpdatedOn(Instant.now());
            sr.add(linkTo(methodOn(StockController.class).getStock("STOCK")).withSelfRel());

            StockResponse[] srArray = {sr};

            List<CSVStockResponse> response = (List<CSVStockResponse>) csvFixer.fix(srArray);
            CSVStockResponse response1 = response.get(0);
            assertEquals(sr.getGains(), response1.getGains());
            assertEquals(sr.getStockCode(), response1.getStockCode());
            assertEquals(sr.getPrice(), response1.getPrice());
            assertEquals(sr.getQuantity(), response1.getQuantity());
            assertEquals(sr.getCloseValue(), response1.getCloseValue());
            assertEquals(sr.getLastTrade(), response1.getLastTrade());
            assertEquals(sr.getOpenValue(), response1.getOpenValue());
            assertEquals(sr.getUpdatedOn(), response1.getUpdatedOn());
        }

    }

    @Nested
    class TraderPortfolioTest {
        @Test
        void convertJSON_TraderPortfolio_Empty() {
            TraderDao traderDao = new TraderDao("TRADER_ID", BigInteger.valueOf(10000));
            TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(traderDao);
            portfolio.add(linkTo(methodOn(TraderController.class).getPortfolio()).withSelfRel());

            List<CSVPortfolioInfo> response = (List<CSVPortfolioInfo>) csvFixer.fix(portfolio);
            CSVPortfolioInfo response1 = response.get(0);
            PortfolioInfo exp = (PortfolioInfo) portfolio.get().values().toArray()[0];
            assertEquals(exp.getAveragePurchase(), response1.getAveragePurchase());
            assertEquals(exp.getCurrentPrice(), response1.getCurrentPrice());
            assertEquals(exp.getQuantity(), response1.getQuantity());
        }

        @Test
        void convertJSON_TraderPortfolio_Traded() {
            TraderDao traderDao = new TraderDao("TRADER_ID", BigInteger.valueOf(10000));
            TestDataUtil testDataUtil = new TestDataUtil();

            testDataUtil.generateTransactions(1).forEach(traderDao::addNewTrade);

            TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(traderDao);
            portfolio.add(linkTo(methodOn(TraderController.class).getPortfolio()).withSelfRel());

            List<CSVPortfolioInfo> response = (List<CSVPortfolioInfo>) csvFixer.fix(portfolio);
            CSVPortfolioInfo response1 = response.get(0);
            PortfolioInfo exp = (PortfolioInfo) portfolio.get().values().toArray()[0];
            assertEquals(exp.getAveragePurchase(), response1.getAveragePurchase());
            assertEquals(exp.getCurrentPrice(), response1.getCurrentPrice());
            assertEquals(exp.getQuantity(), response1.getQuantity());
        }
    }
}