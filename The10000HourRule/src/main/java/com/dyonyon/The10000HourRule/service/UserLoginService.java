package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.domain.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.UserInfo;
import com.dyonyon.The10000HourRule.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserLoginService {

    private UserRepository userRepository;

    public UserLoginService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserAuthInfo login(HttpServletRequest req, UserAuthInfo userAuthInfo) {
        log.info("In UserLoginService : id = "+userAuthInfo.getUser_id()+", pw : "+userAuthInfo.getPw());
        UserAuthInfo loginResult = userRepository.getLoginResult(userAuthInfo);
        return loginResult;
    }
}
