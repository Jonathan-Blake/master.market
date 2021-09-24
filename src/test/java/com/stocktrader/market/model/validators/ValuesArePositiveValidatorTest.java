package com.stocktrader.market.model.validators;

import com.stocktrader.market.TestDataUtil;
import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.ref.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValuesArePositiveValidatorTest {

    private final TestDataUtil testDataUtil = new TestDataUtil();
    ValuesArePositiveValidator validator = new ValuesArePositiveValidator();
    @Mock
    private ConstraintValidatorContext mockContext;
    @Mock
    private StockHistory mockStockHistory;
    @Mock
    private Stock mockStock;

    @Test
    void validatesWithNoErrorsIfPositiveFundsOnly() {
        TraderPortfolio traderPortfolio = new TraderPortfolio(BigInteger.valueOf(1000L), null);
        assertTrue(validator.isValid(traderPortfolio.get(), mockContext));
//        fail();
    }

    @Test
    void validatesWithNoErrorsIfNegativeFundsOnly() {
        TraderPortfolio traderPortfolio = new TraderPortfolio(BigInteger.valueOf(-1000L), null);
        assertFalse(validator.isValid(traderPortfolio.get(), mockContext));
//        fail();
    }

    @Test
    void validatesWithNoErrorsIfSingleTradeIsPositive() {
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.valueOf(5));
        when(mockStock.getSymbol()).thenReturn("AAA");

        Transaction transaction = new Transaction();
        transaction.setQuantity(BigInteger.valueOf(10L));
        transaction.setStockTraded(mockStockHistory);
        transaction.setTransactionType(TransactionType.BUY);

        TraderPortfolio traderPortfolio = new TraderPortfolio(BigInteger.ZERO, List.of(transaction));

        assertTrue(validator.isValid(traderPortfolio.get(), mockContext));
//        fail();
    }

    @Test
    void validatesWithErrorsIfSingleTradeIsNegative() {
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.valueOf(5));
        when(mockStock.getSymbol()).thenReturn("AAA");

        Transaction transaction = new Transaction();
        transaction.setQuantity(BigInteger.valueOf(10L));
        transaction.setStockTraded(mockStockHistory);
        transaction.setTransactionType(TransactionType.SELL);

        TraderPortfolio traderPortfolio = new TraderPortfolio(BigInteger.ZERO, List.of(transaction));
        assertFalse(validator.isValid(traderPortfolio.get(), mockContext));
//        fail();
    }

    @Test
    void validatesTrue_ForManyTrades_AllPurchases() {
        TraderPortfolio traderPortfolio = new TraderPortfolio(BigInteger.ZERO, testDataUtil.generateTransactions(100, new TestDataUtil.GenerateTransactionParams(100, 1000, TransactionType.BUY)));
        assertTrue(validator.isValid(traderPortfolio.get(), mockContext));
//        fail();
    }

    @Test
    void validatesFalse_ForManyTrades_OneNegative() {
        when(mockStockHistory.getStock()).thenReturn(mockStock);
        when(mockStock.getSymbol()).thenReturn("TEST_NOT_VALID");
        when(mockStockHistory.getPrice()).thenReturn(BigInteger.valueOf(5));

        Transaction transaction = new Transaction();
        transaction.setQuantity(BigInteger.valueOf(10L));
        transaction.setStockTraded(mockStockHistory);
        transaction.setTransactionType(TransactionType.SELL);
        transaction.setTime(Instant.now());

        Transaction[] testArray = testDataUtil.generateTransactions(
                100,
                new TestDataUtil.GenerateTransactionParams(100, 1000, TransactionType.BUY)
        ).toArray(new Transaction[101]);
        testArray[100] = transaction;

        TraderPortfolio traderPortfolio = new TraderPortfolio(BigInteger.ZERO, Arrays.stream(testArray).collect(Collectors.toList()));

        assertFalse(validator.isValid(traderPortfolio.get(), mockContext));
//        fail();
    }
}