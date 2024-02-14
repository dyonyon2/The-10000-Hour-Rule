package com.dyonyon.The10000HourRule.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoImageInfo {
    MultipartFile file;
    String user_id;
    String owner_id;
    String memo_type;
    String path;
    String file_name;
}