package com.dyonyon.The10000HourRule.Common;

import com.dyonyon.The10000HourRule.domain.UserAuthInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class APIInterceptor implements HandlerInterceptor {

    private boolean isAPICall(String url){
        if(url.contains("/api/user")){
            log.info("[Interceptor] /api/user Logging 처리");
        } else if(url.contains("/api/memo")){
            log.info("[Interceptor] /api/memo Logging 처리");
        } else if(url.contains("/api/calender")){
            log.info("[Interceptor] /api/calender Logging 처리");
        } else if(url.contains("/api/routine")){
            log.info("[Interceptor] /api/routine Logging 처리");
        } else if(url.contains("/api/group")){
            log.info("[Interceptor] /api/routine Logging 처리");
        } else if(url.contains("/api/etc")){
            log.info("[Interceptor] /api/etc Logging 처리");
        } else if(url.contains("error")){
            log.info("[Interceptor] error Logging 처리");
            return false;
        } else {
            log.info("[Interceptor] else Logging 처리");
            return false;
        }
        return true;
    }
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        // LOGGING
        String url = String.valueOf(req.getRequestURL());
        if(isAPICall(url)){
            log.info("[Interceptor-PreHandle][Request] : ID {}",req.getAttribute("req_id"));
            log.info("[Interceptor-PreHandle][Request] : URL {}",url);
            ServletInputStream inputStream = req.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("[Interceptor-PreHandle][Request] : BODY {}", messageBody);

            // LOG INSERT
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
        String url = String.valueOf(req.getRequestURL());
        if(isAPICall(url)) {
            // LOG 결과 상태 및 END TIME UPDATE
            ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) res;
            String responseBody = new String(responseWrapper.getContentAsByteArray());
            log.info("[Interceptor-AfterCompletion][Response] : STATUS {}", responseWrapper.getStatus());
            log.info("[Interceptor-AfterCompletion][Response] : BODY {}", responseBody);
        }
    }
}
