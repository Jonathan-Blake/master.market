package com.stocktrader.market.model;

import com.stocktrader.market.model.dao.Transaction;

import java.math.BigInteger;
import java.util.List;

public interface ITrader {
    List<Transaction> getTrades();

    String getId();

    BigInteger getFunds();
}
