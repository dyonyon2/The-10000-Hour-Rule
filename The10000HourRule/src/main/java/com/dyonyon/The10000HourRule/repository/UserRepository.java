package com.dyonyon.The10000HourRule.repository;

import com.dyonyon.The10000HourRule.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRepository {
    List<UserInfo> getAllUsers();
}
