package com.stocktrader.market.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stocktrader.market.model.ref.OpenOrClose;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.Instant;

@Entity
public class StockHistory {
    @NotNull
    @ManyToOne()
    public Stock stock;
    @NotNull
    public BigInteger price;
    @NotNull
    public Instant time;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    OpenAndClosingHistory openClose;
    @Id
    @GeneratedValue
    Long id;

    public Long getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public Instant getTime() {
        return time;
    }

    public void setOpenOrClosed(OpenOrClose openOrClose) {
        var event = new OpenAndClosingHistory();
        event.price = this;
        event.openClose = openOrClose;
        this.openClose = event;
    }

    @JsonIgnore
    public boolean isOpeningTime() {
        return openClose != null && OpenOrClose.OPEN == openClose.openClose;
    }

    @JsonIgnore
    public boolean isClosingTime() {
        return openClose != null && OpenOrClose.CLOSE == openClose.openClose;
    }

}
