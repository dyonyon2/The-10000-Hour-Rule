package com.dyonyon.The10000HourRule.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthInfo {

    String user_idx;
    String user_id;
    String pw;
    String name;
    String phone;
    String mail;
    Date expire_dt;
    Date join_dt;
}
