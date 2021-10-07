package com.stocktrader.market.model.ref;

import com.stocktrader.market.service.report.convertor.CSVConvertor;
import com.stocktrader.market.service.report.convertor.IReportConvertor;
import com.stocktrader.market.service.report.fixer.CSVFixer;
import com.stocktrader.market.service.report.fixer.IReportFixer;
import com.stocktrader.market.util.AutoDeletingFile;

import java.io.IOException;

public enum ReportFormat {
    CSV(new CSVFixer(), new CSVConvertor());

    private final IReportConvertor reportConvertor;
    private final IReportFixer reportFixer;

    ReportFormat(IReportFixer reportFixer, IReportConvertor reportConvertor) {
        this.reportFixer = reportFixer;
        this.reportConvertor = reportConvertor;
    }

    public IReportFixer getFixer() {
        return this.reportFixer;
    }

    public IReportConvertor getConvertor() {
        return this.reportConvertor;
    }

    public AutoDeletingFile processJSON(Object json, Class schema) throws IOException {
        return getConvertor().convertJSON(getFixer().fix(json), schema);
    }
}
