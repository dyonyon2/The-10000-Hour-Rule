package com.dyonyon.The10000HourRule.mapper;

import com.dyonyon.The10000HourRule.domain.APICallLogInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper {
    int insertUserLog(APICallLogInfo apiCallLogInfo);
    int insertMemoLog(APICallLogInfo apiCallLogInfo);
    int insertCalenderLog(APICallLogInfo apiCallLogInfo);
    int insertRoutineLog(APICallLogInfo apiCallLogInfo);
    int insertGroupLog(APICallLogInfo apiCallLogInfo);
    int insertEtcLog(APICallLogInfo apiCallLogInfo);
    String getUserIdx(String user_id);

    int updateResDataUserLog(APICallLogInfo apiCallLogInfo);
    int updateResDataMemoLog(APICallLogInfo apiCallLogInfo);
    int updateResDataCalenderLog(APICallLogInfo apiCallLogInfo);
    int updateResDataRoutineLog(APICallLogInfo apiCallLogInfo);
    int updateResDataGroupLog(APICallLogInfo apiCallLogInfo);
    int updateResDataEtcLog(APICallLogInfo apiCallLogInfo);
}
