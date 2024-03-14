package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginInfo;
import com.dyonyon.The10000HourRule.service.*;
import com.dyonyon.The10000HourRule.service.user.UserLoginService;
import com.dyonyon.The10000HourRule.service.user.UserManageService;
import com.dyonyon.The10000HourRule.service.user.UserSignupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Enumeration;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserEventController {

    private APIVerificationService apiVerificationService;
    private UserLoginService userLoginService;
    private UserSignupService userSignupService;
    private UserManageService userManageService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserEventController(APIVerificationService apiVerificationService, UserLoginService userLoginService, UserSignupService userSignupService, UserManageService userManageService) {
        this.apiVerificationService = apiVerificationService;
        this.userLoginService = userLoginService;
        this.userSignupService = userSignupService;
        this.userManageService = userManageService;
    }


    // 로그인 처리
    @PostMapping("/login")
    public ResponseInfo loginController(HttpServletRequest req, @RequestBody UserLoginInfo userLoginInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-UserEvent][/login][{}] URL : {}", req.getAttribute("req_id"), req.getRequestURL());
            log.info("[Controller-UserEvent][/login][{}] BODY : {}", req.getAttribute("req_id"), userLoginInfo);
            log.info("[Controller-UserEvent][/login][{}] Call UserLoginService...", req.getAttribute("req_id"));
            result = userLoginService.login(req, userLoginInfo);
            log.info("[Controller-UserEvent][/login][{}] RESULT : STATUS({}) RES_STATUS({})", req.getAttribute("req_id"), result.getStatus(), result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-UserEvent][/login][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-UserEvent][/login]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Login Fail: Exception Occurred");
            result.setRes_data("[Controller-UserEvent][/login] Login Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseInfo signupController(HttpServletRequest req, @RequestBody UserDetailInfo userDetailInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-UserEvent][/signup][{}] URL : {}", req.getAttribute("req_id"), req.getRequestURL());
            log.info("[Controller-UserEvent][/signup][{}] BODY : {}", req.getAttribute("req_id"), userDetailInfo);
            log.info("[Controller-UserEvent][/signup][{}] Call UserSignupService...", req.getAttribute("req_id"));
            result = userSignupService.signup(req, userDetailInfo);
            log.info("[Controller-UserEvent][/signup][{}] RESULT : STATUS({}) RES_STATUS({})", req.getAttribute("req_id"), result.getStatus(), result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-UserEvent][/signup][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-UserEvent][/signup]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Signup Fail: Exception Occurred");
            result.setRes_data("[Controller-UserEvent][/signup] Signup Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 유저 정보(아이디, 닉네임, 이메일, 핸드폰) 중복 확인 처리
    @GetMapping("/check")
    public ResponseInfo checkController(HttpServletRequest req) {
        ResponseInfo result = new ResponseInfo();
        try {
            Enumeration<String> parameterNames = req.getParameterNames();
            String key = null;
            String value = null;
            log.info("[Controller-UserEvent][/check][{}] URL : {}", req.getAttribute("req_id"), req.getRequestURL());
            while (parameterNames.hasMoreElements()) {
                key = parameterNames.nextElement();
                value = req.getParameter(key);
                log.info("[Controller-UserEvent][/check][{}] QUERY STRING KEY : {}", req.getAttribute("req_id"), key);
                log.info("[Controller-UserEvent][/check][{}] QUERY STRING VALUE : {}", req.getAttribute("req_id"), value);
            }
            log.info("[Controller-UserEvent][/check][{}] Call UserManageService...", req.getAttribute("req_id"));
            result = userManageService.checkDuplication(req, key, value);
            log.info("[Controller-UserEvent][/check][{}] RESULT : STATUS({}) RES_STATUS({})", req.getAttribute("req_id"), result.getStatus(), result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-UserEvent][/check][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-UserEvent][/check]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Duplication Check Fail: Exception Occurred");
            result.setRes_data("[Controller-UserEvent][/check] Check Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // Auth Key(핸드폰,이메일) 생성 및 전송 처리 : API 처리 전 로그인 세션 확인
    @PostMapping("/auth")
    public ResponseInfo authController(HttpServletRequest req, @RequestBody UserAuthInfo userAuthInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-UserEvent][/auth][{}] URL : {}", req.getAttribute("req_id"), req.getRequestURL());
            log.info("[Controller-UserEvent][/auth][{}] BODY : {}", req.getAttribute("req_id"), userAuthInfo);
            log.info("[Controller-UserEvent][/auth][{}] Call API ApiVerificationService...", req.getAttribute("req_id"));
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), userAuthInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-UserEvent][/auth][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-UserEvent][/auth][{}] Call UserManageService...", req.getAttribute("req_id"));
            result = userManageService.generateAndSendAuthKey(req, userAuthInfo);
            log.info("[Controller-UserEvent][/auth][{}] RESULT : STATUS({}) RES_STATUS({})", req.getAttribute("req_id"), result.getStatus(), result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-UserEvent][/auth][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-UserEvent][/auth]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Auth Key Generate Fail: Exception Occurred");
            result.setRes_data("[Controller-UserEvent][/auth] Auth Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // Auth Key 검증 처리
    @GetMapping("/auth/check")
    public ResponseInfo authCheckController(HttpServletRequest req, @ModelAttribute UserAuthInfo userAuthInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-UserEvent][/auth/check][{}] URL : {}", req.getAttribute("req_id"), req.getRequestURL());
            log.info("[Controller-UserEvent][/auth/check][{}] BODY : {}", req.getAttribute("req_id"), userAuthInfo);
            log.info("[Controller-UserEvent][/auth/check][{}] Call API ApiVerificationService...", req.getAttribute("req_id"));
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), userAuthInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-UserEvent][/auth/check][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-UserEvent][/auth/check][{}] Call UserManageService...", req.getAttribute("req_id"));
            result = userManageService.verifyAuthKey(req, userAuthInfo);
            log.info("[Controller-UserEvent][/auth/check][{}] RESULT : STATUS({}) RES_STATUS({})", req.getAttribute("req_id"), result.getStatus(), result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-UserEvent][/auth/check][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-UserEvent][/auth/check]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Auth Check Fail: Exception Occurred");
            result.setRes_data("[Controller-UserEvent][/auth/check] Auth Check Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 유저 정보(닉네임, 비번, 이메일, 핸드폰) 변경 처리
    @PatchMapping("/change")
    public ResponseInfo changeController(HttpServletRequest req, @RequestBody UserDetailInfo userDetailInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-UserEvent][/change][{}] URL : {}", req.getAttribute("req_id"), req.getRequestURL());
            log.info("[Controller-UserEvent][/change][{}] BODY : {}", req.getAttribute("req_id"), userDetailInfo);
            log.info("[Controller-UserEvent][/change][{}] Call API ApiVerificationService...", req.getAttribute("req_id"));
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), userDetailInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-UserEvent][/change][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-UserEvent][/change][{}] Call UserManageService....", req.getAttribute("req_id"));
            result = userManageService.changeUserInfo(req, userDetailInfo);
            log.info("[Controller-UserEvent][/change][{}] RESULT : STATUS({}) RES_STATUS({})", req.getAttribute("req_id"), result.getStatus(), result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-UserEvent][/change][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-UserEvent][/change]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Change Info Fail: Exception Occurred");
            result.setRes_data("[Controller-UserEvent][/change] Change Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }
}
