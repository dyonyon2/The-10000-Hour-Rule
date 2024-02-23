package com.dyonyon.The10000HourRule.domain.memo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoInfo {
    String memo_idx;
    String memo_type;   // 생성시 필요한 데이터
    String user_id;     // 생성시 필요한 데이터
    String user_idx;
    String owner_idx;
    String owner_id;    // 생성시 필요한 데이터
    String access;
    String status;
}
