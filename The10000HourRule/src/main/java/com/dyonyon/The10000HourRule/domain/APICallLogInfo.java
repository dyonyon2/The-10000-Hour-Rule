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
    String req_data;
//    String start_dt;  // => SYSDATE로 입력
}
