package com.stocktrader.market.model.ref;

import com.stocktrader.market.service.reportconvertor.CSVConvertor;
import com.stocktrader.market.service.reportconvertor.IReportConvertor;

public enum ReportFormat {
    CSV(CSVConvertor.get());

    private final IReportConvertor reportConvertor;

    ReportFormat(IReportConvertor reportConvertor) {
        this.reportConvertor = reportConvertor;
    }

    public IReportConvertor getConvertor() {
        return this.reportConvertor;
    }
}
