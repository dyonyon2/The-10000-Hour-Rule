package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginInfo;
import com.dyonyon.The10000HourRule.service.APICheckService;
import com.dyonyon.The10000HourRule.service.UserLoginService;
import com.dyonyon.The10000HourRule.service.UserSignupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserEventController {

    private APICheckService apiCheckService;
    private UserLoginService userLoginService;
    private UserSignupService userSignupService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserEventController(APICheckService apiCheckService, UserLoginService userLoginService, UserSignupService userSignupService) {
        this.apiCheckService = apiCheckService;
        this.userLoginService = userLoginService;
        this.userSignupService = userSignupService;
    }

    @RequestMapping("/login")
    public ResponseInfo loginController(HttpServletRequest req, @RequestBody UserLoginInfo userLoginInfo) throws ParseException {
        ResponseInfo result;
        log.info("[Controller-UserEvent][/login][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-UserEvent][/login][{}] BODY : {}",req.getAttribute("req_id"), userLoginInfo);
        log.info("[Controller-UserEvent][/login][{}] Call UserLoginService....",req.getAttribute("req_id"));
        result = userLoginService.login(req, userLoginInfo);
        log.info("[Controller-UserEvent][/login][{}] RESULT : {}",req.getAttribute("req_id"),result.getStatus());
        return result;
    }

    @RequestMapping("/signup")
    public ResponseInfo signupController(HttpServletRequest req, @RequestBody UserDetailInfo userDetailInfo) throws ParseException {
        ResponseInfo result;
        log.info("[Controller-UserEvent][/signup][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-UserEvent][/signup][{}] BODY : {}",req.getAttribute("req_id"), userDetailInfo);
        log.info("[Controller-UserEvent][/signup][{}] Call UserSignupService....",req.getAttribute("req_id"));
        result = userSignupService.signup(req, userDetailInfo);
        log.info("[Controller-UserEvent][/signup][{}] RESULT : {}",req.getAttribute("req_id"),result.getStatus());
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
