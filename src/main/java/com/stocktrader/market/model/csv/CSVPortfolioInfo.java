package com.stocktrader.market.model.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stocktrader.market.model.dto.PortfolioInfo;

import java.math.BigInteger;

public class CSVPortfolioInfo {
    @JsonProperty
    String stockCode;
    @JsonProperty
    BigInteger currentPrice;
    @JsonProperty
    BigInteger averagePurchase;
    @JsonProperty
    BigInteger quantity;

    public CSVPortfolioInfo(String stockCode, PortfolioInfo portfolioInfo) {
        this.stockCode = stockCode;
        this.currentPrice = portfolioInfo.getCurrentPrice();
        this.averagePurchase = portfolioInfo.getAveragePurchase();
        this.quantity = portfolioInfo.getQuantity();
    }

    public BigInteger getCurrentPrice() {
        return currentPrice;
    }

    public BigInteger getAveragePurchase() {
        return averagePurchase;
    }

    public BigInteger getQuantity() {
        return quantity;
    }
}
