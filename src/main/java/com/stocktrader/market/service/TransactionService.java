package com.stocktrader.market.service;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.dto.TransactionRequest;
import com.stocktrader.market.repo.StockHistoryRepo;
import com.stocktrader.market.repo.StockRepo;
import com.stocktrader.market.repo.TraderRepo;
import com.stocktrader.market.repo.TransactionRepo;
import com.stocktrader.market.service.exceptions.MissingStockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Arrays;
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
    private Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Transactional(rollbackOn = {SQLException.class, ValidationException.class})
    public boolean handleTransaction(final TransactionRequest transactionRequest, final TraderDao user) {
        boolean ret = false;

        logger.info("Beginning Transaction : ( {}, {} {} {} )", user.getId(), transactionRequest.getTransactionType(), transactionRequest.getQuantity(), transactionRequest.getStock());
        final Transaction constructedTransaction;
        try {
            constructedTransaction = constructTransactionResults(transactionRequest, user);
        } catch (MissingStockException e) {
            logger.info("Transaction Failed : ( {} Invalid Request: Stock not found}", user.getId());
            return false;
        }
        final Set<ConstraintViolation<Transaction>> constraintViolationsTransaction = validator.validate(constructedTransaction);
        if (!constraintViolationsTransaction.isEmpty()) {
            logger.info("Transaction Failed : ( {} Invalid Request: Constraint violations: {} )", user.getId(), Arrays.toString(constraintViolationsTransaction.toArray()));
            return false;
        }
        final TraderPortfolio traderPortfolio = TraderPortfolio.buildPortfolio(user);// User is not selling more than they own
        logger.info("Validating Portfolio Post transaction : ( {}: {} )", user.getId(), traderPortfolio.get().get(transactionRequest.getStock()).getQuantity());
        final Set<ConstraintViolation<TraderPortfolio>> validationErrors = validator.validate(traderPortfolio);

        if (validationErrors.isEmpty()) {
            logger.info("Successfully Validated Portfolio : ( {} )", user.getId());
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
                        logger.info("Transaction Successfully Completed: ( {} )", user.getId());
                    } else {
                        logger.info("Transaction Failed : ( {} Invalid Request: Could not purchase quantity: {} quantity available {} )", user.getId(), transactionRequest.getQuantity(), currentlyTradeableStockQuantity);
                    }
                }
                case SELL -> {
                    if (traderPortfolio.get().get(transactionRequest.getStock()).getQuantity().compareTo(BigInteger.ZERO) > -1) {
                        transactionRepo.save(constructedTransaction);
                        traderRepo.save(user);
                        ret = true;
                        logger.info("Transaction Successfully Completed: ( {} )", user.getId());
                    } else {
                        logger.info("Transaction Failed : { {} Invalid Request: Could sell quantity: {} user owns {} }", user.getId(), transactionRequest.getQuantity(), traderPortfolio.get().get(transactionRequest.getStock()).getQuantity().add(transactionRequest.getQuantity()));
                    }
                }
                default -> logger.info("Transaction Failed : ( {} Invalid Request: Did not recognise transaction Type: {} )", user.getId(), transactionRequest.getTransactionType().name());
            }
        } else {
            logger.info("Transaction Failed : ( {} Invalid Portfolio: Constraint violations: {} )", user.getId(), Arrays.toString(validationErrors.toArray()));
        }
        return ret;
    }

    /*
    Cannot transact negative quantites and transaction cannot exceed total available in market.
     */
    private boolean stockHasSufficientQuantity(final BigInteger totalStock, final BigInteger quantityAfterTransaction) {
        return 0 <= quantityAfterTransaction.compareTo(BigInteger.ZERO) && totalStock.compareTo(quantityAfterTransaction) >= 0;
    }

    private @Validated
    Transaction constructTransactionResults(final TransactionRequest transactionRequest, final TraderDao user) throws MissingStockException {
        final Transaction newTrade = new Transaction();
        newTrade.setTransactionType(transactionRequest.getTransactionType());
        newTrade.setQuantity(transactionRequest.getQuantity());
        logger.info("Retrieving Requested Stock : ( {}, {} )", user.getId(), transactionRequest.getStock());
        stockRepo.findById(transactionRequest.getStock())
                .ifPresentOrElse(
                        stock1 -> newTrade.setStockTraded(
                                stockHistoryRepo.findFirst1ByStockOrderByTimeDesc(stock1)
                                        .orElseThrow(() -> new MissingStockException(transactionRequest.getStock()))
                        ),
                        () -> {
                            throw new MissingStockException(transactionRequest.getStock());
                        }
                );
        newTrade.setTime(Instant.now());
        logger.info("Adding Stock to Transaction : ( {} )", user.getId());
        user.addNewTrade(newTrade);
        return newTrade;
    }
}
