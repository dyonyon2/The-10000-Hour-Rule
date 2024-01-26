package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.mapper.UserLoginMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;


// 로그인 이후, 필요한 공통 서비스
@Service
@Slf4j
public class APICheckService {

    private UserLoginMapper userLoginMapper;

    public APICheckService(UserLoginMapper userLoginMapper) {
        this.userLoginMapper = userLoginMapper;
    }

    // 세션 유효/만료 체크
    // Status로 로그인 세션 확인 체크 ( 세션 정상 : 1, 세션 비정상(로그인필요) : -1 )
    public ResponseInfo checkLoginSession(String req_id, String user_id, String session_id){

        Date now = new Date();
        Date exp;
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_code("000000");

        try {
            log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Check Started...", req_id, user_id);

            String session = getSessionId(req_id, user_id, responseInfo);// ID를 LoginSession TBL에서 검색
            if (session != null) {  // select결과 있을 때
                log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Select Success : {}(Now) {}(Prev) ", req_id, user_id, session_id, session);
                if (session.equals(session_id)) {   // session이 동일할 때
                    exp = getSessionEXP(req_id, user_id, responseInfo); // expire_dt를 LoginSession TBL에서 검색
                    log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session EXP Select Success : {}(Now) {}(Exp) ", req_id, user_id, now, exp);
                    if (now.getTime() < exp.getTime()) { // expire_dt가 안 지났을 때 = session이 유요할 때
                        responseInfo.setMsg("Login Session Is Valid");
                        log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Is Valid", req_id, user_id);

                        updateLoginSession(req_id, user_id); // Session UPDATE_DT, EXPIRE_DT update

                    } else {    // expire_dt가 지났을 때 = session이 만료되었을 때
                        responseInfo.setStatus("-1");
                        responseInfo.setMsg("Login Session Is Invalid : Session Is Expired");
                        log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Is Invalid : Session Is Expired", req_id, user_id);
                    }
                } else {    // session이 다를 때
                    responseInfo.setStatus("-1");
                    responseInfo.setMsg("Login Session Is Invalid : Sessions Are Different");
                    log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Is Invalid : Sessions Are Different", req_id, user_id);
                }
            } else {
                responseInfo.setStatus("-1");
                responseInfo.setMsg("Login Session Is Invalid : Session Is Not Exist");
                log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Is Invalid : Session Is Not Exist", req_id, user_id);
            }
            return responseInfo;
        } catch (Exception e){
            responseInfo.setMsg("Login Session Check Failed : Unknown Error Occurred");
            responseInfo.setStatus("-1");
            responseInfo.setRes_data(e.getMessage());
            responseInfo.setRes_code("UN");
            log.error("[Service-APICheck][checkLoginSession][{}] Login({}) Session Validation Check Failed : ERROR OCCURRED {}",req_id,user_id,e.getMessage());
            return responseInfo;
        }
    }

    public String getSessionId(String req_id, String user_id, ResponseInfo resInfo){
        String result = null;
        try {
            result = userLoginMapper.getLoginSessionId(user_id);
            log.info("[Service-APICheck][getSessionId][{}] Login({}) Session Select Successed : {}",req_id, user_id, result);
        } catch (Exception e) {
            log.error("[Service-APICheck][getSessionId][{}] Login({}) Session Select Failed : {}",req_id, user_id, e.getMessage());
            resInfo.setRes_data(e.getMessage());
        }
        return result;
    }

    public Date getSessionEXP(String req_id, String user_id, ResponseInfo resInfo){
        Date exp = null;
        try {
            exp = userLoginMapper.getLoginSessionEXP(user_id);
            log.info("[Service-APICheck][getSessionEXP][{}] Login({}) Session EXP Select Successed : {}",req_id, user_id, exp);
        } catch (Exception e) {
            log.error("[Service-APICheck][getSessionEXP][{}] Login({}) Session EXP Select Failed : {}",req_id, user_id, e.getMessage());
            resInfo.setRes_data(e.getMessage());
        }
        return exp;
    }

    // 세션 UPDATE_DT, EXPIRE_DT UPDATE
    public void updateLoginSession(String req_id, String user_id){
        // UPDATE_DT와 EXPIRE_DT update
        try {
            int result = userLoginMapper.updateSessionTime(user_id);
            log.info("[Service-APICheck][updateLoginSession][{}] Login({}) Session EXP Update Successed : {}",req_id, user_id, result);
        } catch (Exception e) {
            log.error("[Service-APICheck][updateLoginSession][{}] Login({}) Session EXP Update Failed : {}",req_id, user_id, e.getMessage());
        }
    }

    // 서약 동의 여부

    // 핸드폰/이메일 인증 여부

}
