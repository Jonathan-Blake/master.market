package com.stocktrader.market.controller;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.ref.ReportFormat;
import com.stocktrader.market.service.PortfolioService;
import com.stocktrader.market.service.ReportService;
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

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
    PortfolioService portfolioService;
    @Autowired
    ReportService reportService;

    @ModelAttribute
    void getTrader(HttpServletRequest request) {
        trader = (TraderDao) request.getAttribute(TRADER_SESSION_ATTRIBUTE);
    }


    @GetMapping("/portfolio")
    public HttpEntity<TraderPortfolio> getPortfolio() {
        logger.info("Retrieving Portfolio : ( {} )", trader.getId());
        final TraderPortfolio traderPortfolio = portfolioService.getTraderPortfolio(trader);
        traderPortfolio.add(
                linkTo(methodOn(this.getClass()).getPortfolio()).withSelfRel()
        );
        return new ResponseEntity<>(traderPortfolio, HttpStatus.OK);
    }

    @GetMapping("/portfolio/report")
    public HttpEntity<String> getPortfolioReport() throws MessagingException, IOException {
        logger.info("Retrieving Portfolio Report : ( {} )", trader.getId());
        final TraderPortfolio traderPortfolio = portfolioService.getTraderPortfolio(trader);
        reportService.sendReport(traderPortfolio, ReportFormat.CSV, trader);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private TraderPortfolio getTraderPortfolio() {
        return portfolioService.getTraderPortfolio(trader);
    }

}
