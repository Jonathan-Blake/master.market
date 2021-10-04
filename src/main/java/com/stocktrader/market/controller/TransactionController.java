package com.stocktrader.market.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.ErrorResponse;
import com.stocktrader.market.model.dto.TransactionRequest;
import com.stocktrader.market.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;

@Controller
@RequestMapping("/transaction")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    @Autowired
    TransactionService transactionService;
    @Autowired
    MessageSource messageSource;
    private Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private TraderDao trader;

    @ModelAttribute
    void getTrader(HttpServletRequest request) {
        trader = (TraderDao) request.getAttribute(TRADER_SESSION_ATTRIBUTE);
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('transactions')")
    public HttpEntity<String> transaction(@RequestBody @Validated TransactionRequest transactionRequest) {
        // This has been removed as we were unable to get the scopes working on the front end
        // the commented code was left as an example of how it would be done.
//        Authentication user = SecurityContextHolder.getContext().getAuthentication();
//        if(! user.getAuthorities().contains("transactions")){
//            return new ResponseEntity<>("Missing required scopes", HttpStatus.UNAUTHORIZED);
//        }
        ResponseEntity<String> ret;
        logger.info("Transaction Request : ( {} )", trader.getId());
        if (transactionService.handleTransaction(transactionRequest, trader)) {
            ret = new ResponseEntity<>("Transaction Successfully Completed", HttpStatus.OK);
        } else {
            ret = new ResponseEntity<>("Transaction Failed", HttpStatus.BAD_REQUEST);
        }
        return ret;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, InvalidFormatException.class})
    public HttpEntity<ErrorResponse> constraintViolation(HttpServletRequest req, Exception e) {
        logger.error("Transaction Request with Invalid Parameters: ( {} )", e.getMessage());
        ErrorResponse ret = new ErrorResponse();
        ret.setTimestamp(Instant.now());
        ret.setError("Constraint Violation Exception");
        ret.setMessage("Failed to validate TransactionRequest");
        return new ResponseEntity<>(ret, HttpStatus.BAD_REQUEST);
    }
}
