package com.stocktrader.market.model.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stocktrader.market.model.dto.StockResponse;

import java.math.BigInteger;
import java.time.Instant;

public class CSVStockResponse {
    @JsonProperty
    String stockCode;
    @JsonProperty
    Instant updatedOn;
    @JsonProperty
    BigInteger price;
    @JsonProperty
    BigInteger quantity;
    @JsonProperty
    Instant lastTrade;
    @JsonProperty
    BigInteger openValue;
    @JsonProperty
    BigInteger closeValue;
    @JsonProperty
    BigInteger gains;

    public CSVStockResponse(StockResponse stockResponse) {
        this.closeValue = stockResponse.getCloseValue();
        this.openValue = stockResponse.getOpenValue();
        this.updatedOn = stockResponse.getUpdatedOn();
        this.price = stockResponse.getPrice();
        this.quantity = stockResponse.getQuantity();
        this.stockCode = stockResponse.getStockCode();
        this.lastTrade = stockResponse.getLastTrade();
        this.gains = stockResponse.getGains();
    }

    public String getStockCode() {
        return stockCode;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public BigInteger getPrice() {
        return price;
    }

    public BigInteger getQuantity() {
        return quantity;
    }

    public Instant getLastTrade() {
        return lastTrade;
    }

    public BigInteger getOpenValue() {
        return openValue;
    }

    public BigInteger getCloseValue() {
        return closeValue;
    }

    public BigInteger getGains() {
        return gains;
    }
}
