package com.stocktrader.market.service.report.convertor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stocktrader.market.TestDataUtil;
import com.stocktrader.market.controller.StockController;
import com.stocktrader.market.controller.TraderController;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.PortfolioInfo;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.service.report.fixer.CSVFixer;
import com.stocktrader.market.util.AutoDeletingFile;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class CSVConvertorTest {

    CSVConvertor csvConvertor = new CSVConvertor();

//    private void fileReadsBackToObject(File file, Object object) throws IOException {
//        CsvMapper mapper = new CsvMapper();
//        CsvSchema schema = mapper.schemaFor(object.getClass()).withHeader();
//        mapper.findAndRegisterModules();
//        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        Object readValue = mapper.readerFor(object.getClass()).with(schema).readValue(file, object.getClass());
//        assertEquals(object, readValue);
//    }

    private void fileReadsBackToPortfolioInfoList(File file, List<PortfolioInfo> expected) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(PortfolioInfo.class).withHeader();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        MappingIterator<PortfolioInfo> readValue = mapper.readerFor(PortfolioInfo.class).with(schema).readValues(file);
        expected.forEach(exp -> {
            PortfolioInfo actual = readValue.next();
            assertEquals(exp.getQuantity(), actual.getQuantity());
            assertEquals(exp.calculateTotalValue(), actual.calculateTotalValue());
        });
    }

    private void fileReadsBackToStockResponseList(File file, List<StockResponse> expected) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(StockResponse.class).withHeader();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        MappingIterator<StockResponse> readValue = mapper.readerFor(StockResponse.class).with(schema).readValues(file);
        final List<StockResponse> actual = readValue.readAll();
        assertEquals(expected, actual);
    }

    @Nested
    class StockResponseTests {
        @Test
        void convertJSON_StockResponse() throws IOException {
            StockResponse sr = new StockResponse();
            sr.setQuantity(BigInteger.TEN);
            sr.setCloseValue(BigInteger.TWO);
            sr.setOpenValue(BigInteger.ONE);
            sr.setPrice(BigInteger.TEN);
            sr.setStockCode("STOCK");
            sr.setUpdatedOn(Instant.now());
            sr.add(linkTo(methodOn(StockController.class).getStock("STOCK")).withSelfRel());

            Object fixedStockResponse = new CSVFixer().fix(sr);

            AutoDeletingFile response = csvConvertor.convertJSON(fixedStockResponse, fixedStockResponse.getClass());

            fileReadsBackToStockResponseList(response.getFile(), List.of(sr));
        }

        @Test
        void convertJSON_StockResponsePage() throws IOException {
            StockResponse sr = new StockResponse();
            sr.setQuantity(BigInteger.TEN);
            sr.setCloseValue(BigInteger.TWO);
            sr.setOpenValue(BigInteger.ONE);
            sr.setPrice(BigInteger.TEN);
            sr.setStockCode("STOCK");
            sr.setUpdatedOn(Instant.now());
            sr.add(linkTo(methodOn(StockController.class).getStock("STOCK")).withSelfRel());

            Page<StockResponse> page = new PageImpl<>(List.of(sr));
            Object fixedArray = new CSVFixer().fix(page);

            AutoDeletingFile response = csvConvertor.convertJSON(fixedArray, fixedArray.getClass());
            fileReadsBackToStockResponseList(response.getFile(), List.of(sr));
        }

        @Test
        void convertJSON_StockResponseArray() throws IOException {
            StockResponse sr = new StockResponse();
            sr.setQuantity(BigInteger.TEN);
            sr.setCloseValue(BigInteger.TWO);
            sr.setOpenValue(BigInteger.ONE);
            sr.setPrice(BigInteger.TEN);
            sr.setStockCode("STOCK");
            sr.setUpdatedOn(Instant.now());
            sr.add(linkTo(methodOn(StockController.class).getStock("STOCK")).withSelfRel());

            StockResponse[] srArray = {sr};
            Object fixedArray = new CSVFixer().fix(srArray);
            AutoDeletingFile response = csvConvertor.convertJSON(fixedArray, fixedArray.getClass());
            fileReadsBackToStockResponseList(response.getFile(), List.of(sr));
        }

    }

    @Nested
    class TraderPortfolioTest {
        @Test
        void convertJSON_TraderPortfolio_Empty() throws IOException {
            TraderDao traderDao = new TraderDao("TRADER_ID", BigInteger.valueOf(10000));
            TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(traderDao);
            portfolio.add(linkTo(methodOn(TraderController.class).getPortfolio()).withSelfRel());
            Object fixedPortfolio = new CSVFixer().fix(portfolio);

            AutoDeletingFile response = csvConvertor.convertJSON(fixedPortfolio, portfolio.getClass());
            fileReadsBackToPortfolioInfoList(response.getFile(), new ArrayList<>(portfolio.get().values()));
        }

        @Test
        void convertJSON_TraderPortfolio_Traded() throws IOException {
            TraderDao traderDao = new TraderDao("TRADER_ID", BigInteger.valueOf(10000));
            TestDataUtil testDataUtil = new TestDataUtil();

            testDataUtil.generateTransactions(1).forEach(traderDao::addNewTrade);

            TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(traderDao);
            portfolio.add(linkTo(methodOn(TraderController.class).getPortfolio()).withSelfRel());
            Object fixedPortfolio = new CSVFixer().fix(portfolio);

            AutoDeletingFile response = csvConvertor.convertJSON(fixedPortfolio, portfolio.getClass());
            fileReadsBackToPortfolioInfoList(response.getFile(), new ArrayList<>(portfolio.get().values()));
        }
    }
}