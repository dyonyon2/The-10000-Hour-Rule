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


    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        // LOG INSERT
//        log.info("test"+ req.getAttribute("requestBody"));
        log.info("[Interceptor-PreHandle][Request] : URL {}",req.getRequestURL());
        ServletInputStream inputStream = req.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.info("[Interceptor-PreHandle][Request] : BODY {}", messageBody);

//        String messageBody = StreamUtils.copyToString(((ContentCachingRequestWrapper)req).getInputStream(), StandardCharsets.UTF_8);
//        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) req;
//        log.info("In Intercept preHandle2 : "+requestWrapper.getRequestURL());
//        String info = new String (requestWrapper.getContentAsByteArray());
//        log.info("In Intercept preHandle2 : messageBody={}", info);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
        // LOG 결과 상태 및 END TIME UPDATE
//        log.info("[Interceptor-AfterCompletion][Request] : URL {}",req.getRequestURL());
//        ServletInputStream inputStream = req.getInputStream();
//        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//        log.info("[Interceptor-AfterCompletion][Request] : BODY {}", messageBody);

        ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) res;
        String responseBody = new String(responseWrapper.getContentAsByteArray());
        log.info("[Interceptor-AfterCompletion][Response] : STATUS {}",responseWrapper.getStatus());
        log.info("[Interceptor-AfterCompletion][Response] : BODY {}",responseBody);
    }

}
