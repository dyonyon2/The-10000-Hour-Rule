package com.dyonyon.The10000HourRule.service.user;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.user.MemoImageInfo;
import com.dyonyon.The10000HourRule.domain.user.UserAuthInfo;
import com.dyonyon.The10000HourRule.domain.user.UserDetailInfo;
import com.dyonyon.The10000HourRule.mapper.user.UserManageMapper;
import com.dyonyon.The10000HourRule.util.CommonUtil;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class MemoCRUDService {

    private UserManageMapper userManageMapper;

    @Value("${path.img}")
    String imgPath;

    public MemoCRUDService(UserManageMapper userManageMapper){
        this.userManageMapper = userManageMapper;
    }

    // 이미지 파일 저장
    public ResponseInfo saveImageFile(HttpServletRequest req, MemoImageInfo memoImageInfo) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        // 1. 중복 확인
        try{
            log.info("[Service-MemoCRUD][saveImageFile][{}] Save Started... : {}", req_id, memoImageInfo.getFile().getOriginalFilename());

            memoImageInfo.setFile_name(req_id);
            memoImageInfo.setPath(CommonUtil.getImgPath(imgPath)); // basePath/(user or group)/ID/file
            log.info("[Service-MemoCRUD][saveImageFile][{}] File Name({}) Path({})", req_id, memoImageInfo.getFile_name(),memoImageInfo.getPath());
            log.info("[Service-MemoCRUD][saveImageFile][{}] File Name({}) Path({})", req_id, memoImageInfo.getFile_name(),memoImageInfo.getPath());


//            isDuplication(req_id, key, value, responseInfo);

//        } catch (FunctionException e){
//            log.error("[Service-MemoCRUD][saveImageFile][{}] Check Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][saveImageFile][{}] Check Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][saveImageFile]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Duplication Check Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][saveImageFile] Duplication Check Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    // 중복 확인 함수
    public void isDuplication(String req_id, String key, String value, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = -1;
            switch (key){
                case GlobalConstants.nickname:
                    result = userManageMapper.checkProfileDuplication(key, value);
                    break;
                case GlobalConstants.user_id, GlobalConstants.mail, GlobalConstants.phone:
                    result = userManageMapper.checkAuthDuplication(key, value);
                    break;
                default:
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Duplication Check : Invalid Key");
                    resInfo.setRes_data("[Service-UserManage][checkDuplication][isDuplication] Invalid Key("+key+") : Value("+value+")");
                    throw new FunctionException("Invalid Key("+key+") : Value("+value+")");
            }

            log.info("[Service-UserManage][checkDuplication][isDuplication][{}] Duplicate Check Success : Key({}), Value({}), Count({})",req_id,key,value,result);

            if(result>0) {  // 중복
                resInfo.setRes_status("-1");
                resInfo.setMsg("Duplication Check : Duplicated");
                resInfo.setRes_data("[Service-UserManage][checkDuplication][isDuplication] Duplicated : Key("+key+") Value("+value+")");
            } else if (result < 0) {    // 에러
                throw new Exception("Result(" + result + ")");
            } else {    // 중복 X
                resInfo.setMsg("Duplication Check : Not Duplicated");
                resInfo.setRes_data("Not Duplicated : Key("+key+") Value("+value+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][checkDuplication][isDuplication][{}] Duplication Check Select Failed : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][checkDuplication][isDuplication]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Duplication Check Failed : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][checkDuplication][isDuplication] Duplication Check Select Failed : "+e.getMessage());
            throw new FunctionException("Duplication Check Failed : "+e.getMessage());
        }
    }
}