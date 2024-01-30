package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
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

    public ResponseInfo checkDuplication(HttpServletRequest req, String key, String value) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        // 1. 중복 확인
        try{
            log.info("[Service-UserManage][checkDuplication][{}] Check Started... : key({}) value({})", req_id, key, value);

            isDuplication(req_id, key, value, responseInfo);
        } catch (FunctionException e){
            log.error("[Service-UserManage][checkDuplication][{}] Check Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserManage][checkDuplication][{}] Check Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Duplication Check Failed : Exception Occurred");
            responseInfo.setRes_data("Duplication Check Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public void isDuplication(String req_id, String key, String value, ResponseInfo resInfo) throws FunctionException {
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
                    throw new Exception("Invalid Key("+key+") : Value("+value+")");
            }

            log.info("[Service-UserManage][checkDuplication][isDuplication][{}] Duplicate Check Successed : Key({}), Value({}), Count({})",req_id,key,value,result);

            if(result>0) {  // 중복
                resInfo.setRes_status("-1");
                resInfo.setMsg("Duplication Check : Duplicated");
                resInfo.setRes_data("Duplicated : Key("+key+") Value("+value+")");
            } else if (result < 0) {    // 에러
                throw new Exception("Result(" + result + ")");
            } else {    // 중복 X
                resInfo.setMsg("Duplication Check : Not Duplicated");
                resInfo.setRes_data("Not Duplicated : Key("+key+") Value("+value+")");
            }
        } catch (Exception e) {
            log.info("[Service-UserManage][checkDuplication][isDuplication][{}] Duplication Check Select Failed : {}",req_id,e.getMessage());
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Duplication Check Failed : Exception Occurred");
            resInfo.setRes_data("Duplication Check Failed : "+e.getMessage());
            throw new FunctionException("Duplication Check Failed : "+e.getMessage());
        }
    }

}