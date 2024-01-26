package com.dyonyon.The10000HourRule.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginInfo {
    String user_idx;
    String user_id;
    String pw;
    String status;
    String req_id;
    String session_id;

    String msg;
}
