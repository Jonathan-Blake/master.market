package com.stocktrader.market;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.ref.TransactionType;

import java.math.BigInteger;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TestDataUtil {

    private static Random random = new Random();
    private Stock[] validStocks;

    public TestDataUtil() {
        Stock s1 = new Stock("AAA", BigInteger.valueOf(100L));
        Stock s2 = new Stock("BBB", BigInteger.valueOf(200L));
        Stock s3 = new Stock("CCC", BigInteger.valueOf(300L));
        validStocks = new Stock[]{s1, s2, s3};
    }

    public List<Transaction> generateTransactions(int i) {
        return generateTransactions(i, new GenerateTransactionParams(10000, 500L, null));
    }

    public List<Transaction> generateTransactions(int i, GenerateTransactionParams params) {
        List<Transaction> trades = new LinkedList<>();
        while (i-- > 0) {
            trades.add(generateTransaction(params));
        }
        return trades;
    }

    public Transaction generateTransaction(GenerateTransactionParams params) {
        Transaction transaction = new Transaction();
        StockHistory stockHistory = new StockHistory();
        stockHistory.price = BigInteger.valueOf(random.nextInt(params.getPrice()) + 1);
        stockHistory.stock = validStocks[random.nextInt(validStocks.length)];
        transaction.setStockTraded(stockHistory);
        transaction.setTransactionType(params.getTransactionType());
        transaction.setQuantity(BigInteger.valueOf(random.longs(1L, 1000L).findFirst().orElse(50L)));
        transaction.setTime(Instant.now());
        return transaction;
    }

    public static class GenerateTransactionParams {
        private final int price;
        private final long quantity;
        private final TransactionType transactionType;

        public GenerateTransactionParams(int price, long quantity, TransactionType transactionType) {
            this.price = price;
            this.quantity = quantity;
            this.transactionType = transactionType;
        }

        public int getPrice() {
            return price;
        }

        public long getQuantity() {
            return quantity;
        }

        public TransactionType getTransactionType() {
            if (transactionType == null) {
                return TransactionType.values()[random.nextInt(TransactionType.values().length)];
            } else {
                return transactionType;
            }
        }
    }
}