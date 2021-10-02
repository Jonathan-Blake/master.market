package com.stocktrader.market.controller;

import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.service.StockService;
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

import java.util.Arrays;

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

    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<StockResponse>>> getStocksCurrentPrice(@RequestParam(defaultValue = "20") Integer size, @RequestParam(defaultValue = "0") Integer page) {

        System.out.println("Getting Stocks " + size + "  " + page);
        Page<StockResponse> stockPage = stockService.getStocksCurrentPrice(PageRequest.of(page, size));
        System.out.println("Building Model");
        PagedModel<EntityModel<StockResponse>> ret = assembler.toModel(stockPage, linkTo(methodOn(this.getClass()).getStocksCurrentPrice(size, page)).withSelfRel());
        System.out.println("Returning Stocks " + Arrays.toString(ret.getContent().toArray()));

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/{symbol}")
    public HttpEntity<StockResponse> getStock(@PathVariable String symbol) {
        final StockResponse stockCurrentDetails = stockService.getStockCurrentDetails(symbol);
        if (stockCurrentDetails != null) {
            return new ResponseEntity<>(stockCurrentDetails, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
