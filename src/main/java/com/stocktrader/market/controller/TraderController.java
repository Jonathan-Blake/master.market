package com.stocktrader.market.controller;

import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping("trader")
@CrossOrigin(origins = "http://localhost:3000")
public class TraderController {

    private TraderDao trader;
    private Logger logger = LoggerFactory.getLogger(TraderController.class);
    @Autowired
    StockHistoryRepo stockHistoryRepo;
    @Autowired
    StockRepo stockRepo;

    @ModelAttribute
    void getTrader(HttpServletRequest request) {
        trader = (TraderDao) request.getAttribute(TRADER_SESSION_ATTRIBUTE);
    }


    @GetMapping("/portfolio")
    public HttpEntity<TraderPortfolio> getPortfolio() {
        logger.info("Retrieving Portfolio : ( {} )", trader.getId());
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
        traderPortfolio.add(
                linkTo(methodOn(this.getClass()).getPortfolio()).withSelfRel()
        );
        return new ResponseEntity<>(traderPortfolio, HttpStatus.OK);
    }

}
