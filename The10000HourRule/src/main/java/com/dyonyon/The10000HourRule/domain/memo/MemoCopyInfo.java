package com.dyonyon.The10000HourRule.domain.memo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoCopyInfo {
    String req_id;
    String memo_idx;
    String memo_type;
    String user_id;
    String user_idx;
    String owner_idx;
    String owner_id;
    String new_memo_idx;
    String new_memo_type;
    String new_owner_id;
    String new_owner_idx;
}
