package com.dyonyon.The10000HourRule.mapper.memo;

import com.dyonyon.The10000HourRule.domain.memo.MemoDetailInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoListInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemoManageMapper {
    String getOwnerId(MemoDetailInfo info);
    String getGroupOwnerId(MemoDetailInfo info);
    int updateSharedKey(MemoDetailInfo info);
    int deleteSharedKey(MemoDetailInfo info);
}
