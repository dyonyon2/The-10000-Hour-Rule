package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.mapper.UserLoginMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserAPICallLogService {

    private UserLoginMapper userLoginMapper;

    public UserAPICallLogService(UserLoginMapper userLoginMapper){
        this.userLoginMapper = userLoginMapper;
    }

    public UserDetailInfo login(HttpServletRequest req, UserDetailInfo userDetailInfo) {
//        log.info("In UserLoginService : id = "+userAuthInfo.getUser_id()+", pw : "+userAuthInfo.getPw());
//        UserAuthInfo loginResult = userRepository.getLoginResult(userAuthInfo);
        return userDetailInfo;
    }
}
