package com.stocktrader.market.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.ref.ReportFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;


@EnableAsync
@Service
public class ReportService {

    @Autowired
    EmailService emailService;

    @Async
    public void sendReport(Page<StockResponse> pagedData, ReportFormat reportFormat, TraderDao recipient) throws JsonProcessingException {
        String emailAddress = recipient.getId();
        File attachment;
        switch (reportFormat) {
            case CSV -> {
                CsvMapper mapper = new CsvMapper();
                CsvSchema schema = mapper.schemaFor(StockResponse.class)
                        .withHeader();
                String str = mapper.writerFor(StockResponse[].class).with(schema).writeValueAsString(pagedData.getContent().toArray(new StockResponse[]{}));
                System.out.println(str);
            }
        }

    }
}
