package com.dyonyon.The10000HourRule.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentInfo {
    String service;
    String access;
    String user_id;
    String owner_id;
    String content_type;
    String group_idx;
    String group_id;
    String content_idx;
}