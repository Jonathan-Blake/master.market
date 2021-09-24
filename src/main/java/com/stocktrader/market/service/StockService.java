package com.stocktrader.market.service;

import com.stocktrader.market.controller.StockController;
import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.ref.OpenOrClose;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import com.stocktrader.market.repo.TransactionRepo;
import com.stocktrader.market.util.AtomicBigInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class StockService {
    @Autowired
    StockRepo stockRepo;
    @Autowired
    StockHistoryRepo stockHistoryRepo;
    @Autowired
    TransactionRepo transactionRepo;

    public Page<StockResponse> getStocksCurrentPrice(PageRequest pageRequest) {
        //Chose not to use Page's map method because it is not parallel, due to the database calls this seemed to outweigh
        // the performance impact that synchronisation might have otherwise it mimics the logic
        final Page<Stock> allStocks = stockRepo.findAll(pageRequest);
        final List<StockResponse> convertedList = allStocks.stream().parallel()
                .map(stock -> buildResponse(
                        stock,
                        stockHistoryRepo.findFirst1ByStockOrderByTime(stock).orElse(new StockHistory()),
                        getCurrentlyTradeableStockQuantity(stock))
                ).collect(Collectors.toList());
        return new PageImpl<>(convertedList, allStocks.getPageable(), allStocks.getTotalElements());
    }

    public StockResponse getStockCurrentDetails(String symbol) {
        Optional<Stock> stock = stockRepo.findById(symbol);
        if (stock.isPresent()) {
            Optional<StockHistory> stockPrice = stockHistoryRepo.findFirst1ByStockOrderByTime(stock.get());
            return buildResponse(stock.get(), stockPrice.get(), getCurrentlyTradeableStockQuantity(stock.get()));
        } else {
            return null;
        }
    }

    public StockResponse buildResponse(Stock stock, StockHistory stockHistory, BigInteger currentlyTradeableStockQuantity) {
        var ret = new StockResponse();
        ret.setStockCode(stock.getSymbol());
        ret.setUpdatedOn(stockHistory.time);
        ret.setLastTrade(transactionRepo.findFirst1ByStockTraded_StockOrderByTime(stock).orElseGet(Transaction::new).getTime());
        ret.setOpenValue(stockHistory.isOpeningTime() ?
                stockHistory.price :
                stockHistoryRepo.findFirst1ByStockAndOpenClose_OpenCloseOrderByTime(stock, OpenOrClose.OPEN).orElseGet(() -> {
                    var h = new StockHistory();
                    h.price = BigInteger.valueOf(0);
                    return h;
                }).price);
        ret.setCloseValue(stockHistory.isClosingTime() ?
                stockHistory.price :
                stockHistoryRepo.findFirst1ByStockAndOpenClose_OpenCloseOrderByTime(stock, OpenOrClose.CLOSE).orElseGet(StockHistory::new).price);
        ret.setPrice(stockHistory.price);
        ret.setQuantity(currentlyTradeableStockQuantity);
        ret.add(linkTo(methodOn(StockController.class).getStock(stock.getSymbol())).withSelfRel());
        return ret;
    }

    public Page<StockResponse> getPriceHistoryOfStocks(PageRequest pageRequest, Instant startDate, Instant endDate) {
        //Chose not to use Page's map method because it is not parallel, due to the database calls this seemed to outweigh
        // the performance impact that synchronisation might have otherwise it mimics the logic
        final Page<Stock> allStocks = stockRepo.findAll(pageRequest);
        List<StockResponse> convertedList = allStocks.stream().parallel()
                .flatMap(stock -> buildResponseHistory(
                        stock,
                        stockHistoryRepo.findAllByStockAndTimeBetween(stock, startDate, endDate))
                ).collect(Collectors.toList());
        return new PageImpl<>(convertedList, allStocks.getPageable(), allStocks.getTotalElements());
    }

    public Stream<StockResponse> buildResponseHistory(Stock stock, Collection<StockHistory> history) {
        return history.stream().map(each -> buildResponse(stock, each, stock.getTotalQuantity()));
    }

    public BigInteger getCurrentlyTradeableStockQuantity(Stock stock) {
        Collection<Transaction> transactions = transactionRepo.findAllByStockTraded_Stock(stock);
        var quantity = new AtomicBigInt(stock.getTotalQuantity());
        transactions.forEach(transaction -> quantity.set(
                transaction.getTransactionType().transact(
                        quantity.get(),
                        (transaction.getQuantity().divide(transaction.getStockTraded().price)))));
        return quantity.get();
    }
}
