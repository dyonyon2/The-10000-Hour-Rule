package com.dyonyon.The10000HourRule.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailInfo {
    String user_idx;
    String user_id;
    String pw;
    String name;
    String phone;
    String mail;
    String nickname;
    String sex;
    String birth;
    String region;
    String introduction;
    String img_url;
    Date create_dt;
    Date update_dt;
    Date expire_dt;
    Date join_dt;
}
