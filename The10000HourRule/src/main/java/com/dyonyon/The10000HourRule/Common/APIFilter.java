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
import java.text.SimpleDateFormat;
import java.util.Date;

@WebFilter(urlPatterns = "/api/*")
@Slf4j
public class APIFilter implements Filter {
    private int isAPICall(String url){
        if(url.contains("/api/user")){
            log.info("[Filter][API] USER CALL : {} 처리",url);
            return 1;
        } else if(url.contains("/api/memo")){
            log.info("[Filter][API] MEMO CALL : {} 처리",url);
            return 2;
        } else if(url.contains("/api/calender")){
            log.info("[Filter][API] CALENDER CALL : {} 처리",url);
            return 3;
        } else if(url.contains("/api/routine")){
            log.info("[Filter][API] ROUTINE CALL : {} 처리",url);
            return 4;
        } else if(url.contains("/api/group")){
            log.info("[Filter][API] GROUP CALL : {} 처리",url);
            return 5;
        } else if(url.contains("/api/etc")){
            log.info("[Filter][API] ETC CALL : {} 처리",url);
            return 6;
        } else if(url.contains("error")){
            log.info("[Filter][API] ERROR CALL : {} 처리",url);
            return -1;
        } else {
            log.info("[Filter][API] ELSE CALL : {} 처리",url);
            return -2;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String url = String.valueOf(((HttpServletRequest)request).getRequestURL());
            int result = isAPICall(url);
            if(result>0){
                RequestBodyWrapper reqWrapper = new RequestBodyWrapper((HttpServletRequest) request);
                SimpleDateFormat now = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String id = now.format(new Date());
                reqWrapper.setAttribute("req_id",id);
                log.info("[Filter][Request] : ID {}", reqWrapper.getAttribute("req_id"));
                log.info("[Filter][Request] : URL {}", reqWrapper.getRequestURL());
                log.info("[Filter][Request] : BODY DATA {}", reqWrapper.getRequestData());
                ContentCachingResponseWrapper resCaching = new ContentCachingResponseWrapper((HttpServletResponse) response);

                chain.doFilter(reqWrapper, resCaching);

                String responseBody = new String(resCaching.getContentAsByteArray());
                log.info("[Filter][Response] : RESPONSE {}", responseBody);
                resCaching.copyBodyToResponse();
            } else {
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            log.info("[Filter][ERROR] : ERROR {}", (Object) e.getStackTrace());
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
