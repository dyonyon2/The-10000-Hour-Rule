package com.dyonyon.The10000HourRule.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APICallLogInfo {
    String req_id;
    String user_id;
    String user_idx;    // => 로그인 이후에만 사용
    String api_url;
    String status;
    String msg;
    String req_data;
    String err_code;
    String res_status;
    String res_data;
    String start_dt;  // => SYSDATE로 입력
    String end_dt;
    String session_id;
    String group_idx;
    String memo_idx;
    String calender_idx;
    String routine_idx;
    String target_idx;
    String target_type;
    String owner_id;
    String owner_idx;

}
