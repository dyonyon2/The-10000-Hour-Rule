package com.dyonyon.The10000HourRule.service.memo;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoCopyInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoDetailInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoInfo;
import com.dyonyon.The10000HourRule.mapper.memo.MemoFollowMapper;
import com.dyonyon.The10000HourRule.mapper.memo.MemoManageMapper;
import com.dyonyon.The10000HourRule.util.CommonUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@Slf4j
public class MemoFollowService {

    @Autowired
    private PlatformTransactionManager transactionManager;
    private MemoFollowMapper memoFollowMapper;

    @Value("${path.img}")
    String imgPath;

    @Autowired
    Gson gson = new Gson();

    public MemoFollowService(MemoFollowMapper memoFollowMapper){
        this.memoFollowMapper = memoFollowMapper;
    }





}