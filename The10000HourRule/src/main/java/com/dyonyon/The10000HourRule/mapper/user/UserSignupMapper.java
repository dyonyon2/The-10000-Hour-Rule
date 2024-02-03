package com.dyonyon.The10000HourRule.mapper.user;

import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSignupMapper {
    int checkProfileDuplication(String key, String value);
    int checkAuthDuplication(String key, String value);

    int insertSignupUser(UserDetailInfo userDetailInfo);
    int insertSignupUserProfile(UserDetailInfo userDetailInfo);
    int insertSignupUserAuth(UserDetailInfo userDetailInfo);
    String getUserIdx(String user_id);
}
