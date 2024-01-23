package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.domain.user.UserInfo;
import com.dyonyon.The10000HourRule.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MybatisTest {

    private UserRepository userRepository;

    public MybatisTest(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public String mybatisSelectTest() {
        List<UserInfo> userList = userRepository.getAllUsers();
        String userInfoString = "";
        for (UserInfo m: userList) {
            log.info("m = " + m.toString());
            userInfoString=userInfoString+m.toString();
        }
        return userInfoString;
    }
}
