package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.domain.user.UserInfo;
import com.dyonyon.The10000HourRule.mapper.UserSignupMapper;
import com.dyonyon.The10000HourRule.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserManageService {

    private UserSignupMapper userSignupMapper;

    public UserManageService(UserSignupMapper userSignupMapper){
        this.userSignupMapper = userSignupMapper;
    }

    public ResponseInfo check(HttpServletRequest req, String key, String value) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_code("000000");
        boolean isDupled = true; // true : 중복, false : 중복 안됨
        try{
            log.info("[Service-UserManage][check][{}] Check Started... : key({}) value({})", req_id, key, value);
            isDupled = isDuplication(req_id, key, value, responseInfo);
            if(isDupled){   // 중복
                if(!"-1".equals(responseInfo.getStatus())){
                    log.info("[Service-UserManage][check][{}] The Data Is Duplicated : key({}) value({})", req_id, key, value);
                    responseInfo.setMsg("Duplication Check : Duplicated");
                } else {
                    log.info("[Service-UserManage][check][{}] The Key Is Invalid : key({}) value({})", req_id, key, value);
                }
            } else {    // 중복 X
                log.info("[Service-UserManage][check][{}] The Data Is Not Duplicated : key({}) value({})", req_id, key, value);
                responseInfo.setMsg("Duplication Check : Not Duplicated");
            }
            return responseInfo;
        } catch (Exception e){
            log.error("[Service-UserManage][check][{}] Check Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            responseInfo.setStatus("-1");
            responseInfo.setMsg("Duplication Check Failed : Unknown Error Occurred");
            responseInfo.setRes_data(e.getMessage());
            responseInfo.setRes_code("UN");
            return responseInfo;
        }
    }

    public boolean isDuplication(String req_id, String key, String value, ResponseInfo resInfo){
        boolean res = false;
        try {
            int result = -1;
            switch (key){
                case GlobalConstants.nickname:
                    result = userSignupMapper.checkProfileDuplication(key, value);
                    break;
                case GlobalConstants.user_id, GlobalConstants.mail, GlobalConstants.phone:
                    result = userSignupMapper.checkAuthDuplication(key, value);
                    break;
                default:
                    res = true;
                    resInfo.setRes_data("Duplication Check Failed : Key("+key+") Is Invalide");
                    resInfo.setMsg("Duplication Check Failed : Key Is Invalid");
            }
            log.info("[Service-UserSignup][isDuplication][{}] Duplicate Check Successed : Key({}), Value({}), Count({})",req_id,key,value,result);
            if(result>0)
                res = true;
            return res;
        } catch (Exception e) {
            log.info("[Service-UserLogin][isDuplication][{}] Duplication Check Select Failed : {}",req_id,e.getMessage());
            resInfo.setMsg("Duplication Check Failed : Unknown Error Occurred");
            resInfo.setRes_data("Duplication Check Failed : "+e.getMessage());
            resInfo.setStatus("-1");
            return res;
        }
    }

}