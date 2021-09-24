package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stocktrader.market.model.ITransaction;
import com.stocktrader.market.model.ref.TransactionType;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigInteger;
import java.time.Instant;

public class TransactionResponse extends RepresentationModel<TransactionResponse> implements ITransaction {
    @JsonProperty
    TransactionType transactionType;
    @JsonProperty
    String stockCode;
    @JsonProperty
    BigInteger quantity;
    @JsonProperty
    Instant time;

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public BigInteger getQuantity() {
        return quantity;
    }

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
}
