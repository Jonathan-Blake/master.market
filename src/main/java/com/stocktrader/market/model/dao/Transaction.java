package com.stocktrader.market.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stocktrader.market.model.ITransaction;
import com.stocktrader.market.model.ref.TransactionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@JsonIgnoreProperties("traderDao")
public class Transaction implements ITransaction {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore()
    public TraderDao traderDao;

    @NotNull
    @OneToOne
    StockHistory stockTraded;
    BigInteger quantity;
    TransactionType transactionType;
    Instant time;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    public TraderDao getTrader() {
        return traderDao;
    }

    public StockHistory getStockTraded() {
        return stockTraded;
    }

    public void setStockTraded(StockHistory stockTraded) {
        this.stockTraded = stockTraded;
    }

    public BigInteger getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(BigInteger quantity) {
        this.quantity = quantity;
    }

    @Override
    public Instant getTime() {
        return time;
    }

    @Override
    public void setTime(Instant time) {
        this.time = time;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    @Override
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String getStockCode() {
        return getStockTraded().getStock().getSymbol();
    }

    @Override
    public void setStockCode(String stockCode) {
        throw new RuntimeException("Cannot alter the stock through this method.");
    }

    public BigInteger getValue() {
        return stockTraded.getPrice().multiply(quantity);
    }
}
