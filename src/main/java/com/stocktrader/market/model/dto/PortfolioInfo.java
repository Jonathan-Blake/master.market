package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.ref.TransactionType;

import java.math.BigInteger;

public class PortfolioInfo {
    @JsonProperty
    BigInteger price;
    @JsonProperty
    BigInteger averagePurchase = null;
    @JsonProperty
    BigInteger quantity = BigInteger.ZERO;

    public PortfolioInfo(StockHistory mostRecentPrice) {
        this.price = mostRecentPrice.getPrice();
    }

    public PortfolioInfo linkNewTrade(Transaction trade) {
        var mapper = new ObjectMapper();

        try {
            System.out.println(" on portfolio " + mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (trade.getTransactionType() == TransactionType.BUY) {
            if (averagePurchase == null) {
                averagePurchase = trade.getStockTraded().getPrice();
            } else {
                this.averagePurchase = (averagePurchase.multiply(quantity)
                        .add(trade.getStockTraded().getPrice().multiply(trade.getQuantity())))
                        .divide(this.quantity.add(trade.getQuantity())); // Rounding errors causes this to fail for very small changes and transactions
            }

        }
        this.quantity = trade.getTransactionType().transact(quantity, trade.getQuantity().negate());
        try {
            System.out.println("Post Transaction " + mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return this;
    }

    public BigInteger getTotalValue() {
        return price.multiply(quantity);
    }

}
