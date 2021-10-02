package com.stocktrader.market.controller;

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
//    @PreAuthorize("hasAuthority('transactions')")
    public HttpEntity<String> transaction(@RequestBody @Validated TransactionRequest transactionRequest) {
        ResponseEntity<String> ret;
        // This has been removed as we were unable to get the scopes working on the front end
        // the commented code was left as an example of how it would be done.
//        Authentication user = SecurityContextHolder.getContext().getAuthentication();
//        if(! user.getAuthorities().contains("transactions")){
//            return new ResponseEntity<>("Missing required scopes", HttpStatus.UNAUTHORIZED);
//        }
        if (transactionService.handleTransaction(transactionRequest, trader)) {
            ret = new ResponseEntity<>("Transaction Successfully Completed", HttpStatus.OK);
        } else {
            ret = new ResponseEntity<>("Transaction Failed", HttpStatus.BAD_REQUEST);
        }
//        }
        return ret;

    }
}
