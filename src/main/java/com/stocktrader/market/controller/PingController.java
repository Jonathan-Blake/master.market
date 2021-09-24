package com.stocktrader.market.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class PingController {

    @RequestMapping("ping")
    public HttpEntity<String> ping() {
        System.out.println("Pinged");
        return new ResponseEntity<>("Ping", HttpStatus.OK);
    }
}
