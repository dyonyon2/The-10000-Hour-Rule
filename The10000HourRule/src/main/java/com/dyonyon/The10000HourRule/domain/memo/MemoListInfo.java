package com.dyonyon.The10000HourRule.domain.memo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoListInfo {
    String memo_idx;
    String memo_type;
    String owner_id;    // 생성시 필요한 데이터
    String title;
    String create_dt;
    String update_dt;
}
