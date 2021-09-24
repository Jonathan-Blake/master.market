package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.ITrader;
import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.util.AtomicBigInt;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TraderPortfolio extends RepresentationModel<TraderPortfolio> {
    @JsonProperty
    Map<String, PortfolioInfo> portfolio;

    public TraderPortfolio(BigInteger funds, List<Transaction> trades) {
        this.portfolio = new LinkedHashMap<>();
        var fundsCurrent = new StockHistory();
        fundsCurrent.stock = new Stock();
        fundsCurrent.price = BigInteger.ONE;
        var fundsAsStock = new PortfolioInfo(fundsCurrent);
        fundsAsStock.quantity = funds;
        this.portfolio.put("funds", fundsAsStock);
        if (trades != null) {
            trades.stream().sorted(Comparator.comparing(Transaction::getTime)).forEach(trade -> this.portfolio.merge(
                    trade.getStockCode(),
                    new PortfolioInfo(trade.getStockTraded()).linkNewTrade(trade),
                    (prev, next) -> prev.linkNewTrade(trade)
            ));
        }
    }

    public static TraderPortfolio buildPortfolio(ITrader trader) {
        return new TraderPortfolio(trader.getFunds(), trader.getTrades());
    }

    public BigInteger getTotalValue() {
        var value = new AtomicBigInt();
        portfolio.values().forEach(portfolioInfo -> {
            BigInteger current = value.get();
            if (current.compareTo(value.incrementAndGet(portfolioInfo.getTotalValue())) == portfolioInfo.getTotalValue().compareTo(BigInteger.ZERO)) {
                throw new RuntimeException("Integer Overflow");
            }
        });
        return value.get().or(BigInteger.ZERO);
    }

    public int size() {
        return portfolio.size();
    }

    public Map<String, PortfolioInfo> get() {
        return portfolio;
    }

    @Override
    public String toString() {
        var mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return super.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        System.out.println("In Equals");
        if (obj instanceof TraderPortfolio) {
            System.out.println("In Equals");
            return this.portfolio.equals(((TraderPortfolio) obj).portfolio);
        } else {
            return super.equals(obj);
        }
    }
}
