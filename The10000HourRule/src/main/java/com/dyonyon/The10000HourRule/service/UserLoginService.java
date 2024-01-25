package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.code.ResultCode;
import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginLogInfo;
import com.dyonyon.The10000HourRule.repository.LogRepository;
import com.dyonyon.The10000HourRule.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserLoginService {

    private UserRepository userRepository;

    public UserLoginService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserAuthInfo login(HttpServletRequest req, UserAuthInfo userAuthInfo) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        try {
            log.info("[Service-UserLogin][login][{}] Login Started.", req_id);

            // 0. 변수 세팅
            UserLoginLogInfo userLoginLogInfo = new UserLoginLogInfo(); // 로그인 이전 정보
            userLoginLogInfo.setReq_id(req_id); userLoginLogInfo.setSession_id(req.getSession().getId()); userLoginLogInfo.setUser_id(userAuthInfo.getUser_id()); userLoginLogInfo.setPw(userAuthInfo.getPw());
            String sessionId = req.getSession().getId();

            // 1. USER_LOGIN_LOG TBL Insert => 로그 기록
            if (insertLoginLog(req_id, userLoginLogInfo) != 1) {
                log.info("[Service-UserLogin][login][{}] Login Log Insertion Failed",req_id);
                userAuthInfo.setMsg("Login Log Insertion Failed");
                updateLogStatus(req_id, ResultCode.USER_LOGIN_INSERT_ERROR);
                return userAuthInfo;
            }

            // 2. USER_LOGIN_LOG TBL Select => 로그인 시도 체크 (동일 세션에서 동일 아이디 attempt)
            int attempt = checkLoginAttempt(req_id, userLoginLogInfo);
            if(attempt >= GlobalConstants.LoginAttemptCount) {
                log.info("[Service-UserLogin][login][{}] Log Attempts Exceeded : attempt {} Is Over Than {}",req_id,attempt,GlobalConstants.LoginAttemptCount);
                userAuthInfo.setMsg("Log Attempts Exceeded");
                updateLogStatus(req_id, ResultCode.USER_LOGIN_ATTEMPT_OVER_ERROR);
                return userAuthInfo;
            }

            // 3. 로그인 체크
            UserAuthInfo returnInfo = checkLogin(req_id, userLoginLogInfo);
            if(returnInfo != null) { // 로그인 요청시 입력한 USER_ID와, USER 테이블에서 SELECT한 정보의 USER_ID가 같으면 로그인 정보 일치한 것
                returnInfo.setMsg("Login Success");
                userLoginLogInfo.setUser_idx(returnInfo.getUser_idx());

                // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                updateLogStatus(req_id, ResultCode.USER_LOGIN_SUCCESS);
                resetLoginAttempt(returnInfo.getUser_id());

                // 5. 로그인 세션 정보 테이블(USER_SESSION) Insert
                int insertResult = insertLoginSession(req_id, userLoginLogInfo);
                if(insertResult != 1){
                    log.info("[Service-UserLogin][login][{}] Session Insert Failed : {}",req_id,insertResult);
                    userAuthInfo.setMsg("Login Failed : Session Insert Failed");
                    return userAuthInfo;
                }
                return returnInfo;
            } else {
                log.info("[Service-UserLogin][login][{}] Login Failed : Invalid ID/PW : {} / {}",req_id,userLoginLogInfo.getUser_id(),userLoginLogInfo.getPw());
                userAuthInfo.setMsg("Login Failed : Invalid ID/PW");

                // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                updateLogStatus(req_id, ResultCode.USER_LOGIN_FAIL);
                return userAuthInfo;
            }
        } catch (Exception e){
            log.error("[Service-UserLogin][login][{}] ERROR OCCURRED : {}",req_id,e.getMessage());
            userAuthInfo.setMsg("ERROR OCCURRED : "+e.getMessage());
            return userAuthInfo;
        }
    }


    public void updateLogStatus(String req_id, String status){
        try{
            int result = userRepository.updateStatus(req_id, status);
            log.info("[Service-UserLogin][updateLogStatus][{}] Login Log Status({}) Update Successed : {}",req_id,status,result);
        } catch (Exception e){
            log.error("[Service-UserLogin][updateLogStatus][{}] Login Log Status({}) Update Failed : {}",req_id, status, e.getMessage());
        }
    }
    public int insertLoginLog(String req_id, UserLoginLogInfo info){
        int result = -1;
        try {
            result = userRepository.insertLoginLog(info);
            log.info("[Service-UserLogin][insertLoginLog][{}] Login Log Insert Successed : {}",req_id,result);
        } catch (Exception e) {
            log.error("[Service-UserLogin][insertLoginLog][{}] Login Log Insert Failed : {}, {}",req_id,result,e.getMessage());
        }
        return result;
    }

    public int checkLoginAttempt(String req_id, UserLoginLogInfo info){
        int result = 0;
        try {
            result = userRepository.getLoginAttempt(info);
            log.info("[Service-UserLogin][checkLoginAttempt][{}] Login Attempt Select Successed : {}",req_id,result);
        } catch (Exception e) {
            log.error("[Service-UserLogin][checkLoginAttempt][{}] Login Attempt Select Failed : {}, {}",req_id,result,e.getMessage());
        }
        return result;
    }

    public UserAuthInfo checkLogin(String req_id, UserLoginLogInfo info){
        UserAuthInfo result = null;
        try {
            result = userRepository.getLoginResult(info);
            if(result!=null)
                log.info("[Service-UserLogin][getLoginResult][{}] Login Success : {}", req_id, result);
            else
                log.error("[Service-UserLogin][getLoginResult][{}] Login Failed (ID/PW does not match) ID/PW : {}",req_id,info);
        } catch (Exception e) {
            log.error("[Service-UserLogin][getLoginResult][{}] Login Failed (SQL Failed) ID/PW : {}, {}",req_id,info,e.getMessage());
        }
        return result;
    }

    public void resetLoginAttempt(String user_id){
        try{
            int result = userRepository.resetLoginAttempt(user_id);
            log.info("[Service-UserLogin][resetLoginAttempt][{}] Login Log Flag Reset Successed : {}",user_id,result);
        } catch (Exception e){
            log.info("[Service-UserLogin][resetLoginAttempt][{}] Login Log Flag Reset Failed : {}",user_id,e.getMessage());
        }
    }

    // Login Session TBL에서 기존 로그인 세션 select
    // 기록이 있다면 update, 없다면 insert
    public int insertLoginSession(String req_id,UserLoginLogInfo info){
        int result = -1;
        try {
            result = userRepository.getLoginSession(info.getUser_idx());
            if(result == 1){
                log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Select Successed : {}",req_id, result);
                result = userRepository.updateLoginSession(info);
            } else if(result == 0){
                log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Select Successed : {}",req_id, result);
                result = userRepository.insertLoginSession(info);
            } else {
                log.error("[Service-UserLogin][insertLoginSession][{}] Login Session Select Failed : {}",req_id, result);
            }
        } catch (Exception e) {
            log.error("[Service-UserLogin][insertLoginSession][{}] Login Session Insert Failed : {}, {}",req_id, result, e.getMessage());
            return -1;
        }
        return result;
    }
}