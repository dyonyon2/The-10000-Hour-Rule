package com.dyonyon.The10000HourRule.Common;

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
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse response, Object handler) throws Exception {
        // LOG INSERT
//        log.info("test"+ req.getAttribute("requestBody"));
//        log.info("In Intercept preHandle : "+req.getRequestURL());
//        ServletInputStream inputStream = req.getInputStream();
//        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//        log.info("In Intercept preHandle : messageBody={}", messageBody);


        ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) req;
        log.info("In Intercept preHandle2 : "+cachingRequest.getRequestURL());
        String messageBody = new String(cachingRequest.getContentAsByteArray());
        log.info("In Intercept preHandle2 : messageBody={}", messageBody);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // LOG 결과 상태 및 END TIME UPDATE
        log.info("In postHandle 1");
        log.info("In postHandle : "+response.getStatus());
//        String responseBody = response.getOutputStream().toString();
//        log.info("In postHandle : responseBody={}",responseBody);

        ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) response;
        String responseBody = new String(responseWrapper.getContentAsByteArray());
        log.info("In postHandle : body {} ", responseBody);
//        // 클라이언트로 전달 전 실제 response 객체에 copy
//        responseWrapper.copyBodyToResponse();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("In afterCompletion 1");
    }

}
