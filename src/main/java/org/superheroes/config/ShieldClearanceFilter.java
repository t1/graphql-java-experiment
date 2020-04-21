package org.superheroes.config;

import lombok.extern.slf4j.Slf4j;
import org.superheroes.hero.ShieldClearance;
import org.superheroes.hero.ShieldClearance.Level;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.superheroes.hero.ShieldClearance.Level.PUBLIC;

@Slf4j
@WebFilter("/*")
public class ShieldClearanceFilter implements Filter {
    @Inject ShieldClearance shieldClearance;

    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // this is only a demo! yes, it's not secure to trust a client like this ;-) in a real project you'd use JWT or so
        String clearanceString = ((HttpServletRequest) request).getHeader("S.H.I.E.L.D.-Clearance");
        log.info("clearance header is {}", clearanceString);
        shieldClearance.setLevel((clearanceString == null) ? PUBLIC : Level.valueOf(clearanceString));

        chain.doFilter(request, response);
    }
}
