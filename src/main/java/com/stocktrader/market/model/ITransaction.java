package com.stocktrader.market.model;

import com.stocktrader.market.model.ref.TransactionType;

import java.math.BigInteger;
import java.time.Instant;

public interface ITransaction {
    TransactionType getTransactionType();

    void setTransactionType(TransactionType transactionType);

    String getStockCode();

    void setStockCode(String stockCode);

    BigInteger getQuantity();

    void setQuantity(BigInteger quantity);

    Instant getTime();

    void setTime(Instant time);
}
