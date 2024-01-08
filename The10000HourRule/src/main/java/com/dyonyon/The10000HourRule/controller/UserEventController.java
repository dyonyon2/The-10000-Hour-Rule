package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.service.MybatisTest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserEventController {

    private MybatisTest mybatisTest;

    public UserEventController(MybatisTest mybatisTest) {
        this.mybatisTest = mybatisTest;
    }

    @RequestMapping("/")
    public String test(HttpServletRequest req){
        log.info("test!! URL : /");
        String userReturn = mybatisTest.mybatisSelectTest();
        return userReturn;
    }
}
