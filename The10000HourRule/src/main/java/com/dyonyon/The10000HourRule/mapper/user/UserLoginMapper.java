package com.dyonyon.The10000HourRule.mapper.user;

import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface UserLoginMapper {
    int insertLoginLog(UserLoginInfo info);
    int getLoginAttempt(UserLoginInfo info);
    UserDetailInfo getLoginResult(UserLoginInfo info);
    int updateStatus(String req_id, String status);
    int resetLoginAttempt(String user_id);
    String getLoginSessionId(String user_id);
    int updateLoginSession(UserLoginInfo info);
    int insertLoginSession(UserLoginInfo info);
}
