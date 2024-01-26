package com.dyonyon.The10000HourRule.mapper;

import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSignupMapper {
    int checkProfileDuplication(String key, String value);
    int checkAuthDuplication(String key, String value);

    String signupUser(UserDetailInfo userDetailInfo);
    String signupUserProfile(UserDetailInfo userDetailInfo);
    String signupUserAuth(UserDetailInfo userDetailInfo);
}
