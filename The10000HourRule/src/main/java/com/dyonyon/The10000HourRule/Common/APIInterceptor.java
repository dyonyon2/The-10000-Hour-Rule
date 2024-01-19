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
import org.springframework.stereotype.Component;
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
//    private APICallLogInfo apiCallLogInfo = new APICallLogInfo();
    private ObjectMapper objectMapper = new ObjectMapper();

    private int isAPICall(String url){
        if(url.contains("/api/user")){
            log.info("[Interceptor][API] USER CALL : {} 처리",url);
            return 1;
        } else if(url.contains("/api/memo")){
            log.info("[Interceptor][API] MEMO CALL : {} 처리",url);
            return 2;
        } else if(url.contains("/api/calender")){
            log.info("[Interceptor][API] CALENDER CALL : {} 처리",url);
            return 3;
        } else if(url.contains("/api/routine")){
            log.info("[Interceptor][API] ROUTINE CALL : {} 처리",url);
            return 4;
        } else if(url.contains("/api/group")){
            log.info("[Interceptor][API] GROUP CALL : {} 처리",url);
            return 5;
        } else if(url.contains("/api/etc")){
            log.info("[Interceptor][API] ETC CALL : {} 처리",url);
            return 6;
        } else if(url.contains("error")){
            log.info("[Interceptor][API] ERROR CALL : {} 처리",url);
            return -1;
        } else {
            log.info("[Interceptor][API] ELSE CALL : {} 처리",url);
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

            // 변수 세팅
            String url = String.valueOf((req).getRequestURL());
            String method = req.getMethod();
            String sessionId = req.getSession().getId();
            String reqId; String userId = null; String userIdx = null; String reqData = null;
            APICallLogInfo apiCallLogInfo = new APICallLogInfo();
            APICallLogInfo tmp = null;

            int result = isAPICall(url);
            int insertRes = -1;

            // 정의된 Call만 처리
            if (result > 0) {
                // Filter에서 attribute 세팅한 req_id를 get
                reqId = String.valueOf(req.getAttribute("req_id"));

                //POST, GET에 따라 USER_ID와 REQ_DATA 저장
                ServletInputStream inputStream = req.getInputStream();
                if ("GET".equals(method)) { // GET일 때는 쿼리스트링에서 데이터 GET
                    reqData = req.getQueryString();
                    userId = req.getParameter("user_id");
                } else {    // POST, PATCH, PUT, DELETE일 때는 BODY에서 데이터 GET
                    reqData = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    tmp = objectMapper.readValue(reqData, APICallLogInfo.class);
                    userId = tmp.getUser_id();
                }
                reqData=reqData.replace("\"","");

                // USERIDX 찾는 쿼리
                if (userId != null) {
                    userIdx = logRepository.getUserIdx(userId);
                }

                // REQUEST 정보 출력
                log.info("[Interceptor-PreHandle][Request] REQ_ID : {}", reqId);
                log.info("[Interceptor-PreHandle][Request] SESSION : {}", sessionId);
                log.info("[Interceptor-PreHandle][Request] URL : {}", url);
                log.info("[Interceptor-PreHandle][Request] METHOD : {}", method);
                log.info("[Interceptor-PreHandle][Request] BODY : {}", reqData);
                log.info("[Interceptor-PreHandle][Request] USER_ID : {}", userId);
                log.info("[Interceptor-PreHandle][Request] USER_IDX : {}", userIdx);

                //LOG 공통 데이터 세팅
                apiCallLogInfo.setReq_id(reqId);
                apiCallLogInfo.setUser_id(userId);
                apiCallLogInfo.setUser_idx(userIdx);
                apiCallLogInfo.setApi_url(url);
                apiCallLogInfo.setReq_data(reqData);
                apiCallLogInfo.setSession_id(sessionId);

                // LOG INSERT
                log.info("[Interceptor-PreHandle][Request] LOG INSERT : {}", apiCallLogInfo.toString());
                log.info("[Interceptor-PreHandle][Request] SWITCH : {}", result);
                switch (result) {
                    case 1: // /api/user
                        insertRes = logRepository.insertUserLog(apiCallLogInfo);
                        break;
                    case 2: // /api/memo
                        if ("GET".equals(method))
                            apiCallLogInfo.setMemo_idx(req.getParameter("memo_idx"));
                        else
                            apiCallLogInfo.setMemo_idx(tmp.getMemo_idx());
                        insertRes = logRepository.insertMemoLog(apiCallLogInfo);
                        break;
                    case 3: // /api/calender
                        if ("GET".equals(method))
                            apiCallLogInfo.setCalender_idx(req.getParameter("calender_idx"));
                        else
                            apiCallLogInfo.setCalender_idx(tmp.getCalender_idx());
                        insertRes = logRepository.insertCalenderLog(apiCallLogInfo);
                        break;
                    case 4: // /api/routine
                        if ("GET".equals(method))
                            apiCallLogInfo.setRoutine_idx(req.getParameter("routine_idx"));
                        else
                            apiCallLogInfo.setRoutine_idx(tmp.getRoutine_idx());
                        insertRes = logRepository.insertRoutineLog(apiCallLogInfo);
                        break;
                    case 5: // /api/group
                        if ("GET".equals(method))
                            apiCallLogInfo.setGroup_idx(req.getParameter("group_idx"));
                        else
                            apiCallLogInfo.setGroup_idx(tmp.getGroup_idx());
                        insertRes = logRepository.insertGroupLog(apiCallLogInfo);
                        break;
                    case 6: // /api/etc
                        if ("GET".equals(method)) {
                            apiCallLogInfo.setTarget_idx(req.getParameter("target_idx"));
                            apiCallLogInfo.setTarget_type(req.getParameter("target_type"));
                        } else {
                            apiCallLogInfo.setTarget_idx(tmp.getTarget_idx());
                            apiCallLogInfo.setTarget_type(tmp.getTarget_type());
                        }
                        insertRes = logRepository.insertEtcLog(apiCallLogInfo);
                        break;
                    default:
                        break;
                }
                if (insertRes > 0) {
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

        // 변수 세팅
        String url = String.valueOf(req.getRequestURL());
        String sessionId = req.getSession().getId();
        String reqId = String.valueOf(req.getAttribute("req_id"));
        String userId = null; String userIdx = null; String reqData = null;
        APICallLogInfo apiCallLogInfo = new APICallLogInfo();
        APICallLogInfo tmp = null;
        int result = isAPICall(url);
        int updateRes = -1;

        // 정의된 Call만 처리
        if(result>0){
            // 데이터 세팅
            ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) res;
            String responseBody = new String(responseWrapper.getContentAsByteArray());

            // RESPONSE 정보 출력
            log.info("[Interceptor-AfterCompletion][Response] REQ_ID : {}", reqId);
            log.info("[Interceptor-AfterCompletion][Response] SESSION : {}", sessionId);;
            log.info("[Interceptor-AfterCompletion][Response] URL : {}", url);
            log.info("[Interceptor-AfterCompletion][Response] STATUS : {}", responseWrapper.getStatus());
            log.info("[Interceptor-AfterCompletion][Response] BODY : {}", responseBody);
            log.info("[Interceptor-AfterCompletion][Response] USER_ID : {}", userId);
            log.info("[Interceptor-AfterCompletion][Response] USER_IDX : {}", userIdx);
        }
    }
}
