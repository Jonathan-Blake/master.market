package com.stocktrader.market.model.dao;

import com.stocktrader.market.model.ref.OpenOrClose;

import javax.persistence.*;

@Entity
public class OpenAndClosingHistory {
    @Id
    @GeneratedValue
    Long id;
    @OneToOne(fetch = FetchType.LAZY)
    StockHistory price;
    OpenOrClose openClose;
}
