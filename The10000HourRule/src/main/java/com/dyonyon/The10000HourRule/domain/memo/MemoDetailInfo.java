package com.dyonyon.The10000HourRule.domain.memo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoDetailInfo {
    String req_id;
    String memo_idx;
    String memo_type;
    String owner_id;
    String owner_idx;
    String user_id;
    String user_idx;
    String last_edit_user_idx;
    String version;
    String title;
    String content;
    String category_no;
    String favorites;
    String create_dt;
    String update_dt;
    String shared_ket;
}
