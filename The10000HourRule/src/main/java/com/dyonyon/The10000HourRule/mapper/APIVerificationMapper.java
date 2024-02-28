package com.dyonyon.The10000HourRule.mapper;

import com.dyonyon.The10000HourRule.domain.ContentInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface APIVerificationMapper {
    String getLoginSessionId(String user_id);
    Date getLoginSessionEXP(String user_id);
    int updateSessionTime(String user_id);
    String getGroupOwnerId(ContentInfo info);
    String getContentOwnerId(ContentInfo info);
}
