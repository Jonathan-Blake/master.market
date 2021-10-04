package com.stocktrader.market.repo;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.ref.OpenOrClose;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockHistoryRepo extends PagingAndSortingRepository<StockHistory, Long> {
    Optional<StockHistory> findFirst1ByStockOrderByTimeDesc(Stock stock);

    List<StockHistory> findFirst3ByStockOrderByTimeDesc(Stock stock);

    Collection<StockHistory> findAllByStockAndTimeBetween(Stock stock, Instant startDate, Instant endDate);

    Collection<StockHistory> findAllByStock(Stock stock);

    Optional<StockHistory> findFirst1ByStockAndOpenClose_OpenCloseOrderByTime(Stock stock, OpenOrClose openOrClose);

}
