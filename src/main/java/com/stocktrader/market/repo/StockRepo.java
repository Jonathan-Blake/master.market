package com.stocktrader.market.repo;

import com.stocktrader.market.model.dao.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepo extends JpaRepository<Stock, String> {
}
