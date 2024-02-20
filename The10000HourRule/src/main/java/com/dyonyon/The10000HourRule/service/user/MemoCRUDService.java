package com.dyonyon.The10000HourRule.service.user;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
import com.dyonyon.The10000HourRule.mapper.user.UserManageMapper;
import com.dyonyon.The10000HourRule.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

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

        try{
            if(memoImageInfo.getFile()!=null) {
                log.info("[Service-MemoCRUD][saveImageFile][{}] Save File Started... : {}", req_id, memoImageInfo.getFile().getOriginalFilename());

                String orgFileName = memoImageInfo.getFile().getOriginalFilename();
                String fileExtension = ".txt";
                int lastIndex = orgFileName.lastIndexOf('.');
                if (lastIndex >= 0) {
                    fileExtension = "."+orgFileName.substring(lastIndex);
                }
                memoImageInfo.setFile_name(req_id);
                memoImageInfo.setPath(CommonUtil.getImgPath(imgPath)); // setPath = basePath/YYYYMMDD/HH
                File folder = new File(memoImageInfo.getPath());
                if(!folder.exists())
                    folder.mkdirs();
                log.info("[Service-MemoCRUD][saveImageFile][{}] File Name({}) Path({})", req_id, memoImageInfo.getFile_name(), memoImageInfo.getPath());

                File dest = new File(memoImageInfo.getPath() + File.separator + memoImageInfo.getFile_name() + fileExtension);
                memoImageInfo.getFile().transferTo(dest);

                responseInfo.setMsg("Save File Success : File Name("+memoImageInfo.getFile_name()+")");
                responseInfo.setRes_data("[Service-MemoCRUD][saveImageFile] Save File Success : File Name("+memoImageInfo.getFile_name()+") Path("+memoImageInfo.getPath()+")");
                log.info("[Service-MemoCRUD][saveImageFile][{}] Save File Success...", req_id);
            } else {
                log.info("[Service-MemoCRUD][saveImageFile][{}] Save File Started... : {}", req_id, memoImageInfo.getFile());
                log.info("[Service-MemoCRUD][saveImageFile][{}] Save File Failed : File Is Null",req_id);
                responseInfo.setRes_status("-1");
                responseInfo.setMsg("Save File Failed : File Is Null");
                responseInfo.setRes_data("[Service-MemoCRUD][saveImageFile] Save File Failed : File Is Null");
                responseInfo.setErr_code("UN");
            }
        } catch (Exception e){
            log.error("[Service-MemoCRUD][saveImageFile][{}]  Save File Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][saveImageFile]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Save File Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][saveImageFile] Save File Failed : "+e.getMessage());
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