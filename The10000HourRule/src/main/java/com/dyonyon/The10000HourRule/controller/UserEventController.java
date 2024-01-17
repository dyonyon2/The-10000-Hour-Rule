package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.domain.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.UserInfo;
import com.dyonyon.The10000HourRule.service.MybatisTest;
import com.dyonyon.The10000HourRule.service.UserAPICallLogService;
import com.dyonyon.The10000HourRule.service.UserLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserEventController {

    private UserLoginService userLoginService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserEventController(UserLoginService userLoginService, UserAPICallLogService userAPICallLogService) {
        this.userLoginService = userLoginService;
    }

    @RequestMapping("/login")
    public String loginController(HttpServletRequest req, @RequestBody UserAuthInfo userAuthInfo) throws ParseException {
        log.info("[Controller][Request] : CALL {}",req.getRequestURL());
        log.info("[Controller][Request] : SESSION {}",req.getSession());
        log.info("[Controller][Request] : BODY {}",userAuthInfo);
        return userLoginService.login(req, userAuthInfo).toString();
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
