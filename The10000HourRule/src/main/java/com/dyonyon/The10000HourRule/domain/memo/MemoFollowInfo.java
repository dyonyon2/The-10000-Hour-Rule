package com.dyonyon.The10000HourRule.domain.memo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoFollowInfo {
    String memo_idx;
    String memo_type;
    String user_id;
    String user_idx;
    String owner_idx;
    String owner_id;
    String follower_idx;
    String status;
    String follower_access;
    String req_msg;
    String res_msg;
}
