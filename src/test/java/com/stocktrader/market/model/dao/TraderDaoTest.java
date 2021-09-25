package com.stocktrader.market.model.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.TestDataUtil;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.ref.TransactionType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraderDaoTest {
    public static final BigInteger TRADER_FUNDS = BigInteger.valueOf(1000L);
    public static final BigInteger QUANTITY = BigInteger.valueOf(10L);
    static Stock[] validStocks;
    private final TestDataUtil testDataUtil = new TestDataUtil();
    @Mock
    private StockHistory mockStockHistory;

    private List<Transaction> generateTransactions(int i) {
        return testDataUtil.generateTransactions(i);
    }

    private Transaction generateTransaction() {
        return testDataUtil.generateTransaction(new TestDataUtil.GenerateTransactionParams(10000, 500, null));
    }

    @Nested
    class AddNewTrade {
        @Test
        void addTrade_whenNoTrades_andReducesFundsForPurchase() {
            final BigInteger value = BigInteger.valueOf(10);
            when(mockStockHistory.getPrice()).thenReturn(value);
            when(mockStockHistory.getStock()).thenReturn(new Stock("AAA", value));

            Transaction transaction = new Transaction();
            transaction.stockTraded = mockStockHistory;
            transaction.transactionType = TransactionType.BUY;
            transaction.quantity = QUANTITY;
            transaction.time = Instant.now();

            TraderDao traderDao = new TraderDao();
            traderDao.funds = TRADER_FUNDS;

            traderDao.addNewTrade(transaction);

            verify(mockStockHistory).getPrice();
            assertTrue(traderDao.trades.contains(transaction));
            assertEquals(TRADER_FUNDS.subtract(BigInteger.valueOf(100L)), traderDao.funds);
            assertEquals(traderDao, transaction.traderDao);
            assertEquals(TRADER_FUNDS, TraderPortfolio.buildPortfolio(traderDao).getTotalValue());
        }

        @Test
        void addTrade_whenNoTrades_andIncreasesFundsForPurchase() {
            final BigInteger value = BigInteger.valueOf(10);
            when(mockStockHistory.getPrice()).thenReturn(value);
            when(mockStockHistory.getStock()).thenReturn(new Stock("AAA", value));

            Transaction transaction = new Transaction();
            transaction.stockTraded = mockStockHistory;
            transaction.transactionType = TransactionType.SELL;
            transaction.quantity = QUANTITY;
            transaction.time = Instant.now();

            TraderDao traderDao = new TraderDao();
            traderDao.funds = TRADER_FUNDS;

            traderDao.addNewTrade(transaction);

            verify(mockStockHistory).getPrice();
            assertTrue(traderDao.trades.contains(transaction));
            assertEquals(TRADER_FUNDS.add(BigInteger.valueOf(100L)), traderDao.funds);
            assertEquals(traderDao, transaction.traderDao);
            assertEquals(TRADER_FUNDS, TraderPortfolio.buildPortfolio(traderDao).getTotalValue());
        }

        @Test
        void addTrade_toExistingTrades_andReducesFundsForPurchase() {
            final BigInteger value = BigInteger.valueOf(10);
            when(mockStockHistory.getPrice()).thenReturn(value);
            when(mockStockHistory.getStock()).thenReturn(new Stock("AAA", value));

            Transaction transaction = new Transaction();
            transaction.stockTraded = mockStockHistory;
            transaction.transactionType = TransactionType.BUY;
            transaction.quantity = QUANTITY;
            transaction.time = Instant.now();

            TraderDao traderDao = new TraderDao();
            traderDao.funds = TRADER_FUNDS;
            for (int i = 0; i < 5; i++) {
                traderDao.addNewTrade(transaction);
            }

            traderDao.addNewTrade(transaction);

            verify(mockStockHistory, times(6)).getPrice();
            assertEquals(6, traderDao.trades.size());
            assertTrue(traderDao.trades.contains(transaction));
            assertEquals(TRADER_FUNDS.subtract(BigInteger.valueOf(600L)), traderDao.funds);
            assertEquals(traderDao, transaction.traderDao);

            assertEquals(TRADER_FUNDS, TraderPortfolio.buildPortfolio(traderDao).getTotalValue());
        }

        @Test
        void addTrade_toExistingTrades_andIncreasesFundsForPurchase() {
            final BigInteger value = BigInteger.valueOf(10);
            when(mockStockHistory.getPrice()).thenReturn(value);
            when(mockStockHistory.getStock()).thenReturn(new Stock("AAA", value));

            Transaction transaction = new Transaction();
            transaction.stockTraded = mockStockHistory;
            transaction.transactionType = TransactionType.SELL;
            transaction.quantity = QUANTITY;
            transaction.time = Instant.now();

            TraderDao traderDao = new TraderDao();
            traderDao.funds = TRADER_FUNDS;
            for (int i = 0; i < 5; i++) {
                traderDao.addNewTrade(transaction);
            }

            traderDao.addNewTrade(transaction);

            verify(mockStockHistory, times(6)).getPrice();
            assertEquals(6, traderDao.trades.size());
            assertTrue(traderDao.trades.contains(transaction));
            assertEquals(TRADER_FUNDS.add(BigInteger.valueOf(600L)), traderDao.funds);
            assertEquals(traderDao, transaction.traderDao);
            assertEquals(TRADER_FUNDS, TraderPortfolio.buildPortfolio(traderDao).getTotalValue());
        }

        @Test
        void addTrade_alwaysSumsToSameNextTotal() { // This test is a fantastic example of the scaling concerns as
            TraderDao traderDao = new TraderDao();
            traderDao.funds = TRADER_FUNDS;
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            for (Transaction transaction : testDataUtil.generateTransactions(500, new TestDataUtil.GenerateTransactionParams(100, 50L, null))) {
                transaction.getStockTraded().setPrice(BigInteger.TEN); // Price must always be constant otherwise portfolio value will increase.
                traderDao.addNewTrade(transaction);
                final TraderPortfolio traderPortfolio = TraderPortfolio.buildPortfolio(traderDao);
                assertEquals(TRADER_FUNDS, traderPortfolio.getTotalValue());
            }
        }
    }
}