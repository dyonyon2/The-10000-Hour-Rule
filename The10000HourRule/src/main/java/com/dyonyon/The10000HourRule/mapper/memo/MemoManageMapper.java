package com.dyonyon.The10000HourRule.mapper.memo;

import com.dyonyon.The10000HourRule.domain.memo.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemoManageMapper {
    String getOwnerId(MemoDetailInfo info);
    String getGroupOwnerId(MemoDetailInfo info);
    int updateSharedKey(MemoDetailInfo info);
    int deleteSharedKey(MemoDetailInfo info);
    MemoDetailInfo readSharedMemo(MemoInfo info);
    int updateMemoInfo(String key, String value, String memo_idx, String memo_type);
    int updateMemoDetailInfo(String key, String value, String memo_idx, String memo_type);
    String getNewOwnerAuthority(MemoCopyInfo info);
    int getMemoIdx(MemoCopyInfo info);
    int insertMemo(MemoCopyInfo info);
    int insertMemoImg(MemoCopyInfo info);
    int insertMemoDetail(MemoCopyInfo info);
    String getOwnerIdx(MemoCopyInfo info);
}
