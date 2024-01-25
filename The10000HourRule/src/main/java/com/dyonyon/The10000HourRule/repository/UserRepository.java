package com.dyonyon.The10000HourRule.repository;

import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.user.UserInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginLogInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRepository {
    List<UserInfo> getAllUsers();

    int insertLoginLog(UserLoginLogInfo info);
    int getLoginAttempt(UserLoginLogInfo info);
    UserAuthInfo getLoginResult(UserLoginLogInfo info);
    int updateStatus(String req_id, String status);
    int resetLoginAttempt(String user_id);
    int getLoginSession(String user_idx);
    int updateLoginSession(UserLoginLogInfo info);
    int insertLoginSession(UserLoginLogInfo info);
}
