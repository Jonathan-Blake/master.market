package com.stocktrader.market.model.dao;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
public class Stock {
    @Id
    String symbol;
    @NotNull
    BigInteger totalQuantity;

    public Stock() {
    }

    public Stock(String symbol, BigInteger totalQuantity) {
        this.symbol = symbol;
        this.totalQuantity = totalQuantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigInteger getTotalQuantity() {
        return totalQuantity;
    }
}
