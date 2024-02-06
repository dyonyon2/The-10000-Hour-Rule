package com.dyonyon.The10000HourRule.service.user;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.code.ResultCode;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginInfo;
import com.dyonyon.The10000HourRule.mapper.user.UserLoginMapper;
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
                responseInfo.setMsg("Login Fail : Log Attempts Exceeded");
                responseInfo.setRes_data("[Service-UserLogin][login] Log Attempts Exceeded");
                responseInfo.setRes_status("-1");
                updateLogStatus(req_id, ResultCode.USER_LOGIN_ATTEMPT_OVER_ERROR, responseInfo, true);
            }
            // 2-2. 로그인 시도 횟수가 기준값 미만일 때 -> 3. 로그인 체크 진행
            else {

                // 3. 로그인 체크
                UserDetailInfo loginUserInfo = checkLogin(req_id, userLoginInfo, responseInfo);

                // 3-1. 로그인 성공 (로그인 요청시 입력한 USER_ID와, USER 테이블 정보의 USER_ID가 같으면 로그인 정보 일치한 것)
                if (loginUserInfo != null) {
                    // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                    updateLogStatus(req_id, ResultCode.USER_LOGIN_SUCCESS, responseInfo, false);

                    // 5. 로그인 정보 테이블(USER_LOGIN_LOG) Flag update
                    resetLoginAttempt(req_id, loginUserInfo.getUser_id(), responseInfo);

                    // 6. 로그인 세션 정보 테이블(USER_SESSION) Insert
                    insertLoginSession(req_id, userLoginInfo, responseInfo);

                    log.info("[Service-UserLogin][login][{}] Login Success...: ID({})", req_id, userLoginInfo.getUser_id());
                }
                // 3-2. 로그인 실패 -> 4.2 로그인 로그 업데이트
                else {
                    // 4. 로그인 로그 정보 테이블(USER_LOGIN_LOG) 상태값 update
                    updateLogStatus(req_id, ResultCode.USER_LOGIN_FAIL, responseInfo, true);
                }
            }
        } catch (FunctionException e){
            log.error("[Service-UserLogin][login][{}] Login Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserLogin][login][{}] Login Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-UserLogin][login]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Login Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-UserLogin][login] Login Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    // Insert Return 값이 1이면 정상, 그 이외는 비정상
    public void insertLoginLog(String req_id, UserLoginInfo info, ResponseInfo resInfo) throws FunctionException {
        int result = -1;
        try {
            result = userLoginMapper.insertLoginLog(info);
            if(result == 1){
                log.info("[Service-UserLogin][Login][insertLoginLog][{}] Login Log Insert Success : ID({})",req_id, info.getUser_id());
            } else {    // 오류, 실패
                throw new Exception("Result("+result+")");
            }
        } catch (Exception e) { // 오류, 실패
            log.error("[Service-UserLogin][Login][insertLoginLog][{}] Login Log Insert Fail : ID({}) {}, {}",req_id,info.getUser_id(), result,e.getMessage());
            log.error("[Service-UserLogin][login][insertLoginLog]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("Login Fail : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserLogin][Login][insertLoginLog] Login Log Insert Fail : "+e.getMessage());
            throw new FunctionException("Login Log Insertion Fail : "+e.getMessage());
        }
    }

    public void updateLogStatus(String req_id, String status, ResponseInfo resInfo, boolean isError) throws FunctionException {
        try{
            int result = userLoginMapper.updateStatus(req_id, status);
            if(result > 0){
                log.info("[Service-UserLogin][login][updateLogStatus][{}] Login Log Status({}) Update Success : {}",req_id,status,result);
            } else {
                throw new Exception("Result("+result+")");
            }
        } catch (Exception e){
            log.error("[Service-UserLogin][login][updateLogStatus][{}] Login Log Status({}) Update Fail : {}", req_id, status, e.getMessage());
            log.error("[Service-UserLogin][login][updateLogStatus]["+req_id+"] Error PrintStack : ",e);
            if(!isError) {
                resInfo.setStatus("-1");
                resInfo.setRes_status("-1");
                resInfo.setMsg("Login Fail : Exception Occurred");
                resInfo.setRes_data("[Service-UserLogin][login][updateLogStatus] Login Log Status Update Fail : " + e.getMessage());
                throw new FunctionException("Login Log Insertion Fail : "+e.getMessage());
            }
        }
    }

    public int checkLoginAttempt(String req_id, UserLoginInfo info, ResponseInfo resInfo) throws FunctionException {
        int result = 0;
        try {
            result = userLoginMapper.getLoginAttempt(info);
            if(result >= 0){
                log.info("[Service-UserLogin][login][checkLoginAttempt][{}] Login Attempt Select Success : {}",req_id,result);
            } else {
                throw new Exception("Result("+result+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserLogin][login][checkLoginAttempt][{}] Login Attempt Select Fail : {}, {}",req_id,result,e.getMessage());
            log.error("[Service-UserLogin][login][checkLoginAttempt]["+req_id+"] Error PrintStack : ",e);
            resInfo.setRes_data("[Service-UserLogin][login][checkLoginAttempt] Login Attempt Select Fail : "+e.getMessage());
            resInfo.setMsg("Login Fail : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            throw new FunctionException("Login Attempt Select Fail : "+e.getMessage());
        }
        return result;
    }

    public UserDetailInfo checkLogin(String req_id, UserLoginInfo info, ResponseInfo resInfo) throws FunctionException {
        UserDetailInfo result = null;
        try {
            result = userLoginMapper.getLoginResult(info);
            if(result!=null) {
                log.info("[Service-UserLogin][login][getLoginResult][{}] Login Success : {}", req_id, result);
                resInfo.setMsg("Login Success");
                resInfo.setRes_data(result);
                info.setUser_idx(result.getUser_idx());
            }
            else {
                log.info("[Service-UserLogin][login][getLoginResult][{}] Login Fail (Invalid ID/PW) ID({}) PW({})", req_id, info.getUser_id(), info.getPw());
                resInfo.setMsg("Login Fail : Invalid ID/PW");
                resInfo.setRes_status("-1");
                resInfo.setRes_data("[Service-UserLogin][login][getLoginResult] Invalid ID/PW : ID("+info.getUser_id()+") PW("+info.getPw()+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserLogin][login][getLoginResult][{}] Login Fail (SQL Fail) ID({}) PW({}) : {}", req_id, info.getUser_id(), info.getPw(), e.getMessage());
            log.error("[Service-UserLogin][login][getLoginResult]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("Login Fail : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserLogin][login][getLoginResult] Login ID/PW Select Fail : "+e.getMessage());
            throw new FunctionException("Login ID/PW Select Fail : "+e.getMessage());
        }
        return result;
    }

    public void resetLoginAttempt(String req_id, String user_id, ResponseInfo resInfo) throws FunctionException {
        try{
            int result = userLoginMapper.resetLoginAttempt(user_id);
            if(result >= 0){
                log.info("[Service-UserLogin][login][resetLoginAttempt][{}] Login ID({}) Log Flag Reset Success : {}",req_id, user_id,result);
            } else {
                throw new Exception("Result("+result+")");
            }
        } catch (Exception e){
            log.error("[Service-UserLogin][login][resetLoginAttempt][{}] Login ID({}) Log Flag Reset Fail : {}",req_id, user_id,e.getMessage());
            log.error("[Service-UserLogin][login][resetLoginAttempt]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("Login Fail : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserLogin][login][resetLoginAttempt] Login Attempt Reset Fail : "+e.getMessage());
            throw new FunctionException("Login Attempt Reset Fail : "+e.getMessage());
        }
    }

    // Login Session TBL에서 기존 로그인 세션 select
    // 기록이 있다면 update, 없다면 insert
    public void insertLoginSession(String req_id, UserLoginInfo info, ResponseInfo resInfo) throws FunctionException {
        int result = -1;
        try {
            String session = userLoginMapper.getLoginSessionId(info.getUser_id());
            if(session != null){
                log.info("[Service-UserLogin][login][insertLoginSession][{}] Login Session Select Success : {}",req_id, session);
                result = userLoginMapper.updateLoginSession(info);
                if(result==1)
                    log.info("[Service-UserLogin][login][insertLoginSession][{}] Login Session Update Success : {}",req_id, result);
                else {
                    log.info("[Service-UserLogin][login][insertLoginSession][{}] Login Session Update Fail : {}", req_id, result);
                    throw new Exception("USER_ID("+info.getUser_id()+")");
                }
            } else{
                log.info("[Service-UserLogin][login][insertLoginSession][{}] Login Session Select Success : Previous Session is not exist",req_id);
                result = userLoginMapper.insertLoginSession(info);
                if(result==1)
                    log.info("[Service-UserLogin][login][insertLoginSession][{}] Login Session Insert Success : {}",req_id, result);
                else {
                    log.info("[Service-UserLogin][login][insertLoginSession][{}] Login Session Insert Fail : {}", req_id, result);
                    throw new Exception("USER_ID("+info.getUser_id()+")");
                }
            }
        } catch (Exception e) {
            log.error("[Service-UserLogin][login][insertLoginSession][{}] Login Session Insert Fail : {}, {}",req_id, result, e.getMessage());
            log.error("[Service-UserLogin][login][insertLoginSession]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Login Fail : Exception Occurred");
            resInfo.setRes_data("[Service-UserLogin][login][insertLoginSession] Login Session Insert Fail  : "+e.getMessage());
            throw new FunctionException("Login Session Insert/Update Fail  : "+e.getMessage());
        }
    }
}