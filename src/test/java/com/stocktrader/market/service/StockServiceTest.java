package com.stocktrader.market.service;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.ref.TransactionType;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import com.stocktrader.market.repo.TransactionRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    public static final Instant NOW = Instant.now();
    private static final BigInteger QUANTITY = BigInteger.valueOf(1000L);
    private static List<Stock> stockList;
    private static List<StockHistory> stockHistoryList;
    @Mock
    StockRepo stockRepo;
    @Mock
    StockHistoryRepo stockHistoryRepo;
    @Mock
    TransactionRepo transactionRepo;
    @InjectMocks
    StockService stockService;
    @Mock
    private Page<Stock> mockStocks;
    private PageRequest mockPageRequest = PageRequest.of(0, 10);
    private Queue<StockHistory> stockHistoryQueue;

    @BeforeAll
    static void init() {
        Stock stockOne = new Stock("AAA", BigInteger.valueOf(100L));

        stockList = List.of(stockOne);

        StockHistory stockHistoryOne = new StockHistory();
        stockHistoryOne.stock = stockOne;
        stockHistoryOne.time = NOW;
        stockHistoryOne.price = BigInteger.valueOf(50);
        stockHistoryList = List.of(stockHistoryOne);
    }

    @BeforeEach
    void resetQueue() {
        stockHistoryQueue = new PriorityQueue<>(stockHistoryList);
    }

    @Test
    void getStocksCurrentPrice() {
        when(stockRepo.findAll(any(PageRequest.class))).thenReturn(mockStocks);
        when(mockStocks.getPageable()).thenReturn(mockPageRequest);
        when(mockStocks.stream()).thenReturn(stockList.stream());
        when(transactionRepo.findAllByStockTraded_Stock(any(Stock.class))).thenReturn(List.of());
//        doReturn(QUANTITY).when(stockService.getCurrentlyTradeableStockQuantity(any(Stock.class)));

        StockResponse responseA = stockService.buildResponse(stockList.get(0), stockHistoryList.get(0), QUANTITY);
        StockResponse[] expectedItems = new StockResponse[]{
                responseA
        };

        Page<StockResponse> stockPrices = stockService.getStocksCurrentPrice(mockPageRequest);

        verify(stockRepo).findAll(mockPageRequest);
        assertEquals(1, stockPrices.getTotalElements());
        assertThat(stockPrices.getContent(), hasItems(expectedItems));
        assertEquals(mockPageRequest, stockPrices.getPageable());
    }

    @Test
    void getStockCurrentPrice_whenThereIsNoSuchStock() {
        when(stockRepo.findById(anyString())).thenReturn(Optional.empty());

        assertNull(stockService.getStockCurrentDetails("AAA"));
    }

    @Test
    void getStockCurrentPrice_whenThereIsAStock() {
        when(stockRepo.findById(anyString())).thenReturn(Optional.of(stockList.get(0)));
        when(stockHistoryRepo.findFirst1ByStockOrderByTime(any(Stock.class))).thenReturn(Optional.of(stockHistoryList.get(0)));

        lenient().when(transactionRepo.findAllByStockTraded_Stock(any(Stock.class))).thenReturn(List.of());

        StockResponse stockPrice = stockService.getStockCurrentDetails("AAA");

        verify(stockHistoryRepo).findFirst1ByStockOrderByTime(stockList.get(0));
        assertEquals(stockService.buildResponse(stockList.get(0), stockHistoryList.get(0), QUANTITY), stockPrice);
    }

    @Test
    void getCurrentlyTradeableStockQuantity_SinglePurchase() {
        when(transactionRepo.findAllByStockTraded_Stock(any(Stock.class))).thenReturn(List.of(
                createTransaction(BigInteger.valueOf(50L), TransactionType.BUY, stockHistoryList.get(0))
        ));

        BigInteger currentQuantity = stockService.getCurrentlyTradeableStockQuantity(stockList.get(0));

        assertEquals(BigInteger.valueOf(99), currentQuantity);
        verify(transactionRepo).findAllByStockTraded_Stock(stockList.get(0));
    }

    @Test
    void getCurrentlyTradeableStockQuantity_MultipleTransactions() {
        when(transactionRepo.findAllByStockTraded_Stock(any(Stock.class))).thenReturn(List.of(
                createTransaction(BigInteger.valueOf(50L), TransactionType.BUY, stockHistoryList.get(0)),
                createTransaction(BigInteger.valueOf(500L), TransactionType.BUY, stockHistoryList.get(0)),
                createTransaction(BigInteger.valueOf(50L), TransactionType.SELL, stockHistoryList.get(0))
        ));

        BigInteger currentQuantity = stockService.getCurrentlyTradeableStockQuantity(stockList.get(0));

        assertEquals(BigInteger.valueOf(90), currentQuantity);
        verify(transactionRepo).findAllByStockTraded_Stock(stockList.get(0));
    }

    private Transaction createTransaction(BigInteger quantity, TransactionType transactionType, StockHistory stockHistory) {
        Transaction t = new Transaction();
        t.setQuantity(quantity);
        t.setStockTraded(stockHistory);
        t.setTransactionType(transactionType);
        return t;
    }
}