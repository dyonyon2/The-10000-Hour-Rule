package com.dyonyon.The10000HourRule.service.user;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.domain.user.UserInfo;
import com.dyonyon.The10000HourRule.mapper.user.UserSignupMapper;
import com.dyonyon.The10000HourRule.util.CommonUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@Slf4j
@Transactional
public class UserSignupService {

    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    Gson gson = new Gson();

    private UserSignupMapper userSignupMapper;

    public UserSignupService(UserSignupMapper userSignupMapper){
        this.userSignupMapper = userSignupMapper;
    }

    public ResponseInfo signup(HttpServletRequest req, UserDetailInfo userDetailInfo) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-UserSignup][signup][{}] Signup Started...", req_id);

            // 체크 : 필수 값, 규칙 체크, 중복 체크
            // 1. 필수 값 체크
            // isNull => true (null), false(null X)
            if(CommonUtil.isNull(GlobalConstants.USER_ID, userDetailInfo.getUser_id())||
                    CommonUtil.isNull(GlobalConstants.PW, userDetailInfo.getPw())||CommonUtil.isNull(GlobalConstants.NAME, userDetailInfo.getName())||
                    CommonUtil.isNull(GlobalConstants.NICKNAME, userDetailInfo.getNickname())||CommonUtil.isNull(GlobalConstants.SEX, userDetailInfo.getSex())||
                    CommonUtil.isNull(GlobalConstants.BIRTH, userDetailInfo.getBirth())||CommonUtil.isNull(GlobalConstants.REGION, userDetailInfo.getRegion())||
                    CommonUtil.isNull(GlobalConstants.PHONE, userDetailInfo.getPhone())||CommonUtil.isNull(GlobalConstants.MAIL, userDetailInfo.getMail())){
                log.info("[Service-UserSignup][signup][{}] Signup Fail : Required Data Is Missing", req_id);
                responseInfo.setMsg("Signup Fail : Required Data Is Missing");
                responseInfo.setRes_data("[Service-UserSignup][signup] Required Data Is Missing");
                responseInfo.setRes_status("-1");
            }
            // 2. 규칙 체크. 핸드폰/이메일은 인증해야해서 규칙을 무조건 지켜야함. (아이디, 닉네임, 이름, 핸드폰, 이메일, PW, 생일)
            else {
                log.info("[Service-UserSignup][signup][{}] Signup Check 1 Success(Required Data)", req_id);

                // isFormat => true (포맷), false (포맷 X)
                if(!CommonUtil.isFormat(GlobalConstants.USER_ID,userDetailInfo.getUser_id())||    // true : 포맷 안맞음, false : 포맷임
                        !CommonUtil.isFormat(GlobalConstants.NICKNAME,userDetailInfo.getNickname())||!CommonUtil.isFormat(GlobalConstants.NAME,userDetailInfo.getName())||
                        !CommonUtil.isFormat(GlobalConstants.PHONE,userDetailInfo.getPhone())||!CommonUtil.isFormat(GlobalConstants.MAIL,userDetailInfo.getMail())||
                        !CommonUtil.isFormat(GlobalConstants.PW,userDetailInfo.getPw())||!CommonUtil.isFormat(GlobalConstants.BIRTH,userDetailInfo.getBirth())) {
                    log.info("[Service-UserSignup][signup][{}] Signup Fail : There Is Incorrect Format Data", req_id);
                    responseInfo.setMsg("Signup Fail : There Is Incorrect Format Data");
                    responseInfo.setRes_data("[Service-UserSignup][signup] Incorrect Format Data");
                    responseInfo.setRes_status("-1");
                }
                // 3. 중복 체크 => 2중 체크! 원래 회원가입 페이지에서 사전 진행. (아이디, 핸드폰, 이메일, 닉네임)
                else {
                    log.info("[Service-UserSignup][signup][{}] Signup Check 2 Success(Data Format)", req_id);

                    // duplicateCheck => true (중복), false(중복X)
                    if(isDuplication(req_id,GlobalConstants.USER_ID,userDetailInfo.getUser_id(),responseInfo)||
                            isDuplication(req_id,GlobalConstants.PHONE,userDetailInfo.getPhone(),responseInfo)||
                            isDuplication(req_id,GlobalConstants.MAIL,userDetailInfo.getMail(),responseInfo)||
                            isDuplication(req_id,GlobalConstants.NICKNAME,userDetailInfo.getNickname(),responseInfo)){
                        log.info("[Service-UserSignup][signup][{}] Signup Fail : There Is Duplicate Data", req_id);
                        responseInfo.setMsg("Signup Fail : There Is Duplicate Data");
                        responseInfo.setRes_data("[Service-UserSignup][signup] Duplicate Data");
                        responseInfo.setRes_status("-1");
                    }
                    // 4. 회원 정보 insert
                    else {
                        log.info("[Service-UserSignup][signup][{}] Signup Check 3 Success(Data Duplication)", req_id);

                        signupUser(req_id, userDetailInfo, responseInfo);

                        log.info("[Service-UserSignup][signup][{}] Signup Success... : ID({})",req_id,userDetailInfo.getUser_id());
                    }
                }
            }
        } catch (FunctionException e){
            log.error("[Service-UserSignup][signup][{}] Signup Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-UserSignup][signup][{}] Signup Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-UserSignup][signup]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Signup Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-UserSignup][signup] Signup Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public boolean isDuplication(String req_id, String key, String value, ResponseInfo resInfo) throws FunctionException {
        boolean res = false;
        try {
            int result = -1;    // 중복 x
            switch (key){
                case GlobalConstants.NICKNAME:
                    result = userSignupMapper.checkProfileDuplication(key, value);
                    break;
                default:
                    result = userSignupMapper.checkAuthDuplication(key, value);
                    break;
            }
            log.info("[Service-UserSignup][signup][isDuplication][{}] Duplicate Check Select Success : {} = {} count({})",req_id,key,value,result);
            if(result>0)  // 중복 o
                res = true;
            else if(result < 0) // 에러
                throw new Exception("Result("+result+")");
        } catch (Exception e) { //에러
            log.error("[Service-UserSignup][signup][isDuplication][{}] Duplicate Check Select Fail : {}",req_id,e.getMessage());
            log.error("[Service-UserSignup][signup][isDuplication]["+req_id+"] Error PrintStack : ",e);
            resInfo.setMsg("Signup Fail : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserLogin][signup][isDuplication] Signup Duplication Check Fail : "+e.getMessage());
            throw new FunctionException("Signup Duplication Check Fail : "+e.getMessage());
        }
        return res;
    }

    public void signupUser(String req_id, UserDetailInfo info, ResponseInfo resInfo) throws FunctionException {
        int result = -1;
        String res = null;
        UserInfo userInfo = new UserInfo();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            // insert 3개
            result = userSignupMapper.insertSignupUser(info);
            if(result==1){
                log.info("[Service-UserSignup][signup][signupUser][{}] USER Insert Success  : USER_ID({})",req_id, info.getUser_id());
            } else {    // 에러
                log.info("[Service-UserSignup][signup][signupUser][{}] USER Insert Fail  : USER_ID({}), {}",req_id, info.getUser_id(),result);
                throw new Exception("USER Insert Fail  : USER_ID("+info.getUser_id()+")");
            }
            res = userSignupMapper.getUserIdx(info.getUser_id());
            if(res!=null){
                log.info("[Service-UserSignup][signup][signupUser][{}] Signup USER_IDX Select Success  : USER_ID({}), USER_IDX({})",req_id, info.getUser_id(),res);
                info.setUser_idx(res);
            } else {    // 에러
                log.info("[Service-UserSignup][signup][signupUser][{}] USER_AUTH Insert Fail  : USER_ID({}), {}",req_id, info.getUser_id(),result);
                throw new Exception("USER_AUTH Select Fail  : USER_ID("+info.getUser_id()+")");
            }
            result = userSignupMapper.insertSignupUserProfile(info);
            if(result==1){
                log.info("[Service-UserSignup][signup][signupUser][{}] USER_PROFILE Insert Success  : USER_ID({})",req_id, info.getUser_id());
            } else {    // 에러
                log.info("[Service-UserSignup][signup][signupUser][{}] USER_PROFILE Insert Fail  : USER_ID({}), {}",req_id, info.getUser_id(),result);
                throw new Exception("USER_PROFILE Insert Fail  : USER_ID("+info.getUser_id()+")");
            }
            result = userSignupMapper.insertSignupUserAuth(info);
            if(result==1){
                log.info("[Service-UserSignup][signup][signupUser][{}] USER_AUTH Insert Success  : USER_ID({})",req_id, info.getUser_id());
            } else {    // 에러
                log.info("[Service-UserSignup][signup][signupUser][{}] USER_AUTH Insert Fail  : USER_ID({}), {}",req_id, info.getUser_id(),result);
                throw new Exception("USER_AUTH Insert Fail  : USER_ID("+info.getUser_id()+")");
            }
            transactionManager.commit(status);
            log.info("[Service-UserSignup][signup][signupUser][{}] Signup Insert Success  : USER_ID({}), USER_IDX({})",req_id, info.getUser_id(), res);
            userInfo.setUser_id(info.getUser_id()); userInfo.setUser_idx(res); userInfo.setStatus("0");
            resInfo.setMsg("Signup Success");
            resInfo.setRes_data(gson.toJson(userInfo));
        } catch (Exception e) { // 에러
            log.error("[Service-UserLogin][signup][signupUser][{}] Signup User Fail : {}",req_id,e.getMessage());
            log.error("[Service-UserSignup][signup][signupUser]["+req_id+"] Error PrintStack : ",e);
            transactionManager.rollback(status);
            resInfo.setMsg("Signup Fail : Exception Occurred");
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setRes_data("[Service-UserSignup][signup][signupUser] Signup Fail : "+e.getMessage());
            throw new FunctionException("Login Log Insertion Fail : "+e.getMessage());
        }
    }
}