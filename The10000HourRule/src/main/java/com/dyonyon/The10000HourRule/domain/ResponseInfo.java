package com.dyonyon.The10000HourRule.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInfo {
    String status;
    String msg;
    String res_status;
    Object res_data;
    String err_code;
}