package com.stocktrader.market.controller;

import com.stocktrader.market.model.dto.StockResponse;
import com.stocktrader.market.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    private static final Integer SIZE = 5;
    private static final Integer PAGE = 0;
    private static final String SYMBOL = "STOCK";
    private static final Instant INSTANT = Instant.now();

    @InjectMocks
    private StockController stockController;
    @Mock
    private StockService stockService;
    @Mock
    private PagedResourcesAssembler pagedResourcesAssembler;
    @Mock
    private PagedModel<EntityModel<StockResponse>> mockPagedModel;
    @Mock
    private PagedModel<StockResponse> mockPagedModelB;
    @Mock
    private StockResponse mockStock;
    @Mock
    private Page<StockResponse> mockPage;

    @Test
    void getStocksCurrentPrice_ReturnsPageOfValues() {
        when(stockService.getStocksCurrentPrice(any(PageRequest.class))).thenReturn(mockPage);
        when(pagedResourcesAssembler.toModel(any(Page.class), any(Link.class))).thenReturn(mockPagedModel);

        HttpEntity<PagedModel<EntityModel<StockResponse>>> result = stockController.getStocksCurrentPrice(SIZE, PAGE);

        assertEquals(mockPagedModel, result.getBody());
        assertEquals(HttpStatus.OK, ((ResponseEntity) result).getStatusCode());
        verify(stockService).getStocksCurrentPrice(PageRequest.of(PAGE, SIZE));
        verify(pagedResourcesAssembler).toModel(mockPage, linkTo(methodOn(stockController.getClass()).getStocksCurrentPrice(SIZE, PAGE)).withSelfRel());
    }

    @Test
    void getStock_ReturnsPage() {
        when(stockService.getStockCurrentDetails(anyString())).thenReturn(mockStock);

        HttpEntity<StockResponse> result = stockController.getStock(SYMBOL);

        assertEquals(mockStock, result.getBody());
        assertEquals(HttpStatus.OK, ((ResponseEntity) result).getStatusCode());
        verify(stockService).getStockCurrentDetails(SYMBOL);
    }

    @Test
    void getStock_ReturnsNotFound() {
        when(stockService.getStockCurrentDetails(anyString())).thenReturn(null);

        HttpEntity<StockResponse> result = stockController.getStock(SYMBOL);

        assertNull(result.getBody());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseEntity) result).getStatusCode());
        verify(stockService).getStockCurrentDetails(SYMBOL);
    }

    @Test
    void getStockHistories() {
        when(pagedResourcesAssembler.toModel(any(Page.class), any(Link.class))).thenReturn(mockPagedModelB);
        when(stockService.getPriceHistoryOfStocks(any(PageRequest.class), any(Instant.class), any(Instant.class))).thenReturn(mockPage);

        HttpEntity<PagedModel<StockResponse>> result = stockController.getStockHistories(SIZE, PAGE, INSTANT, INSTANT);

        assertEquals(mockPagedModelB, result.getBody());
        assertEquals(HttpStatus.OK, ((ResponseEntity) result).getStatusCode());
        verify(stockService).getPriceHistoryOfStocks(PageRequest.of(PAGE, SIZE), INSTANT, INSTANT);
        verify(pagedResourcesAssembler).toModel(mockPage, linkTo(methodOn(stockController.getClass()).getStockHistories(SIZE, PAGE, INSTANT, INSTANT)).withSelfRel());
    }
}