package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginInfo;
import com.dyonyon.The10000HourRule.service.APICheckService;
import com.dyonyon.The10000HourRule.service.UserLoginService;
import com.dyonyon.The10000HourRule.service.UserManageService;
import com.dyonyon.The10000HourRule.service.UserSignupService;
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

    private APICheckService apiCheckService;
    private UserLoginService userLoginService;
    private UserSignupService userSignupService;
    private UserManageService userManageService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserEventController(APICheckService apiCheckService, UserLoginService userLoginService, UserSignupService userSignupService, UserManageService userManageService) {
        this.apiCheckService = apiCheckService;
        this.userLoginService = userLoginService;
        this.userSignupService = userSignupService;
        this.userManageService = userManageService;
    }


    // 로그인
    @PostMapping("/login")
    public ResponseInfo loginController(HttpServletRequest req, @RequestBody UserLoginInfo userLoginInfo) throws ParseException {
        ResponseInfo result;
        log.info("[Controller-UserEvent][/login][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-UserEvent][/login][{}] BODY : {}",req.getAttribute("req_id"), userLoginInfo);
        log.info("[Controller-UserEvent][/login][{}] Call UserLoginService....",req.getAttribute("req_id"));
        result = userLoginService.login(req, userLoginInfo);
        log.info("[Controller-UserEvent][/login][{}] RESULT : {}",req.getAttribute("req_id"),result.getStatus());
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
        log.info("[Controller-UserEvent][/signup][{}] RESULT : {}",req.getAttribute("req_id"),result.getStatus());
        return result;
    }

    // 중복체크 (회원가입, 내 정보 수정)
//    @GetMapping(value={"/check","/check/{what}"})
//    public ResponseInfo checkController(HttpServletRequest req, @ModelAttribute UserDetailInfo userDetailInfo) {
    @GetMapping("/check")
    public ResponseInfo checkController(HttpServletRequest req) {
        ResponseInfo result = new ResponseInfo();
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
        log.info("[Controller-UserEvent][/check][{}] Call UserSignupService....",req.getAttribute("req_id"));
        result = userManageService.check(req, key, value);
//        log.info("[Controller-UserEvent][/signup][{}] RESULT : {}",req.getAttribute("req_id"),result.getStatus());
        return result;
    }


    @RequestMapping("/test")
    public ResponseInfo testController(HttpServletRequest req, @RequestBody UserDetailInfo userDetailInfo) throws ParseException, IOException {

//        objectMapper 쓰는 방법
//        ServletInputStream inputStream = req.getInputStream();
//        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//        log.info("messageBody={}", messageBody);
//        UserAuthInfo data = objectMapper.readValue(messageBody, UserAuthInfo.class);
//        log.info("id={}, pw={}", data.getUser_id(), data.getPw());
//        return "test";
        ResponseInfo result;
        log.info("[Controller-UserEvent][/test][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        result = apiCheckService.checkLoginSession((String) req.getAttribute("req_id"), userDetailInfo.getUser_id(),req.getSession().getId());
        if("-1".equals(result.getStatus())){
            result.setStatus("1");
            return result;
        }
        return result;
    }
}
