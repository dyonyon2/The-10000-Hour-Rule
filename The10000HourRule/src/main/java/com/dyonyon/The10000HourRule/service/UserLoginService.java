package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.code.ResultCode;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginInfo;
import com.dyonyon.The10000HourRule.mapper.UserLoginMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserLoginService {

    private UserLoginMapper userLoginMapper;

    public UserLoginService(UserLoginMapper userLoginMapper){
        this.userLoginMapper = userLoginMapper;
    }

    public ResponseInfo login(HttpServletRequest req, UserLoginInfo userLoginInfo) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_code("000000");
        try {
            log.info("[Service-UserLogin][login][{}] Login Started...", req_id);

            // 0. 변수 세팅
//            UserLoginInfo userLoginInfo = new UserLoginInfo(); // 로그인 이전 정보
            userLoginInfo.setReq_id(req_id); userLoginInfo.setSession_id(req.getSession().getId());
            String sessionId = req.getSession().getId();

            // 1. USER_LOGIN_LOG TBL Insert => 로그 기록
            if (insertLoginLog(req_id, userLoginInfo, responseInfo) != 1) {
                log.info("[Service-UserLogin][login][{}] Login Log Insertion Failed : {}",req_id,responseInfo.getRes_data());
                responseInfo.setMsg("Login Failed : Login Log Insertion Failed");
                responseInfo.setStatus("-1"); //RES_CODE도 정리&추가 되어야함
                updateLogStatus(req_id, ResultCode.USER_LOGIN_INSERT_ERROR, responseInfo);
                return responseInfo;
            }

            // 2. USER_LOGIN_LOG TBL Select => 로그인 시도 체크 (동일 세션에서 동일 아이디 attempt)
            int attempt = checkLoginAttempt(req_id, userLoginInfo, responseInfo);
            if(attempt >= GlobalConstants.LoginAttemptCount) {
                log.info("[Service-UserLogin][login][{}] Log Attempts Exceeded : Attempt {} Is Over Than {}",req_id,attempt,GlobalConstants.LoginAttemptCount);
                responseInfo.setMsg("Login Failed : Log Attempts Exceeded");
                updateLogStatus(req_id, ResultCode.USER_LOGIN_ATTEMPT_OVER_ERROR, responseInfo);
                return responseInfo;
            }

            // 3. 로그인 체크
            UserDetailInfo loginUserInfo = checkLogin(req_id, userLoginInfo, responseInfo);
            if(loginUserInfo != null) { // 로그인 요청시 입력한 USER_ID와, USER 테이블에서 SELECT한 정보의 USER_ID가 같으면 로그인 정보 일치한 것
//                returnInfo.setMsg("Login Success");
                responseInfo.setMsg("Login Success");
                responseInfo.setRes_data(loginUserInfo);
                userLoginInfo.setUser_idx(loginUserInfo.getUser_idx());

                // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                updateLogStatus(req_id, ResultCode.USER_LOGIN_SUCCESS, responseInfo);

                // 5. 로그인 정보 테이블(USER_LOGIN_LOG) Flag update
                resetLoginAttempt(loginUserInfo.getUser_id(), responseInfo);

                // 6. 로그인 세션 정보 테이블(USER_SESSION) Insert
                if( insertLoginSession(req_id, userLoginInfo, responseInfo) != 1){
                    log.info("[Service-UserLogin][login][{}] Session Insert Failed : {}",req_id,responseInfo.getRes_data());
                    responseInfo.setStatus("-1");
                    responseInfo.setMsg("Login Failed : Session Insert Failed");
                    return responseInfo;
                }
                log.info("[Service-UserLogin][login][{}] Login Success...",req_id);
                return responseInfo;
            } else {
                log.info("[Service-UserLogin][login][{}] Login Failed : Invalid ID/PW : {} / {}",req_id, userLoginInfo.getUser_id(), userLoginInfo.getPw());
                responseInfo.setMsg("Login Failed : Invalid ID/PW");

                // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                updateLogStatus(req_id, ResultCode.USER_LOGIN_FAIL, responseInfo);
                return responseInfo;
            }
        } catch (Exception e){
            log.error("[Service-UserLogin][login][{}] Login Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            responseInfo.setStatus("-1");
            responseInfo.setMsg("Login Failed : Unknown Error Occurred");
            responseInfo.setRes_data(e.getMessage());
            responseInfo.setRes_code("UN");
            return responseInfo;
        }
    }

    public void updateLogStatus(String req_id, String status, ResponseInfo resInfo){
        try{
            int result = userLoginMapper.updateStatus(req_id, status);
            log.info("[Service-UserLogin][updateLogStatus][{}] Login Log Status({}) Update Successed : {}",req_id,status,result);
        } catch (Exception e){
            log.error("[Service-UserLogin][updateLogStatus][{}] Login Log Status({}) Update Failed : {}",req_id, status, e.getMessage());
            resInfo.setRes_data(e.getMessage());
        }
    }
    public int insertLoginLog(String req_id, UserLoginInfo info, ResponseInfo resInfo){
        int result = -1;
        try {
            result = userLoginMapper.insertLoginLog(info);
            log.info("[Service-UserLogin][insertLoginLog][{}] Login Log Insert Successed : {}",req_id,result);
        } catch (Exception e) {
            log.error("[Service-UserLogin][insertLoginLog][{}] Login Log Insert Failed : {}, {}",req_id,result,e.getMessage());
            resInfo.setRes_data(e.getMessage());
        }
        return result;
    }

    public int checkLoginAttempt(String req_id, UserLoginInfo info, ResponseInfo resInfo){
        int result = 0;
        try {
            result = userLoginMapper.getLoginAttempt(info);
            log.info("[Service-UserLogin][checkLoginAttempt][{}] Login Attempt Select Successed : {}",req_id,result);
        } catch (Exception e) {
            log.info("[Service-UserLogin][checkLoginAttempt][{}] Login Attempt Select Failed : {}, {}",req_id,result,e.getMessage());
            resInfo.setRes_data(e.getMessage());
        }
        return result;
    }

    public UserDetailInfo checkLogin(String req_id, UserLoginInfo info, ResponseInfo resInfo){
        UserDetailInfo result = null;
        try {
            result = userLoginMapper.getLoginResult(info);
            if(result!=null)
                log.info("[Service-UserLogin][getLoginResult][{}] Login Success : {}", req_id, result);
            else
                log.info("[Service-UserLogin][getLoginResult][{}] Login Failed (ID/PW does not match) ID/PW : {} / {}",req_id,info.getUser_id(),info.getPw());
        } catch (Exception e) {
            log.error("[Service-UserLogin][getLoginResult][{}] Login Failed (SQL Failed) ID/PW : {}, {}",req_id,info,e.getMessage());
            resInfo.setRes_data(e.getMessage());
        }
        return result;
    }

    public void resetLoginAttempt(String user_id, ResponseInfo resInfo){
        try{
            int result = userLoginMapper.resetLoginAttempt(user_id);
            log.info("[Service-UserLogin][resetLoginAttempt][{}] Login Log Flag Reset Successed : {}",user_id,result);
        } catch (Exception e){
            log.error("[Service-UserLogin][resetLoginAttempt][{}] Login Log Flag Reset Failed : {}",user_id,e.getMessage());
            resInfo.setRes_data(e.getMessage());
        }
    }

    // Login Session TBL에서 기존 로그인 세션 select
    // 기록이 있다면 update, 없다면 insert
    public int insertLoginSession(String req_id, UserLoginInfo info, ResponseInfo resInfo){
        int result = -1;
        try {
            String session = userLoginMapper.getLoginSessionId(info.getUser_id());
            if(session != null){
                log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Select Successed : {}",req_id, session);
                result = userLoginMapper.updateLoginSession(info);
                if(result==1)
                    log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Update Successed : {}",req_id, result);
                else
                    log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Update Failed : {}",req_id, result);
            } else{
                log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Select Successed : Previous Session is not exist",req_id);
                result = userLoginMapper.insertLoginSession(info);
                if(result==1)
                    log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Insert Successed : {}",req_id, result);
                else
                    log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Insert Failed : {}",req_id, result);
            }
        } catch (Exception e) {
            log.error("[Service-UserLogin][insertLoginSession][{}] Login Session Insert Failed : {}, {}",req_id, result, e.getMessage());
            resInfo.setRes_data(e.getMessage());
            return -1;
        }
        return result;
    }
}