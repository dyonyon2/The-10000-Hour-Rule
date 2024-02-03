package com.dyonyon.The10000HourRule.mapper.user;

import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserManageMapper {
    int checkProfileDuplication(String key, String value);
    int checkAuthDuplication(String key, String value);
    int insertAuthKey(UserAuthInfo info);

}
