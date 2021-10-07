package com.stocktrader.market.service;

import com.stocktrader.market.model.ITrader;
import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    @Autowired
    private StockRepo stockRepo;
    @Autowired
    private StockHistoryRepo stockHistoryRepo;

    public TraderPortfolio getTraderPortfolio(ITrader trader) {
        final TraderPortfolio traderPortfolio = TraderPortfolio.buildPortfolio(trader);
        if (traderPortfolio.get().size() > 1) {
            List<Stock> stocks = stockRepo.findAllById(traderPortfolio.get().keySet());
            List<StockHistory> recentPrices = stocks.stream().map(s -> stockHistoryRepo.findFirst1ByStockOrderByTimeDesc(s).get())
                    .filter(Objects::nonNull).collect(Collectors.toList());
            traderPortfolio.get().forEach((s, portfolioInfo) -> {
                if (!s.equals("funds")) {
                    portfolioInfo.setPrice(recentPrices.stream().filter(price -> price.getStock().getSymbol().equals(s)).findFirst().get().getPrice());
                }
            });
        }
        return traderPortfolio;
    }
}