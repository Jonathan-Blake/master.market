package com.stocktrader.market.repo;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    Collection<Transaction> findAllByStockTraded_Stock(Stock stock);

    Optional<Transaction> findFirst1ByStockTraded_StockOrderByTime(Stock stock);
}
