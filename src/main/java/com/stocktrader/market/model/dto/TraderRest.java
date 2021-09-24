package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stocktrader.market.model.ITrader;
import com.stocktrader.market.model.dao.Transaction;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public class TraderRest extends RepresentationModel<TraderRest> implements ITrader {
    String id;
    BigInteger funds;
    @JsonIgnore
    List<Transaction> trades;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public BigInteger getFunds() {
        return funds;
    }

    public void setFunds(BigInteger funds) {
        this.funds = funds;
    }

    @Override
    public List<Transaction> getTrades() {
        return trades;
    }

    public void setTrades(List<Transaction> trades) {
        this.trades = trades;
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(this, obj);
    }
}
