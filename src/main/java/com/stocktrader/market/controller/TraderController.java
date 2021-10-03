package com.stocktrader.market.controller;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.repo.TransactionRepo;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping("trader")
@CrossOrigin(origins = "http://localhost:3000")
public class TraderController {

    @Autowired
    TransactionRepo transactionRepo;
    private TraderDao trader;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private PagedResourcesAssembler<Transaction> pagedResourcesAssembler;
    private Logger logger = LoggerFactory.getLogger(TraderController.class);

    @ModelAttribute
    void getTrader(HttpServletRequest request) {
        trader = (TraderDao) request.getAttribute(TRADER_SESSION_ATTRIBUTE);
    }


    @GetMapping("/portfolio")
    public HttpEntity<TraderPortfolio> getPortfolio() {
        logger.info("Retrieving Portfolio : ( {} )", trader.getId());
        final TraderPortfolio traderPortfolio = TraderPortfolio.buildPortfolio(trader);
        traderPortfolio.add(
                linkTo(methodOn(this.getClass()).getPortfolio()).withSelfRel()
        );
        return new ResponseEntity<>(traderPortfolio, HttpStatus.OK);
    }

}
