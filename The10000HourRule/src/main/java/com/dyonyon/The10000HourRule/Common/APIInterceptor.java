package com.dyonyon.The10000HourRule.Common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Slf4j
public class APIInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // LOG INSERT
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = null;
        String line = "";
        log.info("In preHandle : "+request.getRequestURL());

//        InputStream inputStream = request.getInputStream();
//        if(inputStream!=null){
//            br = new BufferedReader(new InputStreamReader(inputStream));
//            while((line = br.readLine())!=null){
//                stringBuilder.append(line);
//            }
//        } else{
//            log.info("Data 없음");
//        }
        log.info("In preHandle : "+request.requestData);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // LOG 결과 상태 및 END TIME UPDATE
//        log.info("In postHandle : "+response.getHeaderNames());
//        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
//        String responseBody = new String(responseWrapper.getContentAsByteArray());
//        log.info("In postHandle : "+responseBody);
    }
}
