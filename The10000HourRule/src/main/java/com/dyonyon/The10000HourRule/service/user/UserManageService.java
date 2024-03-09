package com.dyonyon.The10000HourRule.service.user;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.mapper.user.UserManageMapper;
import com.dyonyon.The10000HourRule.util.CommonUtil;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserManageService {

    private UserManageMapper userManageMapper;

    @Value("${spring.mail.username}")
    String user;
    @Value("${auth.title}")
    String title;
    @Value("${auth.content}")
    String content;

    @Autowired
    private JavaMailSender javaMailSender;

    public UserManageService(UserManageMapper userManageMapper){
        this.userManageMapper = userManageMapper;
    }


    // 유저(아이디, 닉네임, 이메일, 핸드폰) 중복 확인 기능 : isDuplication(중복 확인 함수)
    public ResponseInfo checkDuplication(HttpServletRequest req, String key, String value) {
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        // 1. 중복 확인
        try{
            log.info("[Service-UserManage][checkDuplication][{}] Check Started... : key({}) value({})", req_id, key, value);

            isDuplication(req_id, key, value, responseInfo);

        } catch (FunctionException e){
            log.error("[Service-UserManage][checkDuplication][{}] Check Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserManage][checkDuplication][{}] Check Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-UserManage][checkDuplication]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Duplication Check Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-UserManage][checkDuplication] Duplication Check Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void isDuplication(String req_id, String key, String value, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = -1;
            switch (key){
                case GlobalConstants.NICKNAME:
                    result = userManageMapper.checkProfileDuplication(key, value);
                    break;
                case GlobalConstants.USER_ID, GlobalConstants.MAIL, GlobalConstants.PHONE:
                    result = userManageMapper.checkAuthDuplication(key, value);
                    break;
                default:
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Duplication Check : Invalid Key");
                    resInfo.setRes_data("[Service-UserManage][checkDuplication][isDuplication] Invalid Key("+key+") : Value("+value+")");
                    throw new FunctionException("Invalid Key("+key+") : Value("+value+")");
            }

            log.info("[Service-UserManage][checkDuplication][isDuplication][{}] Duplicate Check Success : Key({}), Value({}), Count({})",req_id,key,value,result);

            if(result>0) {  // 중복
                resInfo.setRes_status("-1");
                resInfo.setMsg("Duplication Check : Duplicated");
                resInfo.setRes_data("[Service-UserManage][checkDuplication][isDuplication] Duplicated : Key("+key+") Value("+value+")");
            } else if (result < 0) {    // 에러
                throw new Exception("Result(" + result + ")");
            } else {    // 중복 X
                resInfo.setMsg("Duplication Check : Not Duplicated");
                resInfo.setRes_data("Not Duplicated : Key("+key+") Value("+value+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][checkDuplication][isDuplication][{}] Duplication Check Select Failed : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][checkDuplication][isDuplication]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Duplication Check Failed : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][checkDuplication][isDuplication] Duplication Check Select Failed : "+e.getMessage());
            throw new FunctionException("Duplication Check Fail : "+e.getMessage());
        }
    }


    // Auth Key 생성 및 전송(이메일) 기능 : updateAuthKey(Auth Key 등록 함수) -> sendAuthKey(Auth Key 전송 함수)
    public ResponseInfo generateAndSendAuthKey(HttpServletRequest req, UserAuthInfo userAuthInfo){

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");
        String authKey = null;
        try{
            log.info("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generation&Send Started...", req_id);

            // 핸드폰 인증 번호 생성
            if(GlobalConstants.PHONE.equals(userAuthInfo.getMethod())&&userAuthInfo.getPhone()!=null&&!userAuthInfo.getPhone().isEmpty())
                authKey = "P" + CommonUtil.getAuthKey();
            // 메일 인증 번호 생성
            else if(GlobalConstants.MAIL.equals(userAuthInfo.getMethod())&&userAuthInfo.getMail()!=null&&!userAuthInfo.getMail().isEmpty())
                authKey = "M" + CommonUtil.getAuthKey();
            // 그 외의 요청 에러
            else {
                responseInfo.setRes_status("-1");
                responseInfo.setMsg("Auth Key Generation Fail : Invalid Method/Data");
                responseInfo.setRes_data("[Service-UserManage][generateAndSendAuthKey] Auth Key Generate Fail : Invalid Method("+userAuthInfo.getMethod()+")/Mail("+userAuthInfo.getMail()+")/Phone("+userAuthInfo.getPhone()+")");
                log.info("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generate Fail : Invalid Method({})/Mail({})/Phone({}})",req_id, userAuthInfo.getMethod(), userAuthInfo.getMail(), userAuthInfo.getPhone());
            }

            if(authKey!=null){
                userAuthInfo.setKey(authKey);
                // 인증 번호 DB Insert
                updateAuthKey(req_id, userAuthInfo, responseInfo);

                sendAuthKey(req_id, userAuthInfo, responseInfo);

                log.info("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generate&Send Success...: ID({})", req_id, userAuthInfo.getUser_id());
            }
        } catch (FunctionException e){
            log.error("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generate&Send Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generate&Send Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-UserManage][generateAndSendAuthKey]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Auth Key Generate&Send Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-UserManage][generateAndSendAuthKey] Auth Key Generate&Send Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void updateAuthKey(String req_id, UserAuthInfo info, ResponseInfo resInfo) throws FunctionException{
        int result = -1;
        try {
            result = userManageMapper.updateAuthKey(info);
            if(result == 1){
                log.info("[Service-UserManage][generateAndSendAuthKey][insertAuthKey][{}] Auth Key Insert Success : ID({}) Key({})",req_id, info.getUser_id(),info.getKey());
            } else {
                throw new Exception("Result("+result+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][generateAndSendAuthKey][insertAuthKey][{}] Auth Key Insert Fail : ID({}) {}, {}",req_id,info.getUser_id(), result,e.getMessage());
            log.error("[Service-UserManage][generateAndSendAuthKey][insertAuthKey]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("Auth Key Generate&Send Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserManage][generateAndSendAuthKey][insertAuthKey] Auth Key Insert Fail : "+e.getMessage());
            throw new FunctionException("Auth Key Insert Fail : "+e.getMessage());
        }
    }
    public void sendAuthKey(String req_id, UserAuthInfo info, ResponseInfo resInfo) throws FunctionException{
        try{
            String to;
            switch (info.getMethod()){
                case GlobalConstants.PHONE:
                    to = info.getPhone();
                    break;
                case GlobalConstants.MAIL:
                    to = info.getMail();
                    MimeMessage m = javaMailSender.createMimeMessage();
                    MimeMessageHelper h = new MimeMessageHelper(m,"UTF-8");
                    h.setFrom(user);
                    h.setTo(to);
                    h.setSubject(title);
                    h.setText(content+info.getKey());
                    javaMailSender.send(m);

                    break;
                default:
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Auth Key Generate&Send Failed : Invalid Method("+info.getMethod()+")");
                    resInfo.setRes_data("[Service-UserManage][generateAndSendAuthKey][sendAuthKey] Auth Key Send Fail : Invalid Method("+info.getMethod()+")");
                    throw new FunctionException("Auth Key Send Fail : Invalid Method("+info.getMethod()+")");
            }
            resInfo.setMsg("Auth Key Generate&Send Success");
            resInfo.setRes_data(info);
            log.info("[Service-UserManage][generateAndSendAuthKey][sendAuthKey][{}] Auth Key Send Success : ID({}) Method({}) Key({})",req_id, info.getUser_id(),info.getMethod(), info.getKey());
        } catch (Exception e){
            log.error("[Service-UserManage][generateAndSendAuthKey][sendAuthKey][{}] Auth Key Send Fail : ID({}) Method({}) Key({}), {}",req_id,info.getUser_id(), info.getMethod(),info.getKey(), e.getMessage());
            log.error("[Service-UserManage][generateAndSendAuthKey][sendAuthKey]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("Auth Key Generate&Send Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserManage][generateAndSendAuthKey][sendAuthKey] Auth Key Send Fail : "+e.getMessage());
            throw new FunctionException("Auth Key Send Fail : "+e.getMessage());
        }
    }


    // Auth Key 검증 기능 : checkKeyValidation(Auth Key 유효성 확인 함수) -> verify(Auth Key 검증 함수)
    public ResponseInfo verifyAuthKey(HttpServletRequest req, UserAuthInfo userAuthInfo){

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");
        String method;
        try{
            log.info("[Service-UserManage][verifyAuthKey][{}] Auth Key Verify Started...", req_id);

            // Key Method 확인
            checkKeyValidation(req_id, userAuthInfo, responseInfo);

            // Auth Key 인증
            verify(req_id, userAuthInfo, responseInfo);

            log.info("[Service-UserManage][verifyAuthKey][{}] Auth Key Verify Success...", req_id);
        } catch (FunctionException e){
            log.error("[Service-UserManage][verifyAuthKey][{}] Auth Key Verify Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserManage][verifyAuthKey][{}] Auth Key Verify Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-UserManage][verifyAuthKey]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Auth Key Verify Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-UserManage][verifyAuthKey] Auth Key Verify Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void checkKeyValidation(String req_id, UserAuthInfo info, ResponseInfo resInfo) throws FunctionException{
        String key = null;
        String method = null;
        try {
            key = info.getKey();

            if(key!=null&&(!key.isEmpty())) {
                if(key.contains("P")) {
                    method = GlobalConstants.PHONE;
                    info.setMethod(method);
                }
                else if(key.contains("M")) {
                    method = GlobalConstants.MAIL;
                    info.setMethod(method);
                }
                else {
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Auth Key Validate Failed : Invalid Key(" + key + ")");
                    resInfo.setRes_data("[Service-UserManage][verifyAuthKey][checkKeyValidation] Auth Key Validate Fail : Invalid Key(" + key + ")");
                    log.info("[Service-UserManage][verifyAuthKey][checkKeyValidation][{}] Auth Key Validate Fail : Invalid Key. ID({}) Method({}) Key({})",req_id, info.getUser_id(),info.getMethod(), info.getKey());
                    throw new FunctionException("Auth Key Validate Fail : Invalid Key(" + key + ")");
                }
            }
            else {
                resInfo.setRes_status("-1");
                resInfo.setMsg("Auth Key Validate Failed : Invalid Key(" + key + ")");
                resInfo.setRes_data("[Service-UserManage][verifyAuthKey][checkKeyValidation] Auth Key Validate Fail : Invalid Key(" + key + ")");
                log.info("[Service-UserManage][verifyAuthKey][checkKeyValidation][{}] Auth Key Validate Fail : Invalid Key. ID({}) Method({}) Key({})",req_id, info.getUser_id(),info.getMethod(), info.getKey());
                throw new FunctionException("Auth Key Validate Fail : Invalid Key(" + key + ")");
            }
            log.info("[Service-UserManage][verifyAuthKey][checkKeyValidation][{}] Auth Key Validate Success : ID({}) Method({}) Key({})",req_id, info.getUser_id(),info.getMethod(), info.getKey());
        } catch (Exception e) {
            log.error("[Service-UserManage][verifyAuthKey][checkKeyValidation][{}] Auth Key Validate Fail : ID({}) Key({}) {}",req_id,info.getUser_id(), info.getKey(), e.getMessage());
            log.error("[Service-UserManage][verifyAuthKey][checkKeyValidation]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("Auth Key Validate Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserManage][verifyAuthKey][checkKeyValidation] Auth Key Validate Fail : "+e.getMessage());
            throw new FunctionException("Auth Key Validate Fail : "+e.getMessage());
        }
    }
    public void verify(String req_id, UserAuthInfo info, ResponseInfo resInfo) throws FunctionException{
        int result = -1;
        // User_id, Key가 동일해야하며, KEY_EXPIRED_TIME이 지나면 오류로 처리
        try {
            result = userManageMapper.verifyAuthKey(info);
            if(result == 1){
                log.info("[Service-UserManage][verifyAuthKey][verify][{}] Auth Key Verify Success : ID({}) Key({})",req_id, info.getUser_id(),info.getKey());
                resInfo.setMsg("Auth Key Verify Success");
                resInfo.setRes_data(info);
            } else {
                resInfo.setRes_status("-1");
                resInfo.setMsg("Auth Key Verify Failed : Invalid Data OR Expired Key");
                resInfo.setRes_data("[Service-UserManage][verifyAuthKey][verify] Auth Key Verify Fail : Invalid Data OR Expired Key");
                log.info("[Service-UserManage][verifyAuthKey][checkKeyValidation][{}] Auth Key Validate Fail : Invalid Data OR Expired Key. ID({}) Method({}) Key({})",req_id, info.getUser_id(),info.getMethod(), info.getKey());
                throw new FunctionException("Auth Key Verify Fail : Invalid Data OR Expired Key. Result("+result+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][verifyAuthKey][verify][{}] Auth Key Verify Fail : ID({}) Key({}) {}, {}",req_id,info.getUser_id(), info.getKey(), result,e.getMessage());
            log.error("[Service-UserManage][verifyAuthKey][verify]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("Auth Key Verify Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserManage][verifyAuthKey][verify] Auth Key Verify Fail : "+e.getMessage());
            throw new FunctionException("Auth Key Verify Fail : "+e.getMessage());
        }
    }


    // 유저 정보(닉네임, 비번, 이메일, 핸드폰) 변경 기능 : isDuplication2(중복 확인 함수) -> updateUserInfo(정보 업데이트 함수)
    public ResponseInfo changeUserInfo(HttpServletRequest req, UserDetailInfo userDetailInfo){

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");
        String key=null; String value=null;
        try{
            log.info("[Service-UserManage][changeUserInfo][{}] User Info Change Started...", req_id);

            // 1. 변경 대상 체크
            // 2. 유효성 체크
            // 3. 중복 확인
            // 4. 변경

            // 1. 변경 대상 체크
            // 1-1. 닉네임 체크
            if(userDetailInfo.getNickname()!=null&&!userDetailInfo.getNickname().isEmpty()){
                key = GlobalConstants.NICKNAME;
                value = userDetailInfo.getNickname();
            }
            // 1-2. 비번 체크
            else if(userDetailInfo.getPw()!=null&&!userDetailInfo.getPw().isEmpty()){
                key = GlobalConstants.PW;
                value = userDetailInfo.getPw();
            }
            // 1-3. 메일 체크
            else if(userDetailInfo.getMail()!=null&&!userDetailInfo.getMail().isEmpty()){
                key = GlobalConstants.MAIL;
                value = userDetailInfo.getMail();
            }
            // 1-4. 핸드폰 체크
            else if(userDetailInfo.getPhone()!=null&&!userDetailInfo.getPhone().isEmpty()){
                key = GlobalConstants.PHONE;
                value = userDetailInfo.getPhone();
            }
            else{
                responseInfo.setRes_status("-1");
                responseInfo.setMsg("User Info Change Failed : Empty Input Data");
                responseInfo.setRes_data("[Service-UserManage][changeUserInfo] User Info Change Failed : Empty Input Data (nickname,pw,mail,phone)");
                throw new FunctionException("Empty Input Data");
            }

            // 2. 유효성 체크
            if(!CommonUtil.isFormat(key, value)){
                responseInfo.setRes_status("-1");
                responseInfo.setMsg("User Info Change Failed : Invalid Format");
                responseInfo.setRes_data("[Service-UserManage][changeUserInfo] User Info Change Failed : Invalid Format Key("+key+") Value("+value+")");
                throw new FunctionException("Invalid Format Key("+key+") Value("+value+")");
            }

            // 3. 중복 확인
            isDuplication2(req_id, key, value, userDetailInfo.getUser_id(), responseInfo);

            // 4. 변경
            updateUserInfo(req_id, key, value, userDetailInfo.getUser_id(), responseInfo);

            log.info("[Service-UserManage][changeUserInfo][{}] User Info Change Success...", req_id);
        } catch (FunctionException e){
            log.error("[Service-UserManage][changeUserInfo][{}] User Info Change Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserManage][changeUserInfo][{}] User Info Change : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-UserManage][changeUserInfo]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("User Info Change Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-UserManage][changeUserInfo] User Info Change Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void isDuplication2(String req_id, String key, String value, String user_id, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = -1;
            switch (key){
                case GlobalConstants.NICKNAME:
                    result = userManageMapper.checkProfileDuplication(key, value);
                    break;
                case GlobalConstants.MAIL, GlobalConstants.PHONE:
                    result = userManageMapper.checkAuthDuplication(key, value);
                    break;
                case GlobalConstants.PW:
                    result = userManageMapper.checkPwDuplication(key, value, user_id);
                    break;
                default:
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Duplication Check : Invalid Key");
                    resInfo.setRes_data("[Service-UserManage][changeUserInfo][isDuplication2] Invalid Key("+key+") : Value("+value+")");
                    throw new FunctionException("Duplication Check Fail: Invalid Key("+key+") : Value("+value+")");
            }

            log.info("[Service-UserManage][changeUserInfo][isDuplication2][{}] Duplicate Check Success : Key({}), Value({}), Count({})",req_id,key,value,result);

            if(result>0) {  // 중복
                resInfo.setRes_status("-1");
                resInfo.setMsg("User Info Change Failed : Duplicated Value");
                resInfo.setRes_data("[Service-UserManage][changeUserInfo][isDuplication2] Duplicated : Key("+key+") Value("+value+")");
                if(GlobalConstants.PW.equals(key)){
                    resInfo.setMsg("User Info Change Failed : Same PW As Prev PW");
                    resInfo.setRes_data("[Service-UserManage][changeUserInfo][isDuplication2] Duplicated : Same PW As Prev PW Key("+key+") Value("+value+")");
                }
                throw new FunctionException("Duplication Check : Duplicated Key("+key+") Value("+value+")");
            } else if (result < 0) {    // 에러
                throw new Exception("Result(" + result + ")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][changeUserInfo][isDuplication2][{}] Duplication Check Select Fail : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][changeUserInfo][isDuplication2]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("User Info Change Failed : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][changeUserInfo][isDuplication2] Duplication Check Select Fail : "+e.getMessage());
            throw new FunctionException("Duplication Check Select Fail : "+e.getMessage());
        }
    }
    public void updateUserInfo(String req_id, String key, String value, String user_id, ResponseInfo resInfo) throws FunctionException {
        int result = -1;
        try {
            switch (key){
                case GlobalConstants.NICKNAME: // 정보 변경
                    result = userManageMapper.updateProfile(key, value, user_id);
                    break;
                case GlobalConstants.PW, GlobalConstants.MAIL, GlobalConstants.PHONE: // 정보 변경 & 인증 상태 변경 (핸드폰, 메일)
                    result = userManageMapper.updateAuth(key, value, user_id);
                    break;
                default:
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("User Info Update : Invalid Key");
                    resInfo.setRes_data("[Service-UserManage][changeUserInfo][updateUserInfo] Invalid Key("+key+") : Value("+value+")");
                    throw new FunctionException("User Info Update Fail : Invalid Key("+key+") : Value("+value+")");
            }

            if(result==1) {
                log.info("[Service-UserManage][changeUserInfo][updateUserInfo][{}] User Info Update Success : Key({}), Value({}), Count({})",req_id,key,value,result);
                resInfo.setMsg("User Info Update : Success");
            } else {
                throw new Exception("Result(" + result + ")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][changeUserInfo][updateUserInfo][{}] User Info Update Fail : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][changeUserInfo][updateUserInfo]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("User Info Update Failed : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][changeUserInfo][updateUserInfo] User Info Update Fail : "+e.getMessage());
            throw new FunctionException("UserInfo Update Failed : "+e.getMessage());
        }

    }
}