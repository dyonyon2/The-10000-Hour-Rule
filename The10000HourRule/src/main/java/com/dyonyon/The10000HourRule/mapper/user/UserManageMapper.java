package com.dyonyon.The10000HourRule.mapper.user;

import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserManageMapper {
    int checkProfileDuplication(String key, String value);
    int checkAuthDuplication(String key, String value);
    int checkPwDuplication(String key, String value, String user_id);
    int updateAuthKey(UserAuthInfo info);
    int verifyAuthKey(UserAuthInfo info);
    int updateProfile(String key, String value, String user_id);
    int updateAuth(String key, String value, String user_id);

}
