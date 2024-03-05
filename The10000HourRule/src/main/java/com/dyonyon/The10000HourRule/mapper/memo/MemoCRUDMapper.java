package com.dyonyon.The10000HourRule.mapper.memo;

import com.dyonyon.The10000HourRule.domain.memo.MemoDetailInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoInfo;
import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemoCRUDMapper {
    int getMemoIdx();
    int insertMemo(MemoInfo info);
    int insertMemoDetail(MemoInfo info);
    int insertMemoImage(MemoImageInfo info);
    String getOwnerIdx(String memo_type, String owner_id);
    int updateMemo(MemoDetailInfo info);
    MemoDetailInfo readMemo(MemoInfo info);
}
