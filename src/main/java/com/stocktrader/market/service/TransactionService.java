package com.stocktrader.market.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.dto.TransactionRequest;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import com.stocktrader.market.repo.TraderRepo;
import com.stocktrader.market.repo.TransactionRepo;
import com.stocktrader.market.service.exceptions.MissingStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Set;

@Service
public class TransactionService {

    @Autowired
    StockHistoryRepo stockHistoryRepo;
    @Autowired
    TraderRepo traderRepo;
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    StockService stockService;
    @Autowired
    Validator validator;
    @Autowired
    private StockRepo stockRepo;

    @Transactional(rollbackOn = {SQLException.class, ValidationException.class})
    public boolean handleTransaction(final TransactionRequest transactionRequest, final TraderDao user) {
        boolean ret = false;

        System.out.println("Transacting");
        final Transaction constructedTransaction;
        try {
            constructedTransaction = constructTransactionResults(transactionRequest, user);
        } catch (MissingStockException e) {
            System.out.println("Invalid Stock Request");
            return false;
        }
        final Set<ConstraintViolation<Transaction>> constraintViolationsTransaction = validator.validate(constructedTransaction);
        if (!constraintViolationsTransaction.isEmpty()) {
            System.out.println("Invalid Transaction");
            return false;
        }
        final TraderPortfolio traderPortfolio = TraderPortfolio.buildPortfolio(user);// User is not selling more than they own
        System.out.println("Validating");
        final Set<ConstraintViolation<TraderPortfolio>> validationErrors = validator.validate(traderPortfolio);

        if (validationErrors.isEmpty()) {
            System.out.println("Validated : " + transactionRequest.getStock());
            switch (transactionRequest.getTransactionType()) {
                case BUY -> {
                    final BigInteger currentlyTradeableStockQuantity = stockService.getCurrentlyTradeableStockQuantity(constructedTransaction.getStockTraded().getStock());
                    final BigInteger marketQuantityAfterTransaction = transactionRequest.getTransactionType().transact(
                            currentlyTradeableStockQuantity,
                            transactionRequest.getQuantity());
                    if (stockHasSufficientQuantity(
                            constructedTransaction.getStockTraded().getStock().getTotalQuantity(),
                            marketQuantityAfterTransaction)
                            && user.getFunds().compareTo(BigInteger.ZERO) > -1
                    ) {
                        transactionRepo.save(constructedTransaction);
                        traderRepo.save(user);
                        ret = true;
                    } else {
                        System.out.println("Cannot make transaction of requested quantity");
                    }
                }
                case SELL -> {
                    if (traderPortfolio.get().get(transactionRequest.getStock()).getQuantity().compareTo(BigInteger.ZERO) > -1) {
                        transactionRepo.save(constructedTransaction);
                        traderRepo.save(user);
                        ret = true;
                    } else {
                        System.out.println("User Does not own sufficient stocks ");
                    }
                }
                default -> System.out.println("Unrecognised Transaction Type, Implement " + transactionRequest.getTransactionType().name());
            }
        } else {
            System.out.println("Validation Failed");
        }
        return ret;
    }

    /*
    Cannot transact negative quantites and transaction cannot exceed total available in market.
     */
    private boolean stockHasSufficientQuantity(final BigInteger totalStock, final BigInteger quantityAfterTransaction) {
        final boolean b = 0 <= quantityAfterTransaction.compareTo(BigInteger.ZERO) && totalStock.compareTo(quantityAfterTransaction) >= 0;
        System.out.println("Sufficient Quantity : " + b);
        return b;
    }

    private @Validated
    Transaction constructTransactionResults(final TransactionRequest transactionRequest, final TraderDao user) throws MissingStockException {
        final Transaction newTrade = new Transaction();
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println("transactionRequest : " + mapper.writeValueAsString(transactionRequest));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        newTrade.setTransactionType(transactionRequest.getTransactionType());
        newTrade.setQuantity(transactionRequest.getQuantity());
        System.out.println("Retrieving Stock");
        stockRepo.findById(transactionRequest.getStock())
                .ifPresentOrElse(
                        stock1 -> newTrade.setStockTraded(
                                stockHistoryRepo.findFirst1ByStockOrderByTime(stock1)
                                        .orElseThrow(() -> new MissingStockException(transactionRequest.getStock()))
                        ),
                        () -> {
                            throw new MissingStockException(transactionRequest.getStock());
                        }
                );

        newTrade.setTime(Instant.now());
        System.out.println("Adding Trade");
        user.addNewTrade(newTrade);
        return newTrade;
    }
}
