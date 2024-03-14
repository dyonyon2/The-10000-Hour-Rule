package com.dyonyon.The10000HourRule.common;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.domain.APICallLogInfo;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.mapper.LogMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;

@Slf4j
public class APIInterceptor implements HandlerInterceptor {

    @Autowired
    private LogMapper logMapper;
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
            String ownerId=null; String ownerIdx=null; String contentIdx = null;
            APICallLogInfo apiCallLogInfo = new APICallLogInfo();
            APICallLogInfo tmp = null;
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            int result = isAPICall(url);
            int insertRes = -1;

            String contentType = req.getContentType();

            // 정의된 Call만 처리
            if (result > 0) {
                // Filter에서 attribute 세팅한 req_id를 get
                reqId = String.valueOf(req.getAttribute("req_id"));

                //POST, GET에 따라 USER_ID와 REQ_DATA 저장
                // GET일 때는 쿼리스트링에서 데이터 GET
                if ("GET".equals(method)) {
                    reqData = req.getQueryString();
                    userId = req.getParameter("user_id");
                    ownerId = req.getParameter("owner_id");
                }
                // Form-data인 경우는 File과 Body 따로 데이터 GET
                else if(contentType!=null && contentType.contains("form-data")) {
//                    log.info("req getParameterName contentType : " + contentType);
                    for(Part part : req.getParts()){
                        if(part.getName()!=null && part.getName().equals(GlobalConstants.FILE)){
                            reqData = "File("+part.getSubmittedFileName()+")";
                        }
                    }
                    reqData = reqData + ", Json("+req.getParameter(GlobalConstants.JSON)+")";
                    tmp = objectMapper.readValue(req.getParameter(GlobalConstants.JSON), APICallLogInfo.class);
                    userId = tmp.getUser_id();
                    contentIdx = tmp.getMemo_idx();
                    ownerId = tmp.getOwner_id();
//                    log.info("tmp = {}",tmp);
//                    log.info("reqData = {}",reqData);
                }
                // POST, PATCH, PUT, DELETE일 때는 BODY에서 데이터 GET
                else {
                    ServletInputStream inputStream = req.getInputStream();
                    reqData = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                    tmp = objectMapper.readValue(reqData, APICallLogInfo.class);
                    userId = tmp.getUser_id();
                    ownerId = tmp.getOwner_id();
                }

                if(reqData!=null)
                    reqData=reqData.replace("\"","");

                // USERIDX 찾는 쿼리
                if (userId != null) {
                    userIdx = logMapper.getUserIdx(userId);
                    req.setAttribute("user_id",userId);
                    req.setAttribute("user_idx",userIdx);
                }

                // REQUEST 정보 출력
//                log.info("[Interceptor-PreHandle][Request][{}] REQ_ID : {}", reqId);
                log.trace("[Interceptor-PreHandle][Request][{}] URL : {}", reqId, url);
                log.trace("[Interceptor-PreHandle][Request][{}] SESSION : {}", reqId,sessionId);
                log.trace("[Interceptor-PreHandle][Request][{}] METHOD : {}", reqId, method);
                log.trace("[Interceptor-PreHandle][Request][{}] BODY : {}", reqId, reqData);
                log.trace("[Interceptor-PreHandle][Request][{}] USER_ID : {}", reqId, userId);
                log.trace("[Interceptor-PreHandle][Request][{}] USER_IDX : {}", reqId, userIdx);

                //LOG 공통 데이터 세팅
                apiCallLogInfo.setReq_id(reqId);
                apiCallLogInfo.setUser_id(userId);
                apiCallLogInfo.setUser_idx(userIdx);
                apiCallLogInfo.setApi_url(url);
                apiCallLogInfo.setReq_data(reqData);
                apiCallLogInfo.setSession_id(sessionId);

                // LOG INSERT
                log.trace("[Interceptor-PreHandle][Request][{}] LOG INSERT : {}", reqId, apiCallLogInfo.toString());
                log.trace("[Interceptor-PreHandle][Request][{}] SWITCH : {}", reqId, result);
                switch (result) {
                    case 1: // /api/user
                        insertRes = logMapper.insertUserLog(apiCallLogInfo);
                        break;
                    case 2: // /api/memo
                        if ("GET".equals(method))
                            apiCallLogInfo.setMemo_idx(req.getParameter("memo_idx"));
//                        else if(contentType!=null && contentType.contains("form-data"))
//                            apiCallLogInfo.setMemo_idx(contentIdx);
                        else
                            apiCallLogInfo.setMemo_idx(tmp.getMemo_idx());
                        insertRes = logMapper.insertMemoLog(apiCallLogInfo);
                        break;
                    case 3: // /api/calender
                        if ("GET".equals(method))
                            apiCallLogInfo.setCalender_idx(req.getParameter("calender_idx"));
                        else
                            apiCallLogInfo.setCalender_idx(tmp.getCalender_idx());
                        insertRes = logMapper.insertCalenderLog(apiCallLogInfo);
                        break;
                    case 4: // /api/routine
                        if ("GET".equals(method))
                            apiCallLogInfo.setRoutine_idx(req.getParameter("routine_idx"));
                        else
                            apiCallLogInfo.setRoutine_idx(tmp.getRoutine_idx());
                        insertRes = logMapper.insertRoutineLog(apiCallLogInfo);
                        break;
                    case 5: // /api/group
                        if ("GET".equals(method))
                            apiCallLogInfo.setGroup_idx(req.getParameter("group_idx"));
                        else
                            apiCallLogInfo.setGroup_idx(tmp.getGroup_idx());
                        insertRes = logMapper.insertGroupLog(apiCallLogInfo);
                        break;
                    case 6: // /api/etc
                        if ("GET".equals(method)) {
                            apiCallLogInfo.setTarget_idx(req.getParameter("target_idx"));
                            apiCallLogInfo.setTarget_type(req.getParameter("target_type"));
                        } else {
                            apiCallLogInfo.setTarget_idx(tmp.getTarget_idx());
                            apiCallLogInfo.setTarget_type(tmp.getTarget_type());
                        }
                        insertRes = logMapper.insertEtcLog(apiCallLogInfo);
                        break;
                    default:
                        break;
                }
                if (insertRes > 0) {
                    log.trace("[Interceptor-PreHandle][Request][{}] LOG INSERT SUCCESS : {}", reqId, insertRes);
                } else {
                    log.error("[Interceptor-PreHandle][Request][{}] LOG INSERT Fail : {}", reqId, insertRes);
                }

                return true;
            }
            return true;
        } catch (Exception e){
            log.error("[Interceptor-PreHandle][ERROR] "+e.getMessage());
            log.error("[Interceptor-PreHandle][ERROR] Error PrintStack : ",e);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
        try {
            // 변수 세팅
            String url = String.valueOf(req.getRequestURL());
            String sessionId = req.getSession().getId();
            String reqId = String.valueOf(req.getAttribute("req_id"));
            String userId = String.valueOf(req.getAttribute("user_id"));
            String userIdx = String.valueOf(req.getAttribute("user_idx"));
            APICallLogInfo apiCallLogInfo = new APICallLogInfo();
            int result = isAPICall(url);
            int updateRes = -1;

            // 정의된 Call만 처리
            if (result > 0) {
                // 데이터 세팅
                ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) res;
                ResponseInfo responseInfo = objectMapper.readValue(responseWrapper.getContentAsByteArray(), ResponseInfo.class);
                String responseBody = responseInfo.getRes_data() == null ? "" : responseInfo.getRes_data().toString();
                responseBody = responseBody.substring(0, Math.min(responseBody.length(), GlobalConstants.MaxDataLength));
                String responseMsg = responseInfo.getMsg() == null ? "" : responseInfo.getMsg();
                String responseStatus = responseInfo.getStatus();
                String res_status = responseInfo.getRes_status();
                String err_code = responseInfo.getErr_code();

                // RESPONSE 정보 출력
                //            log.info("[Interceptor-AfterCompletion][Response][{}] REQ_ID : {}", reqId);
                log.trace("[Interceptor-AfterCompletion][Response][{}] URL : {}", reqId, url);
                log.trace("[Interceptor-AfterCompletion][Response][{}] SESSION : {}", reqId, sessionId);
                ;
                log.trace("[Interceptor-AfterCompletion][Response][{}] STATUS : HTTP({}), STATUS({}), RES_STATUS({})", reqId, responseWrapper.getStatus(), responseStatus, res_status);
                log.trace("[Interceptor-AfterCompletion][Response][{}] MSG : {}", reqId, responseMsg);
                log.trace("[Interceptor-AfterCompletion][Response][{}] BODY : {}", reqId, responseBody);
                log.trace("[Interceptor-AfterCompletion][Response][{}] USER_ID : {}", reqId, userId);
                log.trace("[Interceptor-AfterCompletion][Response][{}] USER_IDX : {}", reqId, userIdx);
                log.trace("[Interceptor-AfterCompletion][Response][{}] ERR_CODE : {}", reqId, err_code);

                apiCallLogInfo.setReq_id(reqId);
                apiCallLogInfo.setRes_data(responseBody.replace("'", ""));
                apiCallLogInfo.setStatus(responseStatus);
                apiCallLogInfo.setMsg(responseMsg);
                apiCallLogInfo.setRes_status(res_status);
                apiCallLogInfo.setErr_code(err_code);

                // LOG INSERT
                log.trace("[Interceptor-AfterCompletion][Response][{}] LOG UPDATE : {}", reqId, apiCallLogInfo.toString());
                log.trace("[Interceptor-AfterCompletion][Response][{}] SWITCH : {}", reqId, result);
                switch (result) {
                    case 1: // /api/user
                        updateRes = logMapper.updateResDataUserLog(apiCallLogInfo);
                        break;
                    case 2: // /api/memo
                        updateRes = logMapper.updateResDataMemoLog(apiCallLogInfo);
                        break;
                    case 3: // /api/calender
                        updateRes = logMapper.updateResDataCalenderLog(apiCallLogInfo);
                        break;
                    case 4: // /api/routine
                        updateRes = logMapper.updateResDataRoutineLog(apiCallLogInfo);
                        break;
                    case 5: // /api/group
                        updateRes = logMapper.updateResDataGroupLog(apiCallLogInfo);
                        break;
                    case 6: // /api/etc
                        updateRes = logMapper.updateResDataEtcLog(apiCallLogInfo);
                        break;
                    default:
                        break;
                }

                if (updateRes > 0) {
                    log.trace("[Interceptor-AfterCompletion][Response][{}] LOG UPDATE SUCCESS : {}", reqId, updateRes);
                } else {
                    log.error("[Interceptor-AfterCompletion][Response][{}] LOG UPDATE Fail : {}", reqId, updateRes);
                }
            }
        } catch (Exception e){
            log.error("[Interceptor-AfterCompletion][ERROR] "+e.getMessage());
            log.error("[Interceptor-AfterCompletion][ERROR] Error PrintStack : ",e);
        }
    }
}
