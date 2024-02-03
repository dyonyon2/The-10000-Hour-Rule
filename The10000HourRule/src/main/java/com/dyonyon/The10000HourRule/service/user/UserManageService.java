package com.dyonyon.The10000HourRule.service.user;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginInfo;
import com.dyonyon.The10000HourRule.mapper.user.UserManageMapper;
import com.dyonyon.The10000HourRule.mapper.user.UserSignupMapper;
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
            log.info("[Service-UserManage][checkDuplication][isDuplication][{}] Duplication Check Select Failed : {}",req_id,e.getMessage());
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
        String authKey;
        try{
            log.info("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generation&Send Started...", req_id);

            // 핸드폰 인증 번호 생성
            if(GlobalConstants.phone.equals(userAuthInfo.getMethod()))
                authKey = "P"+CommonUtil.getAuthKey();
                // 메일 인증 번호 생성
            else if(GlobalConstants.mail.equals(userAuthInfo.getMethod()))
                authKey = "M"+CommonUtil.getAuthKey();
                // 그 외의 요청 에러
            else {
                responseInfo.setMsg("Auth Key Generation Fail : Invalid Method");
                responseInfo.setStatus("1");
                responseInfo.setRes_status("-1");
                responseInfo.setRes_data("[Service-UserManage][generateAndSendAuthKey] Auth Key Generate Fail : Invalid Method " + userAuthInfo.getMethod());
                throw new FunctionException("Auth Key Generation Fail : Invalid Method " + userAuthInfo.getMethod());
            }

            userAuthInfo.setKey(authKey);
            // 인증 번호 DB Insert
            insertAuthKey(req_id, userAuthInfo, responseInfo);

            sendAuthKey(req_id, userAuthInfo, responseInfo);

            log.info("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generate&Send Success...: ID({})", req_id, userAuthInfo.getUser_id());
        } catch (FunctionException e){
            log.error("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generate&Send Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserManage][generateAndSendAuthKey][{}] Auth Key Generate&Send Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Auth Key Generate&Send Failed : Exception Occurred");
            responseInfo.setRes_data("Auth Key Generate&Send Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public void insertAuthKey(String req_id, UserAuthInfo info, ResponseInfo resInfo) throws FunctionException{
        int result = -1;
        try {
            result = userManageMapper.insertAuthKey(info);
            if(result == 1){
                log.info("[Service-UserManage][generateAndSendAuthKey][insertAuthKey][{}] Auth Key Insert Success : ID({}) Key({})",req_id, info.getUser_id(),info.getKey());
            } else {
                throw new Exception("Result("+result+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][generateAndSendAuthKey][insertAuthKey][{}] Auth Key Insert Fail : ID({}) {}, {}",req_id,info.getUser_id(), result,e.getMessage());
            resInfo.setMsg("Auth Key Generate&Send Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserManage][generateAndSendAuthKey][insertAuthKey] Auth Key Insert Fail : "+e.getMessage());
            throw new FunctionException("Auth Key Insert Fail : "+e.getMessage());
        }
    }

    public void sendAuthKey(String req_id, UserAuthInfo info, ResponseInfo resInfo) throws FunctionException{
        try{
            switch (info.getMethod()){
                case GlobalConstants.phone:
                    break;
                case GlobalConstants.mail:
                    MimeMessage m = javaMailSender.createMimeMessage();
                    MimeMessageHelper h = new MimeMessageHelper(m,"UTF-8");
                    h.setFrom(user);
                    h.setTo("jychoi9712@gmail.com");
                    h.setSubject("테스트메일");
                    h.setText("메일테스트");
                    javaMailSender.send(m);
                    break;
                default:
                    throw new Exception("Invalid Method("+info.getMethod()+")");
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error("[Service-UserManage][generateAndSendAuthKey][sendAuthKey][{}] Auth Key Send Fail : ID({}) Method({}), {}",req_id,info.getUser_id(), info.getMethod(),e.getMessage());
            resInfo.setMsg("Auth Key Generate&Send Failed : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserManage][generateAndSendAuthKey][insertAuthKey] Auth Key Send Fail : "+e.getMessage());
            throw new FunctionException("Auth Key Send Fail : "+e.getMessage());
        }
    }

}