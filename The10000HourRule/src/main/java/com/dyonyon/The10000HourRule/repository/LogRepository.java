package com.dyonyon.The10000HourRule.repository;

import com.dyonyon.The10000HourRule.domain.APICallLogInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogRepository {
    int insertUserLog(APICallLogInfo apiCallLogInfo);
    int insertMemoLog(APICallLogInfo apiCallLogInfo);
    int insertCalenderLog(APICallLogInfo apiCallLogInfo);
    int insertRoutineLog(APICallLogInfo apiCallLogInfo);
    int insertGroupLog(APICallLogInfo apiCallLogInfo);
    int insertEtcLog(APICallLogInfo apiCallLogInfo);
    String getUserIdx(String user_id);
}
