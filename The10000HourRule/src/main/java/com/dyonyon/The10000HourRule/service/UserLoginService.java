package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.code.ResultCode;
import com.dyonyon.The10000HourRule.common.FunctionException;
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
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");
        try {
            log.info("[Service-UserLogin][login][{}] Login Started...", req_id);

            // 0. 변수 세팅
            userLoginInfo.setReq_id(req_id); userLoginInfo.setSession_id(req.getSession().getId());

            // 1. 로그인 로그 기록
            insertLoginLog(req_id, userLoginInfo, responseInfo);

            // 2. 로그인 시도 체크 (동일 세션&아이디 로그인 로그 개수)
            int attempt = checkLoginAttempt(req_id, userLoginInfo, responseInfo);

            // 2-1.로그인 시도 횟수가 기준값 초과일 때 -> 로그인 실패
            if(attempt >= GlobalConstants.LoginAttemptCount) {
                log.info("[Service-UserLogin][login][{}] Log Attempts Exceeded : Attempt {} Is Over Than {}",req_id,attempt,GlobalConstants.LoginAttemptCount);
                responseInfo.setMsg("Login Failed : Log Attempts Exceeded");
                responseInfo.setRes_data("Log Attempts Exceeded");
                responseInfo.setRes_status("-1");
                updateLogStatus(req_id, ResultCode.USER_LOGIN_ATTEMPT_OVER_ERROR, responseInfo, true);
            }
            // 2-2. 로그인 시도 횟수가 기준값 미만일 때 -> 3. 로그인 체크 진행
            else {

                // 3. 로그인 체크
                UserDetailInfo loginUserInfo = checkLogin(req_id, userLoginInfo, responseInfo);

                // 3-1. 로그인 성공 (로그인 요청시 입력한 USER_ID와, USER 테이블 정보의 USER_ID가 같으면 로그인 정보 일치한 것)
                if (loginUserInfo != null) {
                    log.info("[Service-UserLogin][login][{}] Login Success...", req_id);

                    // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                    updateLogStatus(req_id, ResultCode.USER_LOGIN_SUCCESS, responseInfo, false);

                    // 5. 로그인 정보 테이블(USER_LOGIN_LOG) Flag update
                    resetLoginAttempt(loginUserInfo.getUser_id(), responseInfo);

                    // 6. 로그인 세션 정보 테이블(USER_SESSION) Insert
                    if (insertLoginSession(req_id, userLoginInfo, responseInfo) != 1) {
                        log.info("[Service-UserLogin][login][{}] Session Insert Failed : {}", req_id, responseInfo.getRes_data());
                        return responseInfo;
                    }
                    log.info("[Service-UserLogin][login][{}] Login Success...", req_id);
                    return responseInfo;
                }
                // 3-2. 로그인 실패 -> 로그인 로그 업데이트
                else {
                    log.info("[Service-UserLogin][login][{}] Login Failed : Invalid ID/PW : {} / {}", req_id, userLoginInfo.getUser_id(), userLoginInfo.getPw());

                    // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                    updateLogStatus(req_id, ResultCode.USER_LOGIN_FAIL, responseInfo, true);
                    return responseInfo;
                }
            }
        } catch (FunctionException e){
            log.error("[Service-UserLogin][login][{}] Login Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserLogin][login][{}] Login Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Login Failed : Exception Occurred");
            responseInfo.setRes_data("Login Log Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public void updateLogStatus(String req_id, String status, ResponseInfo resInfo, boolean isError) throws FunctionException {
        try{
            int result = userLoginMapper.updateStatus(req_id, status);
            log.info("[Service-UserLogin][updateLogStatus][{}] Login Log Status({}) Update Successed : {}",req_id,status,result);
        } catch (Exception e){
            log.error("[Service-UserLogin][updateLogStatus][{}] Login Log Status({}) Update Failed : {}", req_id, status, e.getMessage());
            if(!isError) {
                resInfo.setMsg("Login Failed : Exception Occurred");
                resInfo.setStatus("-1");
                resInfo.setRes_status("-1");
                resInfo.setRes_data("Login Log Status Update Failed : " + e.getMessage());
                throw new FunctionException("Login Log Insertion Failed : "+e.getMessage());
            }
        }
    }

    // Insert Return 값이 1이면 정상, 그 이외는 비정상
    public void insertLoginLog(String req_id, UserLoginInfo info, ResponseInfo resInfo) throws FunctionException {
        int result = -1;
        try {
            result = userLoginMapper.insertLoginLog(info);
            if(result == 1){
                log.info("[Service-UserLogin][Login][insertLoginLog][{}] Login Log Insert Successed : ID({})",req_id, info.getUser_id());
            } else {
                throw new Exception("Result("+result+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserLogin][Login][insertLoginLog][{}] Login Log Insert Failed : ID({}) {}, {}",req_id,info.getUser_id(), result,e.getMessage());
            resInfo.setMsg("Login Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("Login Log Insertion Failed : "+e.getMessage());
            throw new FunctionException("Login Log Insertion Failed : "+e.getMessage());
        }
    }

    public int checkLoginAttempt(String req_id, UserLoginInfo info, ResponseInfo resInfo) throws FunctionException {
        int result = 0;
        try {
            result = userLoginMapper.getLoginAttempt(info);
            log.info("[Service-UserLogin][checkLoginAttempt][{}] Login Attempt Select Successed : {}",req_id,result);
        } catch (Exception e) {
            log.info("[Service-UserLogin][checkLoginAttempt][{}] Login Attempt Select Failed : {}, {}",req_id,result,e.getMessage());
            resInfo.setRes_data("Login Attempt Select Failed : "+e.getMessage());
            resInfo.setMsg("Login Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            throw new FunctionException("Login Attempt Select Failed : "+e.getMessage());
        }
        return result;
    }

    public UserDetailInfo checkLogin(String req_id, UserLoginInfo info, ResponseInfo resInfo) throws FunctionException {
        UserDetailInfo result = null;
        try {
            result = userLoginMapper.getLoginResult(info);
            if(result!=null) {
                log.info("[Service-UserLogin][getLoginResult][{}] Login Success : {}", req_id, result);
                resInfo.setMsg("Login Success");
                resInfo.setRes_data(result);
                info.setUser_idx(result.getUser_idx());
            }
            else {
                log.info("[Service-UserLogin][getLoginResult][{}] Login Failed (Invalid ID/PW) ID({}) PW({})", req_id, info.getUser_id(), info.getPw());
                resInfo.setMsg("Login Failed : Invalid ID/PW");
                resInfo.setRes_status("-1");
                resInfo.setRes_data("Invalid ID/PW : ID("+info.getUser_id()+") PW("+info.getPw()+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserLogin][getLoginResult][{}] Login Failed (SQL Failed) ID({}) PW({}) : {}", req_id, info.getUser_id(), info.getPw(), e.getMessage());
            resInfo.setMsg("Login Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("Login ID/PW Select Failed : "+e.getMessage());
            throw new FunctionException("Login ID/PW Select Failed : "+e.getMessage());
        }
        return result;
    }

    public void resetLoginAttempt(String user_id, ResponseInfo resInfo) throws FunctionException {
        try{
            int result = userLoginMapper.resetLoginAttempt(user_id);
            log.info("[Service-UserLogin][resetLoginAttempt][{}] Login Log Flag Reset Successed : {}",user_id,result);
        } catch (Exception e){
            log.error("[Service-UserLogin][resetLoginAttempt][{}] Login Log Flag Reset Failed : {}",user_id,e.getMessage());
            resInfo.setMsg("Login Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("Login Attempt Reset Failed : "+e.getMessage());
            throw new FunctionException("Login Attempt Reset Failed : "+e.getMessage());
        }
    }

    // Login Session TBL에서 기존 로그인 세션 select
    // 기록이 있다면 update, 없다면 insert
    public int insertLoginSession(String req_id, UserLoginInfo info, ResponseInfo resInfo) throws FunctionException {
        int result = -1;
        try {
            String session = userLoginMapper.getLoginSessionId(info.getUser_id());
            if(session != null){
                log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Select Successed : {}",req_id, session);
                result = userLoginMapper.updateLoginSession(info);
                if(result==1)
                    log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Update Successed : {}",req_id, result);
                else {
                    log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Update Failed : {}", req_id, result);
                    throw new Exception("Login Session Update Failed  : USER_ID("+info.getUser_id()+")");
                }
            } else{
                log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Select Successed : Previous Session is not exist",req_id);
                result = userLoginMapper.insertLoginSession(info);
                if(result==1)
                    log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Insert Successed : {}",req_id, result);
                else {
                    log.info("[Service-UserLogin][insertLoginSession][{}] Login Session Insert Failed : {}", req_id, result);
                    throw new Exception("USER_ID("+info.getUser_id()+")");
                }
            }
        } catch (Exception e) {
            log.error("[Service-UserLogin][insertLoginSession][{}] Login Session Insert Failed : {}, {}",req_id, result, e.getMessage());
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Login Failed : Exception Occurred");
            resInfo.setRes_data("Login Session Insert Failed  : "+e.getMessage());
            throw new FunctionException("Login Session Insert Failed  : "+e.getMessage());
        }
        return result;
    }
}