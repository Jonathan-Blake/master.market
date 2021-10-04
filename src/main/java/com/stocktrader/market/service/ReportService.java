package com.stocktrader.market.service;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.ref.ReportFormat;
import com.stocktrader.market.util.AutoDeletingFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;


@EnableAsync
@Service
public class ReportService {

    @Autowired
    EmailService emailService;

    @Async
    public void sendReport(Page<StockResponse> pagedData, ReportFormat reportFormat, TraderDao recipient) throws IOException, MessagingException {
        String emailAddress = recipient.getId();
        try (AutoDeletingFile file = reportFormat.getConvertor().convertJSON(pagedData, pagedData.getClass())) {
            emailService.sendReport(file, emailAddress);
        }
    }
}
