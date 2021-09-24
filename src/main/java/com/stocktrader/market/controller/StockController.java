package com.stocktrader.market.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
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
    public HttpEntity<PagedModel<EntityModel<StockResponse>>> getStocksCurrentPrice(@RequestParam(defaultValue = "20") Integer size, @RequestParam(defaultValue = "0") Integer page, @AuthenticationPrincipal OAuth2User user) {
        var mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString("Auth Principal " + user));
            System.out.println(mapper.writeValueAsString("Trader "));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("Getting Stocks " + size + "  " + page);
        Page<StockResponse> stockPage = stockService.getStocksCurrentPrice(PageRequest.of(page, size));
        System.out.println("Building Model");
        PagedModel<EntityModel<StockResponse>> ret = assembler.toModel(stockPage, linkTo(methodOn(this.getClass()).getStocksCurrentPrice(size, page, user)).withSelfRel());
        System.out.println("Returning Stocks " + Arrays.toString(ret.getContent().toArray()));

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/{symbol}")
    public HttpEntity<StockResponse> getStock(@PathVariable String symbol) {
        final StockResponse stockCurrentDetails = stockService.getStockCurrentDetails(symbol);
        var mapper = new ObjectMapper();
        try {
            System.out.println("Returning stock details: " + mapper.writeValueAsString(stockCurrentDetails));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(stockCurrentDetails, HttpStatus.OK);
    }

    @GetMapping("/history")
    public HttpEntity<PagedModel<StockResponse>> getStockHistories(@RequestParam(defaultValue = "20") Integer size, @RequestParam(defaultValue = "0") Integer page,
                                                                   @RequestParam() Instant startDate, @RequestParam() Instant endDate) {
        return new ResponseEntity(
                assembler.toModel(
                        stockService.getPriceHistoryOfStocks(PageRequest.of(page, size), startDate, endDate),
                        linkTo(methodOn(this.getClass()).getStockHistories(size, page, startDate, endDate)).withSelfRel()
                ),
                HttpStatus.OK
        );
    }
}
