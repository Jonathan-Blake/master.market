package com.stocktrader.market.model.dto;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class StockResponseTest {

    public static final String STOCK_CODE = "AAA";
    public static final Instant NOW = Instant.now();
    public static final BigInteger PRICE = BigInteger.valueOf(50);
    public static final StockHistory STOCK_HISTORY = new StockHistory();
    private static final BigInteger STOCK_QUANTITY = BigInteger.valueOf(100L);
    public static final Stock STOCK = new Stock(STOCK_CODE, STOCK_QUANTITY);

    @BeforeAll
    static void setup() {
        STOCK_HISTORY.time = NOW;
        STOCK_HISTORY.price = PRICE;
    }

//    @Test
//    void buildResponseEmptyStockHistory() {
//        StockResponse ret = StockResponse.buildResponse(STOCK, new StockHistory(), null );
//
//        assertEquals(STOCK_CODE, ret.stockCode);
//        assertNull(ret.price);
//        assertNull(ret.updatedOn);
//        assertNull(ret.quantity);
//    }

//    @Test
//    void buildResponseWithStockHistory() {
//
//        StockResponse ret = StockResponse.buildResponse(STOCK, STOCK_HISTORY, null);
//
//        assertEquals(STOCK_CODE, ret.stockCode);
//        assertEquals(PRICE, ret.price);
//        assertEquals(NOW, ret.updatedOn);
//        assertNull(ret.quantity);
//    }

//    @Test
//    void buildResponseForEmptyList() {
//        Stream ret = StockResponse.buildResponseHistory(STOCK, Collections.EMPTY_LIST);
//        assertEquals(0, ret.count());
//    }

//    @Test
//    void buildResponseForSingletonList() {
//        Stream<StockResponse> ret = StockResponse.buildResponseHistory(STOCK, Collections.singleton(STOCK_HISTORY));
//        List<StockResponse> list = ret.collect(Collectors.toList());
//        assertEquals(1, list.size());
//        StockResponse item = list.get(0);
//        assertEquals(STOCK_CODE, item.stockCode);
//        assertEquals(PRICE, item.price);
//        assertEquals(NOW, item.updatedOn);
//    }

}