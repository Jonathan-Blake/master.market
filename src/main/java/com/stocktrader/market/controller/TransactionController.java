package com.stocktrader.market.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.TransactionRequest;
import com.stocktrader.market.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;

@Controller
@RequestMapping("/transaction")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    @Autowired
    TransactionService transactionService;
    private TraderDao trader;

    @ModelAttribute
    void getTrader(HttpServletRequest request) {
        trader = (TraderDao) request.getAttribute(TRADER_SESSION_ATTRIBUTE);
    }

    @PostMapping
    public HttpEntity<String> transaction(@RequestBody @Validated TransactionRequest transactionRequest) {
        ResponseEntity<String> ret;
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValueAsString(transactionRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (transactionService.handleTransaction(transactionRequest, trader)) {
            ret = new ResponseEntity<>("Transaction Successfully Completed", HttpStatus.OK);
        } else {
            ret = new ResponseEntity<>("Transaction Failed", HttpStatus.BAD_REQUEST);
        }
        return ret;
    }
}
