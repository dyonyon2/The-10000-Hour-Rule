package com.dyonyon.The10000HourRule.domain.user;

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
    String name;
    String phone;
    String mail;
    String key;
    String method;
    Date key_expire_dt;
}
