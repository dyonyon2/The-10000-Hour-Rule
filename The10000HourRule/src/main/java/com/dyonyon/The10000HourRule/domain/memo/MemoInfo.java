package com.dyonyon.The10000HourRule.domain.memo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoInfo {
    String memo_idx;
    String memo_type;
    String owner_idx;
    String owner_id;
    String access;
    String status;
}
