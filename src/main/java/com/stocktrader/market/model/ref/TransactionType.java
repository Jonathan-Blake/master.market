package com.stocktrader.market.model.ref;

import java.math.BigInteger;
import java.util.function.BinaryOperator;

public enum TransactionType {
    BUY(BigInteger::subtract),
    SELL(BigInteger::add);

    private final BinaryOperator<BigInteger> transaction;

    TransactionType(BinaryOperator<BigInteger> functionalInterface) {
        this.transaction = functionalInterface;
    }

    public BigInteger transact(BigInteger existingBalance, BigInteger transactionValue) {
        return transaction.apply(existingBalance, transactionValue);
    }
}
