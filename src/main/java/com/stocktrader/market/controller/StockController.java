package com.stocktrader.market.controller;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.model.ref.ReportFormat;
import com.stocktrader.market.service.ReportService;
import com.stocktrader.market.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping("stock")
//@CrossOrigin(origins = "http://localhost:3000")
public class StockController {

    @Autowired
    StockService stockService;
    @Autowired
    PagedResourcesAssembler<StockResponse> assembler;
    private Logger logger = LoggerFactory.getLogger(StockController.class);
    private ReportService reportService;

    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<StockResponse>>> getStocksCurrentPrice(@RequestParam(defaultValue = "20") Integer size, @RequestParam(defaultValue = "0") Integer page) {

        logger.info("Requested Stock page ( size: {}, page: {} )", size, page);
        Page<StockResponse> stockPage = stockService.getStocksCurrentPrice(PageRequest.of(page, size));
        PagedModel<EntityModel<StockResponse>> ret = assembler.toModel(stockPage, linkTo(methodOn(this.getClass()).getStocksCurrentPrice(size, page)).withSelfRel());

        logger.info("Returning Stock page");
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/report")
    public HttpEntity<String> getStocksCurrentPriceReport(@RequestParam(defaultValue = "20") Integer size, @RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam ReportFormat reportFormat, HttpServletRequest request) throws Exception {
        Page<StockResponse> stockPage = stockService.getStocksCurrentPrice(PageRequest.of(page, size));
        TraderDao trader = (TraderDao) request.getAttribute(TRADER_SESSION_ATTRIBUTE);

        ResponseEntity<String> ret;
        reportService.sendReport(stockPage, reportFormat, trader);
        ret = new ResponseEntity<>(HttpStatus.ACCEPTED);
        return ret;
    }

    @GetMapping("/{symbol}")
    public HttpEntity<StockResponse> getStock(@PathVariable String symbol) {
        logger.info("Requested Specific Stock page ( {} )", symbol);
        final StockResponse stockCurrentDetails = stockService.getStockCurrentDetails(symbol);
        if (stockCurrentDetails != null) {
            logger.info("Returning Stock");
            return new ResponseEntity<>(stockCurrentDetails, HttpStatus.OK);
        } else {
            logger.info("Stock not Found ( {} )", symbol);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
