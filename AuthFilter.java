package com.hospital.controller;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Blocks every /api/* call except /api/login unless the front desk has signed in. */
@WebFilter("/api/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        boolean loginCall = req.getRequestURI().endsWith("/api/login");
        boolean signedIn = req.getSession(false) != null && req.getSession(false).getAttribute("user") != null;
        if (loginCall || signedIn) {
            chain.doFilter(request, response);
            return;
        }
        res.setStatus(401);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"Please sign in first.\"}");
    }
}
