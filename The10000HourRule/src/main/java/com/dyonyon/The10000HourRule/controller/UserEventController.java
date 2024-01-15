package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.domain.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.UserInfo;
import com.dyonyon.The10000HourRule.service.MybatisTest;
import com.dyonyon.The10000HourRule.service.UserAPICallLogService;
import com.dyonyon.The10000HourRule.service.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserEventController {

    private UserLoginService userLoginService;

    public UserEventController(UserLoginService userLoginService, UserAPICallLogService userAPICallLogService) {
        this.userLoginService = userLoginService;
    }

    @RequestMapping("/login")
    public String loginController(HttpServletRequest req, @RequestBody UserAuthInfo userAuthInfo) throws ParseException {
        log.info("/api/user/login API CALL");
        log.info("session : "+req.getSession());
        log.info("userAuthInfo : "+userAuthInfo);
        return userLoginService.login(req, userAuthInfo).toString();

    }
}
