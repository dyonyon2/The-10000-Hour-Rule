package com.dyonyon.The10000HourRule.service;

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
    final int LoginAttemptCount = 10;

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

            // 1. USER_LOGIN_LOG TBL Insert => 로그 기록
            int result = userRepository.insertLoginLog(userLoginLogInfo);
            if (result != 1) {
                log.error("[Service-UserLogin][login][{}] Log Insert Failed : {}",req_id,result);
                userAuthInfo.setMsg("Login Log Insertion Failed");
                updateLogStatus(req_id, ResultCode.USER_LOGIN_INSERT_ERROR);
                return userAuthInfo;
            }

            // 2. USER_LOGIN_LOG TBL Select => 로그인 시도 체크 (동일 세션에서 동일 아이디 attempt)
            result = userRepository.getLoginAttempt(userLoginLogInfo);
            log.info("[Service-UserLogin][login][{}] Log Attempt Is {}",req_id,result);
            if(result >= LoginAttemptCount) {
                log.info("[Service-UserLogin][login][{}] Log Attempt {} Is Over Than {}",req_id,result,LoginAttemptCount);
                userAuthInfo.setMsg("Log Attempts Exceeded");
                updateLogStatus(req_id, ResultCode.USER_LOGIN_ATTEMPT_OVER_ERROR);
                return userAuthInfo;
            }

            // 3. 로그인 체크
            UserAuthInfo returnInfo = userRepository.getLoginResult(userLoginLogInfo);
            if(returnInfo != null) { // 로그인 요청시 입력한 USER_ID와, USER 테이블에서 SELECT한 정보의 USER_ID가 같으면 로그인 정보 일치한 것
                log.info("[Service-UserLogin][login][{}] Login Success : {}", req_id, returnInfo);
                returnInfo.setMsg("Login Success");

                // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                updateLogStatus(req_id, ResultCode.USER_LOGIN_SUCCESS);
                resetLoginAttempt(returnInfo.getUser_id());

                // 5. 로그인 세션 정보 테이블(USER_SESSION) Insert
                return returnInfo;
            } else {
                log.info("[Service-UserLogin][login][{}] Login Failed : Request {}",req_id,userLoginLogInfo);
                userAuthInfo.setMsg("Login Failed");

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
            if(result>0){
                log.info("[Service-UserLogin][login][{}] Login Log Status {} Update Successed : {}",req_id,status,result);
            } else {
                log.info("[Service-UserLogin][login][{}] Login Log Status {} Update Failed : {}",req_id,status,result);
            }
        } catch (Exception e){
            log.error("[Service-UserLogin][login][{}] Login Log Status {} Update Failed : {}",req_id,status,e.getMessage());
        }
    }

    public void resetLoginAttempt(String user_id){
        try{
            int result = userRepository.resetLoginAttempt(user_id);
            if(result>0){
                log.info("[Service-UserLogin][login][{}] Login Log Attempt Reset Successed : {}",user_id,result);
            } else {
                log.info("[Service-UserLogin][login][{}] Login Log Attempt Reset Failed : {}",user_id,result);
            }
        } catch (Exception e){
            log.info("[Service-UserLogin][login][{}] Login Log Attempt Reset Failed : {}",user_id,e.getMessage());
        }
    }
}
