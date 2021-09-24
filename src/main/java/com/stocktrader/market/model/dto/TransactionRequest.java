package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stocktrader.market.model.ref.TransactionType;

import java.math.BigInteger;

public class TransactionRequest {

    @JsonProperty
    TransactionType transactionType;
    @JsonProperty
    String stockCode;
    @JsonProperty
    BigInteger quantity;

    public TransactionRequest(String stockCode, TransactionType transactionType, BigInteger quantity) {
        this.transactionType = transactionType;
        this.stockCode = stockCode;
        this.quantity = quantity;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getStock() {
        return stockCode;
    }

    public BigInteger getQuantity() {
        return quantity;
    }
}
