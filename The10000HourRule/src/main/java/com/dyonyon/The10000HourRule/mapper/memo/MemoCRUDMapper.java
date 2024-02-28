package com.dyonyon.The10000HourRule.mapper.memo;

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
    String getOwnerIdx(MemoInfo info);
    String getOwnerIdx2(MemoImageInfo info);
}
