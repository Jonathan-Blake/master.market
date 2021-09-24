package com.stocktrader.market.filters;

import com.stocktrader.market.model.dao.TraderDao;
import com.stocktrader.market.repo.TraderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

@Component
@Order(1)
/*
    Searches trader in database and stores in request.
    Creates new trader if one does not match existing authenticated user.
 */
public class TraderFilter implements Filter {

    public static final String TRADER_SESSION_ATTRIBUTE = "MARKET.SESSION.TRADER";
    @Autowired
    TraderRepo repo;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<TraderDao> trader;
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            System.out.println(authentication.getName() + " is authenticated");
            trader = repo.findById(authentication.getName());
            if (trader.isEmpty()) {
                System.out.println(authentication.getName() + " creating new trader record");
                trader = Optional.of(repo.save(new TraderDao(authentication.getName(), BigInteger.valueOf(1000000L))));
            }
        } else {
            trader = Optional.empty();
        }
        request.setAttribute(TRADER_SESSION_ATTRIBUTE, trader.orElse(null));
        chain.doFilter(request, response);
    }
}
