package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.service.UserLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserEventController {

    private UserLoginService userLoginService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserEventController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @RequestMapping("/login")
    public String loginController(HttpServletRequest req, @RequestBody UserAuthInfo userAuthInfo) throws ParseException {
        String returnString;
        log.info("[Controller-UserEvent][/login][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-UserEvent][/login][{}] BODY : {}",req.getAttribute("req_id"), userAuthInfo);
//        if(userAuthInfo.getUser_id()==null||userAuthInfo.getPw()==null||userAuthInfo.getUser_id()==null)
        log.info("[Controller-UserEvent][/login][{}] Call UserLoginService....",req.getAttribute("req_id"));
        returnString = userLoginService.login(req, userAuthInfo).toString();
        log.info("[Controller-UserEvent][/login][{}] RESULT : {}",req.getAttribute("req_id"),returnString);
        return returnString;
    }

    @RequestMapping("/test")
    public String testController(HttpServletRequest req) throws ParseException, IOException {

        ServletInputStream inputStream = req.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.info("messageBody={}", messageBody);
        UserAuthInfo data = objectMapper.readValue(messageBody, UserAuthInfo.class);
        log.info("id={}, pw={}", data.getUser_id(), data.getPw());
        return "test";
    }
}
