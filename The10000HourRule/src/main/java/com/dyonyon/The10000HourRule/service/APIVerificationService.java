package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ContentInfo;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
import com.dyonyon.The10000HourRule.mapper.user.UserLoginMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;


// 로그인 이후, 필요한 공통 서비스
@Service
@Slf4j
public class APIVerificationService {

    private UserLoginMapper userLoginMapper;

    public APIVerificationService(UserLoginMapper userLoginMapper) {
        this.userLoginMapper = userLoginMapper;
    }

    // 세션 유효/만료 체크
    // Status로 로그인 세션 확인 체크 ( 세션 정상 : 1, 세션 비정상(로그인필요) : -1 )
    public ResponseInfo checkLoginSession(String req_id, String user_id, String session_id){

        Date now = new Date();
        Date exp;
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try {
            log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Check Started...", req_id, user_id);

            // 1. 아이디-세션 검색
            String session = getSessionId(req_id, user_id, responseInfo);// ID를 LoginSession TBL에서 검색
            // 1-1. 아이디 세션 결과 있을 때
            if (session != null) {
                log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Select Success : {}(Now) {}(Prev) ", req_id, user_id, session_id, session);

                // 2. 세션 비교
                // 2-1. 세션이 동일할 때
                if (session.equals(session_id)) {

                    // 3. 세션 유효성 검사
                    exp = getSessionEXP(req_id, user_id, responseInfo); // expire_dt를 LoginSession TBL에서 검색
                    log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session EXP Select Success : {}(Now) {}(Exp) ", req_id, user_id, now, exp);
                    // 3-1. expire_dt가 안 지났을 때 = session이 유요할 때
                    if (now.getTime() < exp.getTime()) {
                        log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Is Valid", req_id, user_id);
                        responseInfo.setMsg("Login Session Is Valid");

                        // 4. 세션 만료 시간 업데이트
                        updateLoginSession(req_id, user_id, responseInfo);
                    }
                    // 3-2. expire_dt가 지났을 때 = session이 만료되었을 때
                    else {
                        log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Is Invalid : Session Is Expired", req_id, user_id);
                        responseInfo.setRes_status("-1");
                        responseInfo.setMsg("API Call Fail : Session Is Expired");
                        responseInfo.setRes_data("[Service-APICheck][checkLoginSession] Session Is Expired");
                    }
                }
                // 2-2. 세션이 다를 때
                else {
                    log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Is Invalid : Sessions Are Different", req_id, user_id);
                    responseInfo.setRes_status("-1");
                    responseInfo.setMsg("API Call Fail : Sessions Are Different");
                    responseInfo.setRes_data("[Service-APICheck][checkLoginSession] Sessions Are Different");
                }
            }
            // 1-2. 아이디 세션 결과 없을 때 -> 로그인 필요
            else {
                log.info("[Service-APICheck][checkLoginSession][{}] Login({}) Session Is Invalid : Session Is Not Exist", req_id, user_id);
                responseInfo.setRes_status("-1");
                responseInfo.setMsg("API Call Fail : Session Is Not Exist");
                responseInfo.setRes_data("[Service-APICheck][checkLoginSession] Session Is Not Exist");
            }
        } catch (FunctionException e){
            log.error("[Service-APICheck][checkLoginSession][{}] Login Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-APICheck][checkLoginSession][{}] Login({}) Session Check Fail : ERROR OCCURRED {}",req_id,user_id,e.getMessage());
            log.error("[Service-APICheck][checkLoginSession]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("API Validation Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-APICheck][checkLoginSession] Session Check Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public String getSessionId(String req_id, String user_id, ResponseInfo resInfo) throws FunctionException {
        String result = null;
        try {
            result = userLoginMapper.getLoginSessionId(user_id);
            log.info("[Service-APICheck][checkLoginSession][getSessionId][{}] Login({}) Session Select Success : {}",req_id, user_id, result);
        } catch (Exception e) {
            log.error("[Service-APICheck][checkLoginSession][getSessionId][{}] Login({}) Session Select Fail : {}",req_id, user_id, e.getMessage());
            log.error("[Service-APICheck][checkLoginSession][getSessionId]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("API Validation Fail : Exception Occurred");
            resInfo.setRes_data("[Service-APICheck][checkLoginSession][getSessionId] Select Session ID Fail : " + e.getMessage());
            throw new FunctionException("Select Session ID Fail : "+e.getMessage());
        }
        return result;
    }

    public Date getSessionEXP(String req_id, String user_id, ResponseInfo resInfo) throws FunctionException {
        Date exp = null;
        try {
            exp = userLoginMapper.getLoginSessionEXP(user_id);
            log.info("[Service-APICheck][checkLoginSession][getSessionEXP][{}] Login({}) Session EXP Select Success : {}",req_id, user_id, exp);
        } catch (Exception e) {
            log.error("[Service-APICheck][checkLoginSession][getSessionEXP][{}] Login({}) Session EXP Select Fail : {}",req_id, user_id, e.getMessage());
            log.error("[Service-APICheck][checkLoginSession][getSessionEXP]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("API Validation Fail : Exception Occurred");
            resInfo.setRes_data("[Service-APICheck][checkLoginSession][getSessionEXP] Select Session EXP Fail : " + e.getMessage());
            throw new FunctionException("Select Session EXP Fail : "+e.getMessage());
        }
        return exp;
    }

    // 세션 UPDATE_DT, EXPIRE_DT UPDATE
    public void updateLoginSession(String req_id, String user_id, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = userLoginMapper.updateSessionTime(user_id);
            if(result>0)
                log.info("[Service-APICheck][checkLoginSession][updateLoginSession][{}] Login({}) Session EXP Update Success : {}",req_id, user_id, result);
            else
                throw new Exception("Result("+result+")");
        } catch (Exception e) {
            log.error("[Service-APICheck][checkLoginSession][updateLoginSession][{}] Login({}) Session EXP Update Fail : {}",req_id, user_id, e.getMessage());
            log.error("[Service-APICheck][checkLoginSession][updateLoginSession]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("API Call Fail : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-APICheck][checkLoginSession][updateLoginSession] Session Update Fail : " + e.getMessage());
            throw new FunctionException("Session Update Fail : "+e.getMessage());
        }
    }

    // 서약 동의 여부

    // 핸드폰/이메일 인증 여부

    // 권한 검증
    public ResponseInfo verifyAuthority(String req_id, ContentInfo verifyInfo) {

        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try {
            log.info("[Service-APICheck][verifyAuthority][{}] Authority Verify Started...", req_id);

            // 데이터 존재 여부
            checkParams(req_id, verifyInfo, responseInfo);

            // 권한 인증
            verify(req_id, verifyInfo, responseInfo);

        } catch (FunctionException e){
            log.error("[Service-APICheck][verifyAuthority][{}] Authority Verify Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-APICheck][verifyAuthority][{}] Authority Verify Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-APICheck][verifyAuthority]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("API Validation Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-APICheck][verifyAuthority] Authority Verify Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public void checkParams(String req_id, ContentInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String service = info.getService(); String user_id = info.getUser_id(); String owner_id = info.getOwner_id(); String type = info.getContent_type();
            if(user_id!=null&&owner_id!=null&&service!=null&&type!=null){
                log.info("[Service-APICheck][verifyAuthority][checkParams][{}] Check Parameters : Service({}) Type({}) User({}) Owner({})", req_id, service, type, user_id, owner_id);
            }
            else{
                log.info("[Service-APICheck][verifyAuthority][checkParams][{}] Required Data Is Missing : Service({}) Type({}) User({}) Owner({})", req_id, service, type, user_id, owner_id);
                resInfo.setRes_status("-1");
                resInfo.setMsg("API Validation Fail : Required Data Is Missing");
                resInfo.setRes_data("[Service-APICheck][verifyAuthority][checkParams] Required Data Is Missing : Service({}) Type({}) User({}) Owner({})");
                throw new FunctionException("Required Data Is Missing");
            }
        } catch (Exception e) {
            log.error("[Service-APICheck][verifyAuthority][checkParams][{}] Check Parameters Fail : {}",req_id, e.getMessage());
            log.error("[Service-APICheck][verifyAuthority][checkParams]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("API Validation Fail : Exception Occurred");
            resInfo.setRes_data("[Service-APICheck][verifyAuthority][checkParams] Check Parameters Fail : " + e.getMessage());
            throw new FunctionException("Check Parameters Fail : "+e.getMessage());
        }
    }

    // 권한 검증(대상 Content ID가 없을 때)
    public void verify(String req_id, ContentInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String service = info.getService(); String user_id = info.getUser_id(); String owner_id = info.getOwner_id(); String type = info.getContent_type();
            // content_type 이 개인(U)일때
            if(GlobalConstants.content_type_user.equals(type)){
                // user 와 owner 가 동일 => 권한 있음
                if(user_id.equals(owner_id)) {
                    log.info("[Service-APICheck][verifyAuthority][verify][{}] Authorized : {}({}) User({}) = Owner({})", req_id, service, type, user_id, owner_id);
                }
                // user 와 owner 가 다를 때 => 권한 없음
                else{
                    log.info("[Service-APICheck][verifyAuthority][verify][{}] Not Authorized : {}({}) User({}) != Owner({})", req_id, service, type, user_id, owner_id);
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("API Validation Fail : Not Authorized");
                    resInfo.setRes_data("[Service-APICheck][verifyAuthority][verify] Not Authorized : User("+user_id+") != Owner("+owner_id+")");
                    throw new FunctionException("Not Authorized");
                }
            }
            // content_type 이 그룹(G)일때
            else if(GlobalConstants.content_type_group.equals(type)){
                // Group 의 Owner 인지 확인

                // Group 의 Mem
                // group_idx 에 대해 user 와 owner 가 동일 => (자신이 그룹장 일 때) 권한 있음
                if(user_id.equals(owner_id)) {
                    log.info("[Service-APICheck][verifyAuthority][verify][{}] Authorized : {}({}) User({}) = Owner({})", req_id, service, type, user_id, owner_id);
                }
                // user 와 owner 가 다를 때
                else{
                    // owner(그룹)에 user 의 권한 확인
                    // user 가 owner (그룹)에 속하며 권한이 있을 때

                    // user 가 owner (그룹)에 속하지 않거나 권한이 없을 때
//
//                    log.info("[Service-APICheck][verifyAuthority][verify][{}] Not Authorized : {}({}) User({}) != Owner({})", req_id, service, type, user_id, owner_id);
//                    resInfo.setRes_status("-1");
//                    resInfo.setMsg("API Validation Fail : Not Authorized");
//                    resInfo.setRes_data("[Service-APICheck][verifyAuthority][verify] Not Authorized : User("+user_id+") Does Not Have Authority About "+service+"("++")");
                    throw new FunctionException("Unknown Content_type");
                }
            }
            // content_type 이 unknown 일 때
            else{
                log.info("[Service-APICheck][verifyAuthority][verify][{}] Not Authorized : Unknown Content_type({})", req_id, type);
                resInfo.setRes_status("-1");
                resInfo.setMsg("API Validation Fail : Not Authorized");
                resInfo.setRes_data("[Service-APICheck][verifyAuthority][verify] Not Authorized : Unknown Content_type("+type+")");
                throw new FunctionException("Unknown Content_type");
            }
        } catch (Exception e) {
            log.error("[Service-APICheck][verifyAuthority][checkParams][{}] Check Parameters Fail : {}",req_id, e.getMessage());
            log.error("[Service-APICheck][verifyAuthority][checkParams]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("API Validation Fail : Exception Occurred");
            resInfo.setRes_data("[Service-APICheck][verifyAuthority][checkParams] Check Parameters Fail : " + e.getMessage());
            throw new FunctionException("Check Parameters Fail : "+e.getMessage());
        }
    }

    // 권한 검증(대상 Content ID가 있을 때)
    // 각 content follow 확인

}
