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
    private TestService testService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserEventController(APIVerificationService apiVerificationService, UserLoginService userLoginService, UserSignupService userSignupService, UserManageService userManageService, TestService testService) {
        this.apiVerificationService = apiVerificationService;
        this.userLoginService = userLoginService;
        this.userSignupService = userSignupService;
        this.userManageService = userManageService;
        this.testService = testService;
    }


    // 로그인
    @PostMapping("/login")
    public ResponseInfo loginController(HttpServletRequest req, @RequestBody UserLoginInfo userLoginInfo) throws ParseException {
        ResponseInfo result;
        log.info("[Controller-UserEvent][/login][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-UserEvent][/login][{}] BODY : {}",req.getAttribute("req_id"), userLoginInfo);
        log.info("[Controller-UserEvent][/login][{}] Call UserLoginService....",req.getAttribute("req_id"));
        result = userLoginService.login(req, userLoginInfo);
        log.info("[Controller-UserEvent][/login][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        return result;
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseInfo signupController(HttpServletRequest req, @RequestBody UserDetailInfo userDetailInfo) throws ParseException {
        ResponseInfo result;
        log.info("[Controller-UserEvent][/signup][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-UserEvent][/signup][{}] BODY : {}",req.getAttribute("req_id"), userDetailInfo);
        log.info("[Controller-UserEvent][/signup][{}] Call UserSignupService....",req.getAttribute("req_id"));
        result = userSignupService.signup(req, userDetailInfo);
        log.info("[Controller-UserEvent][/signup][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        return result;
    }

    // 중복체크 (회원가입, 내 정보 수정)
    @GetMapping("/check")
    public ResponseInfo checkController(HttpServletRequest req) {
        ResponseInfo result;
        Enumeration<String> parameterNames = req.getParameterNames();
        String key = null;
        String value = null;
        log.info("[Controller-UserEvent][/check][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        while (parameterNames.hasMoreElements()){
            key = parameterNames.nextElement();
            value = req.getParameter(key);
            log.info("[Controller-UserEvent][/check][{}] QUERY STRING KEY : {}",req.getAttribute("req_id"), key);
            log.info("[Controller-UserEvent][/check][{}] QUERY STRING VALUE : {}",req.getAttribute("req_id"), value);
        }
        log.info("[Controller-UserEvent][/check][{}] Call UserManageService....",req.getAttribute("req_id"));
        result = userManageService.checkDuplication(req, key, value);
        log.info("[Controller-UserEvent][/check][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        return result;
    }

    @PostMapping("/auth")
    public ResponseInfo authController(HttpServletRequest req, @RequestBody UserAuthInfo userAuthInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        log.info("[Controller-UserEvent][/auth][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-UserEvent][/auth][{}] BODY : {}",req.getAttribute("req_id"), userAuthInfo);
        log.info("[Controller-UserEvent][/auth][{}] Call API ApiVerificationService....",req.getAttribute("req_id"));
        result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), userAuthInfo.getUser_id(),req.getSession().getId());
        if("-1".equals(result.getRes_status()))
            return result;
        log.info("[Controller-UserEvent][/auth][{}] Call UserManageService....",req.getAttribute("req_id"));
        result = userManageService.generateAndSendAuthKey(req, userAuthInfo);
        log.info("[Controller-UserEvent][/auth][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        return result;
    }

    @GetMapping("/auth/check")
    public ResponseInfo authCheckController(HttpServletRequest req, @ModelAttribute UserAuthInfo userAuthInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        log.info("[Controller-UserEvent][/auth/check][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-UserEvent][/auth/check][{}] BODY : {}",req.getAttribute("req_id"), userAuthInfo);
        log.info("[Controller-UserEvent][/auth/check][{}] Call API ApiVerificationService....",req.getAttribute("req_id"));
        result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), userAuthInfo.getUser_id(),req.getSession().getId());
        if("-1".equals(result.getRes_status()))
            return result;
        log.info("[Controller-UserEvent][/auth/check][{}] Call UserManageService....",req.getAttribute("req_id"));
        result = userManageService.verifyAuthKey(req, userAuthInfo);
        log.info("[Controller-UserEvent][/auth/check][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        return result;
    }




    @RequestMapping("/test")
//    public ResponseInfo testController(HttpServletRequest req, @RequestBody UserDetailInfo userDetailInfo) throws ParseException, IOException {
    public ResponseInfo testController(HttpServletRequest req) throws ParseException, IOException {

//        objectMapper 쓰는 방법
//        ServletInputStream inputStream = req.getInputStream();
//        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//        log.info("messageBody={}", messageBody);
//        UserAuthInfo data = objectMapper.readValue(messageBody, UserAuthInfo.class);
//        log.info("id={}, pw={}", data.getUser_id(), data.getPw());
//        return "test";

//        ResponseInfo result;
//        log.info("[Controller-UserEvent][/test][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
//        result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), userDetailInfo.getUser_id(),req.getSession().getId());
//        if("-1".equals(result.getStatus())){
//            result.setStatus("1");
//            return result;
//        }
//        return result;

        ResponseInfo result;
        log.info("[Controller-UserEvent][/test][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        result = testService.mailSend((String) req.getAttribute("req_id"), String.valueOf(req.getRequestURL()));
        return result;
    }
}
