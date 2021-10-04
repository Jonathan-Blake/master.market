package com.stocktrader.market.service.reportconvertor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stocktrader.market.TestDataUtil;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.dto.TraderPortfolio;
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

class CSVConvertorTest {

    CSVConvertor csvConvertor = CSVConvertor.get();

    private void fileReadsBackToObject(File file, Object object) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(object.getClass()).withHeader();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Object readValue = mapper.readerFor(object.getClass()).with(schema).readValue(file, object.getClass());
        assertEquals(object, readValue);
    }

    private void fileReadsBackToObjectList(File file, Object object) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(object.getClass()).withHeader();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        MappingIterator<Object> readValue = mapper.readerFor(object.getClass()).with(schema).readValues(file);
        final List<Object> actual = readValue.readAll();
        System.out.println(actual.get(0).getClass());
        assertEquals(object, actual);
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

            AutoDeletingFile response = csvConvertor.convertJSON(sr, sr.getClass());

            fileReadsBackToObject(response.getFile(), sr);
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

            Page<StockResponse> page = new PageImpl<>(List.of(sr));

            AutoDeletingFile response = csvConvertor.convertJSON(page, page.getClass());
            fileReadsBackToObject(response.getFile(), sr);
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

            StockResponse[] srArray = {sr};

            AutoDeletingFile response = csvConvertor.convertJSON(srArray, sr.getClass());
            fileReadsBackToObject(response.getFile(), sr);
        }

    }

    @Nested
    class TraderPortfolioTest {
        @Test
        void convertJSON_TraderPortfolio_Empty() throws IOException {
            TraderDao traderDao = new TraderDao("TRADER_ID", BigInteger.valueOf(10000));
            TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(traderDao);

            AutoDeletingFile response = csvConvertor.convertJSON(portfolio, portfolio.getClass());
            fileReadsBackToObjectList(response.getFile(), new ArrayList<>(portfolio.get().values()));
        }

        @Test
        void convertJSON_TraderPortfolio_Traded() throws IOException {
            TraderDao traderDao = new TraderDao("TRADER_ID", BigInteger.valueOf(10000));
            TestDataUtil testDataUtil = new TestDataUtil();

            testDataUtil.generateTransactions(1).forEach(traderDao::addNewTrade);

            TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(traderDao);

            AutoDeletingFile response = csvConvertor.convertJSON(portfolio, portfolio.getClass());
            fileReadsBackToObjectList(response.getFile(), new ArrayList<>(portfolio.get().values()));
        }
    }
}