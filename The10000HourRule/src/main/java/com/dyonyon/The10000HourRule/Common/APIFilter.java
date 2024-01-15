package com.dyonyon.The10000HourRule.Common;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@WebFilter(urlPatterns = "/api/*")
@Slf4j
public class APIFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest requestWrapper = new RequestBodyWrapper(request);
//        HttpServletResponse responseWrapper = new ResponseBodyWrapper(response);
        filterChain.doFilter(requestWrapper, response);
//        filterChain.doFilter(request, response);
    }
}
