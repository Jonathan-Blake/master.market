package com.stocktrader.market.service.report.convertor;

import com.stocktrader.market.util.AutoDeletingFile;

import java.io.IOException;

public interface IReportConvertor {

    AutoDeletingFile convertJSON(Object json, Class schema) throws IOException;
}
