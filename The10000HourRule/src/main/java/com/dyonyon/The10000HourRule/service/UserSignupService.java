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
public class UserSignupService {

    private UserSignupMapper userSignupMapper;

    public UserSignupService(UserSignupMapper userSignupMapper){
        this.userSignupMapper = userSignupMapper;
    }

    public ResponseInfo signup(HttpServletRequest req, UserDetailInfo userDetailInfo) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_code("000000");
        UserInfo resinfo = new UserInfo();

        try{
            log.info("[Service-UserSignup][signup][{}] Signup Started...", req_id);

            // 체크 : 필수 값, 규칙 체크, 중복 체크
            // 1. 필수 값 체크
            // isNull => true (null), false(null X)
            if(CommonUtil.isNull(GlobalConstants.user_id, userDetailInfo.getUser_id())||
                    CommonUtil.isNull(GlobalConstants.pw, userDetailInfo.getPw())||CommonUtil.isNull(GlobalConstants.name, userDetailInfo.getName())||
                    CommonUtil.isNull(GlobalConstants.nickname, userDetailInfo.getNickname())||CommonUtil.isNull(GlobalConstants.sex, userDetailInfo.getSex())||
                    CommonUtil.isNull(GlobalConstants.birth, userDetailInfo.getBirth())||CommonUtil.isNull(GlobalConstants.region, userDetailInfo.getRegion())||
                    CommonUtil.isNull(GlobalConstants.phone, userDetailInfo.getPhone())||CommonUtil.isNull(GlobalConstants.mail, userDetailInfo.getMail())){
                log.info("[Service-UserSignup][signup][{}] Signup Failed : Required Data Is Missing", req_id);
                responseInfo.setMsg("Signup Failed : Required Data Is Missing");
                return responseInfo;
            }
            log.info("[Service-UserSignup][signup][{}] Signup Check 1 Success(Required Data)", req_id);

            // 2. 규칙 체크. 핸드폰/이메일은 인증해야해서 규칙을 무조건 지켜야함. (아이디, 닉네임, 이름, 핸드폰, 이메일, PW, 생일)
            // isFormat => true (포맷), false (포맷 X)
            if(!CommonUtil.isFormat(GlobalConstants.user_id,userDetailInfo.getUser_id())||    // true : 포맷 안맞음, false : 포맷임
                    !CommonUtil.isFormat(GlobalConstants.nickname,userDetailInfo.getNickname())||!CommonUtil.isFormat(GlobalConstants.name,userDetailInfo.getName())||
                    !CommonUtil.isFormat(GlobalConstants.phone,userDetailInfo.getPhone())||!CommonUtil.isFormat(GlobalConstants.mail,userDetailInfo.getMail())||
                    !CommonUtil.isFormat(GlobalConstants.pw,userDetailInfo.getPw())||!CommonUtil.isFormat(GlobalConstants.birth,userDetailInfo.getBirth())) {
                log.info("[Service-UserSignup][signup][{}] Signup Failed : There Is Incorrect Format Data", req_id);
                responseInfo.setMsg("Signup Failed : There Is Incorrect Format Data");
                return responseInfo;
            }
            log.info("[Service-UserSignup][signup][{}] Signup Check 2 Success(Data Format)", req_id);

            // 3. 중복 체크 => 2중 체크! 원래 회원가입 페이지에서 사전 진행. (아이디, 핸드폰, 이메일, 닉네임)
            // duplicateCheck => true (중복), false(중복X)
            if(isDuplication(req_id,GlobalConstants.user_id,userDetailInfo.getUser_id(),responseInfo)|| 
                    isDuplication(req_id,GlobalConstants.phone,userDetailInfo.getPhone(),responseInfo)||
                    isDuplication(req_id,GlobalConstants.mail,userDetailInfo.getMail(),responseInfo)||
                    isDuplication(req_id,GlobalConstants.nickname,userDetailInfo.getNickname(),responseInfo)){
                log.info("[Service-UserSignup][signup][{}] Signup Failed : There Is Duplicate Data", req_id);
                if(!"-1".equals(responseInfo.getStatus())){  // isDuplication 함수 내의 쿼리 오류는 함수 내에서 세팅.
                    responseInfo.setMsg("Signup Failed : There Is Duplicate Data"); // 쿼리 오류가 아닌 중복일 때
                }
                return responseInfo;
            }
            log.info("[Service-UserSignup][signup][{}] Signup Check 3 Success(Data Duplication)", req_id);

            // 4. 회원 정보 insert
            String user_idx = signupUser(req_id, userDetailInfo, responseInfo);
            if(user_idx != null){
                log.info("[Service-UserSignup][signup][{}] Signup Success...",req_id,userDetailInfo.getUser_id(),user_idx);
                resinfo.setUser_id(userDetailInfo.getUser_id()); resinfo.setUser_idx(user_idx); resinfo.setStatus("0");
                responseInfo.setMsg("Signup Success");
                responseInfo.setRes_data(resinfo);
            } else { // Insert Fail = Exception
                log.info("[Service-UserSignup][signup][{}] Signup Failed : {}",req_id,responseInfo.getRes_data());
                responseInfo.setMsg("Signup Failed : Signup Info Insert Failed");
                responseInfo.setStatus("-1");
            }
            return responseInfo;


        } catch (Exception e){
            log.error("[Service-UserSignup][signup][{}] Signup Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            responseInfo.setStatus("-1");
            responseInfo.setMsg("Signup Failed : Unknown Error Occurred");
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
                default:
                    result = userSignupMapper.checkAuthDuplication(key, value);
                    break;
            }
            log.info("[Service-UserSignup][isDuplication][{}] Duplicate Check Select Successed : {} = {} count({})",req_id,key,value,result);
            if(result>0)
                res = true;
        } catch (Exception e) {
            log.info("[Service-UserLogin][isDuplication][{}] Duplicate Check Select Failed : {}",req_id,e.getMessage());
            resInfo.setMsg("Signup Failed : Unknown Error Occurred");
            resInfo.setRes_data(e.getMessage());
            resInfo.setStatus("-1");
        }
        return res;
    }

    @Transactional
    public String signupUser(String req_id, UserDetailInfo info, ResponseInfo resInfo){
        int result = -1;
        String res = null;
        try {
            // insert 3개
            result = userSignupMapper.insertSignupUser(info);
            if(result==1){
                log.info("[Service-UserSignup][signupUser][{}] USER Insert Successed  : USER_ID({})",req_id, info.getUser_id());
            } else {
                log.info("[Service-UserSignup][signupUser][{}] USER Insert Failed  : USER_ID({}), {}",req_id, info.getUser_id(),result);
                throw new Exception("USER Insert Failed  : USER_ID("+info.getUser_id()+")");
            }
            res = userSignupMapper.getUserIdx(info.getUser_id());
            if(res!=null){
                log.info("[Service-UserSignup][signupUser][{}] Signup USER_IDX Select Successed  : USER_ID({}), USER_IDX({})",req_id, info.getUser_id(),res);
                info.setUser_idx(res);
            } else {
                log.info("[Service-UserSignup][signupUser][{}] USER_AUTH Insert Failed  : USER_ID({}), {}",req_id, info.getUser_id(),result);
                throw new Exception("USER_AUTH Select Failed  : USER_ID("+info.getUser_id()+")");
            }
            result = userSignupMapper.insertSignupUserProfile(info);
            if(result==1){
                log.info("[Service-UserSignup][signupUser][{}] USER_PROFILE Insert Successed  : USER_ID({})",req_id, info.getUser_id());
            } else {
                log.info("[Service-UserSignup][signupUser][{}] USER_PROFILE Insert Failed  : USER_ID({}), {}",req_id, info.getUser_id(),result);
                throw new Exception("USER_PROFILE Insert Failed  : USER_ID("+info.getUser_id()+")");
            }
            result = userSignupMapper.insertSignupUserAuth(info);
            if(result==1){
                log.info("[Service-UserSignup][signupUser][{}] USER_AUTH Insert Successed  : USER_ID({})",req_id, info.getUser_id());
            } else {
                log.info("[Service-UserSignup][signupUser][{}] USER_AUTH Insert Failed  : USER_ID({}), {}",req_id, info.getUser_id(),result);
                throw new Exception("USER_AUTH Insert Failed  : USER_ID("+info.getUser_id()+")");
            }
            log.info("[Service-UserSignup][signupUser][{}] Signup Insert Successed  : USER_ID({}), USER_IDX({})",req_id, info.getUser_id(), res);
        } catch (Exception e) {
            log.info("[Service-UserLogin][signupUser][{}] Signup User Failed : {}",req_id,e.getMessage());
            resInfo.setMsg("Signup Failed : Unknown Error Occurred");
            resInfo.setRes_data(e.getMessage());
            resInfo.setStatus("-1");
        }
        return res;
    }
}