package com.stocktrader.market.service.report.fixer;

import com.stocktrader.market.model.csv.CSVPortfolioInfo;
import com.stocktrader.market.model.csv.CSVStockResponse;
import com.stocktrader.market.model.dto.PortfolioInfo;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.dto.TraderPortfolio;
import org.springframework.data.util.Streamable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CSVFixer implements IReportFixer {
    @Override
    public Object fix(Object object) {
        Object ret;
        if (object instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry) object;
            if (entry.getValue() instanceof PortfolioInfo) {
                ret = new CSVPortfolioInfo((String) entry.getKey(), (PortfolioInfo) entry.getValue());
            } else {
                ret = null;
            }
        } else if (object instanceof StockResponse) {
            ret = new CSVStockResponse((StockResponse) object);
        } else if (object instanceof TraderPortfolio) {
            ret = ((TraderPortfolio) object).get().entrySet().stream()
                    .map(this::fix)
                    .collect(Collectors.toList());
        } else if (object instanceof Collection) {
            ret = ((Collection<?>) object).stream()
                    .map(this::fix)
                    .collect(Collectors.toList());
        } else if (object.getClass().isArray()) {
            ret = Arrays.stream((Object[]) object)
                    .map(this::fix)
                    .collect(Collectors.toList());
        } else if (object instanceof Streamable) {
            ret = ((Streamable) object).stream()
                    .map(this::fix)
                    .collect(Collectors.toList());
        } else {
            ret = null;
        }
        return ret;
    }
}
