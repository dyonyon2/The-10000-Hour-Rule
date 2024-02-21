package com.dyonyon.The10000HourRule.mapper.memo;

import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemoCRUDMapper {
    int insertMemoImage(MemoImageInfo info);

}
