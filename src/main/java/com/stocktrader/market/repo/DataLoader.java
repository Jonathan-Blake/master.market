package com.stocktrader.market.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrader.market.model.dao.Stock;
import com.stocktrader.market.model.dao.StockHistory;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.ref.OpenOrClose;
import com.stocktrader.market.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

@Component
public class DataLoader {
    private final Random rand = new Random();
    @Autowired
    TraderRepo traderRepo;
    @Autowired
    StockHistoryRepo stockHistoryRepo;
    @Autowired
    StockRepo stockRepo;
    @Autowired
    TransactionService transactionService;

    @PostConstruct
    @Transactional(rollbackOn = SQLException.class)
    void loadData() {
        String[] stocksToCreate = new String[]{"C"
                , "HSBC", "CS", "RBS", "CL", "TN", "AV", "MS",
                "L", "CAP", "CLNGF", "BNP", "WN", "QBE", "FT", "NI", "AS", "DC", "JPM", "LL", "HBOS",
                "N", "ING", "BNY", "SG", "LCH", "DA", "RCD", "FO", "BD", "SC", "SSB", "BTO", "LSE",
                "BOA", "TP", "NE", "FH", "TR", "CI", "TS", "WTG", "EY", "AE", "BY", "JAVA", "THSY"
        };
//         Rabobank RBS Duplicate Key need to change to look at name
        Arrays.stream(stocksToCreate).forEach(this::generateAndSaveRandomStockData);

        TraderDao t = new TraderDao("Test", BigInteger.valueOf(10000L));
        traderRepo.save(t);

        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(
                    stockHistoryRepo.findFirst1ByStockAndOpenClose_OpenCloseOrderByTime(
                            stockRepo.findById("HSBC").get(),
                            OpenOrClose.OPEN
                    ).get()
            ));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void generateAndSaveRandomStockData(String stockCode) {
        Stock s = new Stock(stockCode, BigInteger.valueOf(rand.longs(2000000L, 1000000000L).findFirst().orElse(2000000L)));
        StockHistory sh = new StockHistory();
        sh.stock = s;
        sh.price = BigInteger.valueOf(rand.nextInt(545000) + 500L);
        sh.time = Instant.now();
        sh.setOpenOrClosed(OpenOrClose.OPEN);

        stockRepo.save(s);
        stockHistoryRepo.save(sh);
    }

}
