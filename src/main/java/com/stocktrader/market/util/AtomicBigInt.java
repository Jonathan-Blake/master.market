package com.stocktrader.market.util;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicBigInt {

    private final AtomicReference<BigInteger> valueHolder = new AtomicReference<>();

    public AtomicBigInt() {
        this(BigInteger.ZERO);
    }

    public AtomicBigInt(BigInteger initialValue) {
        valueHolder.set(initialValue);
    }

    public BigInteger incrementAndGet(BigInteger inc) {
        while (true) {
            BigInteger current = valueHolder.get();
            BigInteger next = current.add(inc);
            if (valueHolder.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public void set(BigInteger newValue) {
        valueHolder.set(newValue);
    }

    public BigInteger get() {
        return valueHolder.get();
    }
}