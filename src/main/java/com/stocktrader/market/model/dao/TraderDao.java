package com.stocktrader.market.model.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stocktrader.market.model.ITrader;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

@Entity
public class TraderDao implements ITrader {
    @Id
    String id;
    BigInteger funds;
    @OneToMany(fetch = FetchType.EAGER)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<Transaction> trades;

    public TraderDao() {
    }

    public TraderDao(String id, BigInteger funds) {
        this.id = id;
        this.funds = funds;
    }

    public TraderDao(String id, BigInteger funds, List<Transaction> trades) {
        this(id, funds);
        this.trades = trades;
    }

    public void addNewTrade(Transaction trade) {
        if (trades == null) {
            this.trades = new LinkedList<>();
        }
        this.trades.add(trade);
        trade.traderDao = this;
        funds = trade.transactionType.transact(funds, trade.getValue());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BigInteger getFunds() {
        return funds;
    }

    public List<Transaction> getTrades() {
        return trades;
    }
}
