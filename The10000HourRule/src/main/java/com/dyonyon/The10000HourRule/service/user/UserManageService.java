package com.dyonyon.The10000HourRule.service.user;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
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
            responseInfo.setRes_data("Duplication Check Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public void isDuplication(String req_id, String key, String value, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = -1;
            switch (key){
                case GlobalConstants.nickname:
                    result = userManageMapper.checkProfileDuplication(key, value);
                    break;
                case GlobalConstants.user_id, GlobalConstants.mail, GlobalConstants.phone:
                    result = userManageMapper.checkAuthDuplication(key, value);
                    break;
                default:
                    throw new Exception("Invalid Key("+key+") : Value("+value+")");
            }

            log.info("[Service-UserManage][checkDuplication][isDuplication][{}] Duplicate Check Successed : Key({}), Value({}), Count({})",req_id,key,value,result);

            if(result>0) {  // 중복
                resInfo.setRes_status("-1");
                resInfo.setMsg("Duplication Check : Duplicated");
                resInfo.setRes_data("Duplicated : Key("+key+") Value("+value+")");
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
            resInfo.setRes_data("Duplication Check Failed : "+e.getMessage());
            throw new FunctionException("Duplication Check Failed : "+e.getMessage());
        }
    }

    public ResponseInfo generateAndSendAuthKey(HttpServletRequest req, UserAuthInfo userAuthInfo){

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");
        String authKey = null;
        try{
            log.info("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generation&Send Started...", req_id);

            // 핸드폰 인증 번호 생성
            if(GlobalConstants.phone.equals(userAuthInfo.getMethod())&&userAuthInfo.getPhone()!=null&&!userAuthInfo.getPhone().isEmpty())
                authKey = "P" + CommonUtil.getAuthKey();
            // 메일 인증 번호 생성
            else if(GlobalConstants.mail.equals(userAuthInfo.getMethod())&&userAuthInfo.getMail()!=null&&!userAuthInfo.getMail().isEmpty())
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
            responseInfo.setRes_data("Auth Key Generate&Send Failed : "+e.getMessage());
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
                case GlobalConstants.phone:
                    to = info.getPhone();
                    break;
                case GlobalConstants.mail:
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
            responseInfo.setRes_data("Auth Key Verify Failed : "+e.getMessage());
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
                    method = GlobalConstants.phone;
                    info.setMethod(method);
                }
                else if(key.contains("M")) {
                    method = GlobalConstants.mail;
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
                throw new FunctionException("Invalid Data OR Expired Key. Result("+result+")");
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

}