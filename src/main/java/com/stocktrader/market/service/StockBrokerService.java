package com.stocktrader.market.service;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
@EnableScheduling
public class StockBrokerService {
    @Autowired
    StockRepo stockRepo;
    @Autowired
    StockHistoryRepo stockHistoryRepo;
    private Random random = new Random();
    private Logger logger = LoggerFactory.getLogger(StockBrokerService.class);

    @Scheduled(initialDelay = 300000, fixedRate = 300000)
    void updatePrices() {
        logger.info("Updating Prices");
        stockRepo.findAll().forEach(
                this::calculateNewPrice
        );
    }

    void calculateNewPrice(final Stock stock) {
        List<StockHistory> mostRecentPrices = stockHistoryRepo.findFirst3ByStockOrderByTime(stock);
        int trend = calculateTrend(mostRecentPrices);

        double priceModifier = (random.nextDouble() + .5 + trend) * .1;
        final StockHistory mostRecentPrice = mostRecentPrices.get(mostRecentPrices.size() - 1);
        BigDecimal modifiedPrice = BigDecimal.valueOf(mostRecentPrice.getPrice().floatValue() * (1 + priceModifier));
        StockHistory newPrice = new StockHistory();
        newPrice.stock = stock;
        newPrice.time = Instant.now();
        newPrice.price = modifiedPrice.toBigInteger();
        logger.trace("Stock: {} from {} to {}", stock.getSymbol(), mostRecentPrice.price, newPrice.price);
        stockHistoryRepo.save(newPrice);
    }

    int calculateTrend(List<StockHistory> mostRecentPrices) {
        int trend = 0;
        if (mostRecentPrices.size() > 1) {
            Iterator<StockHistory> iter = mostRecentPrices.iterator();
            StockHistory i = iter.next();
            while (iter.hasNext()) {
                StockHistory y = iter.next();
                trend -= i.getPrice().compareTo(y.getPrice());
            }
        }
        return trend;
    }
}
