package com.stocktrader.market.controller;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.model.dao.Transaction;
import com.stocktrader.market.model.dto.TraderPortfolio;
import com.stocktrader.market.model.dto.TraderRest;
import com.stocktrader.market.model.dto.TransactionResponse;
import com.stocktrader.market.repo.TransactionRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.stocktrader.market.filters.TraderFilter.TRADER_SESSION_ATTRIBUTE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping("trader")
@CrossOrigin(origins = "http://localhost:3000")
public class TraderController {

    @Autowired
    TransactionRepo transactionRepo;
    private TraderDao trader;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private PagedResourcesAssembler<Transaction> pagedResourcesAssembler;

    @ModelAttribute
    void getTrader(HttpServletRequest request) {
        trader = (TraderDao) request.getAttribute(TRADER_SESSION_ATTRIBUTE);
    }

    @GetMapping()
    public HttpEntity<TraderRest> getDetails() {
        System.out.println("Fetching Details");
        final TraderRest traderRest = mapper.map(trader, TraderRest.class);
        traderRest.add(
                linkTo(methodOn(this.getClass()).getDetails()).withSelfRel(),
                linkTo(methodOn(this.getClass()).getPagedHistory(0, 20)).withRel(LinkRelation.of("trade-history")),
                linkTo(methodOn(this.getClass()).getPortfolio(null)).withRel(LinkRelation.of("portfolio"))
        );
        return new ResponseEntity<>(traderRest, HttpStatus.OK);
    }

    @GetMapping("/portfolio")
    public HttpEntity<TraderPortfolio> getPortfolio(@AuthenticationPrincipal OAuth2User user) {
        final TraderPortfolio traderPortfolio = TraderPortfolio.buildPortfolio(trader);
        traderPortfolio.add(
                linkTo(methodOn(this.getClass()).getPortfolio(null)).withSelfRel(),
                linkTo(methodOn(this.getClass()).getDetails()).withRel(LinkRelation.of("trader"))
        );
        return new ResponseEntity<>(traderPortfolio, HttpStatus.OK);
    }

    @GetMapping("/trade-history")
    public HttpEntity<PagedModel<TransactionResponse>> getPagedHistory(@RequestParam @Min(0) int page, @RequestParam @Min(0) @Max(50) int size) {
        RepresentationModelAssembler<Transaction, TransactionResponse> assembler = entity -> mapper.map(entity, TransactionResponse.class);
        PagedModel<TransactionResponse> ret = pagedResourcesAssembler.toModel(transactionRepo.findAll(PageRequest.of(page, size, Sort.by("time").descending())), assembler);
        ret.add(linkTo(methodOn(this.getClass()).getDetails()).withRel(LinkRelation.of("trader")));
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

}
