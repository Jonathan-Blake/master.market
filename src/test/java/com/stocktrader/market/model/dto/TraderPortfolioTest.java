package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.ITrader;
import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.ref.TransactionType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraderPortfolioTest {
    public static final BigInteger TRADER_FUNDS = BigInteger.valueOf(1000L);
    private static final String ID = "ID";
    @Mock
    private StockHistory mockStockHistory;
    @Mock
    private Stock mockStock;

    @Nested
    class BuildPortfolio {

        public static final String STOCK_CODE = "test";

        @Test
        void transactionlessTrader_onlyContains_CashValue() {
            ITrader trader = new TraderDao(ID, TRADER_FUNDS);

            TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(trader);

            assertEquals(1, portfolio.size());
            assertTrue(portfolio.get().containsKey("funds"));
            assertEquals(TRADER_FUNDS, portfolio.getTotalValue());
        }

        @Test
        void traderWithOneTransaction_contains_cashValueAndSingleStock() {
            when(mockStockHistory.getPrice()).thenReturn(BigInteger.valueOf(10));
            when(mockStockHistory.getStock()).thenReturn(mockStock);
            when(mockStock.getSymbol()).thenReturn(STOCK_CODE);

            Transaction transaction = new Transaction();
            transaction.setTransactionType(TransactionType.BUY);
            transaction.setStockTraded(mockStockHistory);
            transaction.setQuantity(BigInteger.valueOf(10L));

            ITrader trader = new TraderDao(ID, TRADER_FUNDS, List.of(transaction));

            TraderPortfolio portfolio = TraderPortfolio.buildPortfolio(trader);

            assertEquals(2, portfolio.size());
            assertTrue(portfolio.get().containsKey("funds"));
            ObjectMapper mapper = new ObjectMapper();
            try {
                System.out.println(mapper.writeValueAsString(portfolio.get()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            assertTrue(portfolio.get().containsKey(STOCK_CODE));
            assertEquals(BigInteger.valueOf(100), portfolio.get().get(STOCK_CODE).getTotalValue());
            assertEquals(TRADER_FUNDS.add(BigInteger.valueOf(100)), portfolio.getTotalValue());
        }
    }

}