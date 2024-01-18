package com.dyonyon.The10000HourRule.Common;

import com.dyonyon.The10000HourRule.domain.APICallLogInfo;
import com.dyonyon.The10000HourRule.domain.UserAuthInfo;
import com.dyonyon.The10000HourRule.repository.LogRepository;
import com.dyonyon.The10000HourRule.repository.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Intercepts;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LogRepository logRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

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
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        try {
            // 1. 일단 정의된 TYPE의 URL인지 체크
            // 2. API TYPE 확인 (POST, GET)
            // 3. 로그 insert
            // 4. API 체크하여 로그인 필요 여부 판단
            // 4.1 로그인 필요시, 로그인 체크
            // 4.2 로그인 조건 불충분할 시 로그인 필요 경고 창 return & 로그 상태값 update

            String url = String.valueOf((req).getRequestURL());
            String method = req.getMethod();
            String reqId;
            String userId = null;
            String userIdx = null;
            String reqData = null;
            APICallLogInfo apiCallLogInfo;
            int result = isAPICall(url);
            int insertRes = -1;
            if (result > 0) {
                reqId = String.valueOf(req.getAttribute("req_id"));
                ServletInputStream inputStream = req.getInputStream();
                log.info("[Interceptor-PreHandle][Request] REQ_ID : {}", reqId);
                log.info("[Interceptor-PreHandle][Request] URL : {}", url);
                log.info("[Interceptor-PreHandle][Request] METHOD : {}", method);


                //POST, GET에 따라 USER_ID와 REQ_DATA 저장
                if ("GET".equals(method)) {
                    reqData = req.getQueryString();
                    userId = req.getParameter("user_id");
                } else {
                    reqData = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    APICallLogInfo tmp = objectMapper.readValue(reqData, APICallLogInfo.class);
                    userId = tmp.getUser_id();
                }
                reqData=reqData.replace("\"","");
                log.info("[Interceptor-PreHandle][Request] BODY : {}", reqData);
                log.info("[Interceptor-PreHandle][Request] USER_ID : {}", userId);
                if (userId != null) {
                    // USERIDX 찾는 쿼리
                    userIdx = logRepository.getUserIdx(userId);
                }
                log.info("[Interceptor-PreHandle][Request] USER_IDX : {}", userIdx);
                apiCallLogInfo = new APICallLogInfo(reqId, userId, userIdx, url, reqData);
                log.info("[Interceptor-PreHandle][Request] LOG INSERT : {}", apiCallLogInfo.toString());

                log.info("[Interceptor-PreHandle][Request] SWITCH : {}", result);
                // LOG INSERT
                switch (result) {
                    case 1: // /api/user
                        insertRes = logRepository.insertUserLog(apiCallLogInfo);
                        break;
                    case 2: // /api/memo
                        insertRes = logRepository.insertMemoLog(apiCallLogInfo);
                        break;
                    case 3: // /api/calender
                        insertRes = logRepository.insertCalenderLog(apiCallLogInfo);
                        break;
                    case 4: // /api/routine
                        insertRes = logRepository.insertRoutineLog(apiCallLogInfo);
                        break;
                    case 5: // /api/group
                        insertRes = logRepository.insertGroupLog(apiCallLogInfo);
                        break;
                    case 6: // /api/etc
                        insertRes = logRepository.insertEtcLog(apiCallLogInfo);
                        break;
                    default:
                        break;
                }
                if (insertRes != 1) {
                    log.info("[Interceptor-PreHandle][Request] : LOG INSERT SUCCESSED {}", insertRes);
                } else {
                    log.info("[Interceptor-PreHandle][Request] : LOG INSERT FAILED {}", insertRes);
                }

                return true;
            }
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
        String url = String.valueOf(req.getRequestURL());
        int result = isAPICall(url);
        if(result>0){
            // LOG 결과 상태 및 END TIME UPDATE
            ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) res;
            String responseBody = new String(responseWrapper.getContentAsByteArray());
            log.info("[Interceptor-AfterCompletion][Response] : STATUS {}", responseWrapper.getStatus());
            log.info("[Interceptor-AfterCompletion][Response] : BODY {}", responseBody);
        }
    }
}
