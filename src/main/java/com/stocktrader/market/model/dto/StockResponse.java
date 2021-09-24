package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigInteger;
import java.time.Instant;

public class StockResponse extends RepresentationModel<StockResponse> {
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

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public BigInteger getQuantity() {
        return quantity;
    }

    public void setQuantity(BigInteger quantity) {
        this.quantity = quantity;
    }

    public Instant getLastTrade() {
        return lastTrade;
    }

    public void setLastTrade(Instant lastTrade) {
        this.lastTrade = lastTrade;
    }

    public BigInteger getOpenValue() {
        return openValue;
    }

    public void setOpenValue(BigInteger openValue) {
        this.openValue = openValue;
    }

    public BigInteger getCloseValue() {
        return closeValue;
    }

    public void setCloseValue(BigInteger closeValue) {
        this.closeValue = closeValue;
    }

    @JsonProperty
    BigInteger getGains() {
        return price.subtract(openValue);
    }
}
