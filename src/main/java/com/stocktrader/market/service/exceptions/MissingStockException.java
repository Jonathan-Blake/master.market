package com.stocktrader.market.service.exceptions;

public class MissingStockException extends RuntimeException {
    public MissingStockException(String stock) {
        super("Could not find stock " + stock);
    }
}
