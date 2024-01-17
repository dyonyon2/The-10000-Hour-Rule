package com.dyonyon.The10000HourRule.Common;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@WebFilter(urlPatterns = "/api/*")
@Slf4j
public class APIFilter implements Filter {

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        RequestBodyWrapper requestWrapper = new RequestBodyWrapper(request);
//        log.info("In filter : "+requestWrapper.getRequestData());
//        requestWrapper.setAttribute("requestBody", requestWrapper.getRequestData());
////        HttpServletResponse responseWrapper = new ResponseBodyWrapper(response);
//        filterChain.doFilter(requestWrapper, response);
////        filterChain.doFilter(request, response);
//    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            RequestBodyWrapper reqWrapper = new RequestBodyWrapper((HttpServletRequest)request);
            log.info("[Filter][Request] : URL {}",reqWrapper.getRequestURL());
            log.info("[Filter][Request] : BODY DATA {}",reqWrapper.getRequestData());
            ContentCachingResponseWrapper resCaching = new ContentCachingResponseWrapper((HttpServletResponse) response);

            // SetAttribute로 추가로 넣어주는 방법!
//            wrapper.setAttribute("requestBody", wrapper.getRequestData());
//            log.info("In filter : res {}",resWrapper.getResponseData());

            chain.doFilter(reqWrapper, resCaching);

            String responseBody = new String(resCaching.getContentAsByteArray());
            log.info("[Filter][Response] : RESPONSE {}",responseBody);
            resCaching.copyBodyToResponse();

        } catch (Exception e) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
