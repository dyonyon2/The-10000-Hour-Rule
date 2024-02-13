package com.dyonyon.The10000HourRule.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoInfo {
    String memo_idx;
    String memo_type;
    String owner_idx;
    String access;
    String status;
}
