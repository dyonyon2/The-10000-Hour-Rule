package com.dyonyon.The10000HourRule.service.user;

import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoInfo;
import com.dyonyon.The10000HourRule.mapper.memo.MemoCRUDMapper;
import com.dyonyon.The10000HourRule.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.File;

@Service
@Slf4j
public class MemoCRUDService {

    @Autowired
    private PlatformTransactionManager transactionManager;
    private MemoCRUDMapper memoCRUDMapper;

    @Value("${path.img}")
    String imgPath;

    public MemoCRUDService(MemoCRUDMapper memoCRUDMapper){
        this.memoCRUDMapper = memoCRUDMapper;
    }

    public ResponseInfo createMemo(HttpServletRequest req, MemoInfo memoInfo) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoCRUD][createMemo][{}] Create Memo Started...", req_id);

            // owner_idx 세팅
            setOwnerIdx(req_id, memoInfo, responseInfo);

            // 메모 생성
            createMemoAndGetMemoIdx(req_id, memoInfo, responseInfo);

            log.info("[Service-MemoCRUD][createMemo][{}] Create Memo Success...: Memo({})", req_id, memoInfo.getMemo_idx());
            memoInfo.setStatus("1"); memoInfo.setAccess("-1");
            responseInfo.setRes_data(memoInfo);
            responseInfo.setMsg("Memo Create Success");
        } catch (FunctionException e){
            log.error("[Service-MemoCRUD][createMemo][{}] Create Memo Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][createMemo][{}]  Create Memo Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][createMemo]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Create Memo Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][createMemo] Create Memo Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public void setOwnerIdx(String req_id, MemoInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String ownerIdx = null;
            ownerIdx = memoCRUDMapper.getOwnerIdx(info);
            if(ownerIdx!=null && !ownerIdx.isEmpty()){
                log.info("[Service-MemoCRUD][createMemo][setOwnerIdx][{}] Owner IDX Get Success : Owner ID({}), IDX({})",req_id, info.getOwner_id(), info.getOwner_idx());
                info.setOwner_idx(ownerIdx);
            }
            else {
                throw new Exception("Owner IDX Get Result("+ownerIdx+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][createMemo][setOwnerIdx][{}] Owner IDX Get Failed : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][createMemo][setOwnerIdx]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Create Memo Failed : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][createMemo][setOwnerIdx] Owner IDX Get Failed : "+e.getMessage());
            throw new FunctionException("Insert DB Failed : "+e.getMessage());
        }
    }

    public void createMemoAndGetMemoIdx(String req_id, MemoInfo info, ResponseInfo resInfo) throws FunctionException {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            int memoIdx = -1; int result = -1; int result2 = -1;
            memoIdx = memoCRUDMapper.getMemoIdx();
            if(memoIdx > 0){
                info.setMemo_idx(Integer.toString(memoIdx));
                result = memoCRUDMapper.insertMemo(info);
                if(result==1){
                    result2 = memoCRUDMapper.insertMemoDetail(info);
                    if(result2==1){
                        log.info("[Service-UserManage][createMemo][createMemoAndGetMemoIdx][{}] Memo Create Success : Memo Idx({})",req_id, info.getMemo_idx());
                        transactionManager.commit(status);
                    } else {
                        throw new Exception("MemoDetail Create Result("+result2+")");
                    }
                } else {
                    throw new Exception("Memo Create Result("+result+")");
                }
            } else {
                throw new Exception("MemoIdx Select Result("+memoIdx+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][createMemo][createMemoAndGetMemoIdx][{}] Insert DB Failed : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][createMemo][createMemoAndGetMemoIdx]["+req_id+"] Error PrintStack : ",e);
            transactionManager.rollback(status);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Create Memo Failed : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][createMemo][createMemoAndGetMemoIdx] Insert DB Failed : "+e.getMessage());
            throw new FunctionException("Insert DB Failed : "+e.getMessage());
        }
    }

    // 이미지 파일 저장
    public ResponseInfo saveImageFile(HttpServletRequest req, MemoImageInfo memoImageInfo) {

        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoCRUD][saveImageFile][{}] Save File & Insert DB Started...", req_id);

            // owner_idx 세팅
            setOwnerIdx(req_id, memoImageInfo, responseInfo);
            // 이미지 파일 저장
            saveImage(req_id, memoImageInfo, responseInfo);
            // 이미지 파일 정보 DB에 저장
            insertImageInfo(req_id, memoImageInfo, responseInfo);

            log.info("[Service-MemoCRUD][saveImageFile][{}] Save File & Insert DB Success...", req_id);
        } catch (FunctionException e){
            log.error("[Service-MemoCRUD][saveImageFile][{}] Save File & Insert DB Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][saveImageFile][{}]  Save File & Insert DB Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][saveImageFile]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Save Image File Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][saveImageFile] Save File & Insert DB Failed : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

    public void setOwnerIdx(String req_id, MemoImageInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String ownerIdx = null;
            ownerIdx = memoCRUDMapper.getOwnerIdx2(info);
            if(ownerIdx!=null && !ownerIdx.isEmpty()){
                log.info("[Service-MemoCRUD][createMemo][setOwnerIdx][{}] Owner IDX Get Success : Owner ID({}), IDX({})",req_id, info.getOwner_id(), info.getOwner_idx());
                info.setOwner_idx(ownerIdx);
            }
            else {
                throw new Exception("Owner IDX Get Result("+ownerIdx+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][createMemo][setOwnerIdx][{}] Owner IDX Get Failed : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][createMemo][setOwnerIdx]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Create Memo Failed : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][createMemo][setOwnerIdx] Owner IDX Get Failed : "+e.getMessage());
            throw new FunctionException("Insert DB Failed : "+e.getMessage());
        }
    }

    // 중복 확인 함수
    public void saveImage(String req_id, MemoImageInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            // 이미지 데이터가 존재할 때
            if(info.getFile()!=null) {
                String orgFileName = info.getFile().getOriginalFilename();
                String fileExtension = "";
                if(orgFileName!=null) {
                    int lastIndex = orgFileName.lastIndexOf('.');
                    if (lastIndex >= 0) {
                        fileExtension = "." + orgFileName.substring(lastIndex);
                    }
                }
                info.setFile_name(req_id+fileExtension);
                info.setPath(CommonUtil.getImgPath(imgPath)); // setPath = basePath/YYYYMMDD/HH
                File folder = new File(info.getPath());
                if(!folder.exists())
                    folder.mkdirs();
                log.info("[Service-MemoCRUD][saveImageFile][saveImage][{}] File Name({}) Path({})", req_id, info.getFile_name(), info.getPath());

                File dest = new File(info.getPath() + File.separator + info.getFile_name());
                info.getFile().transferTo(dest);

                resInfo.setMsg("Save File Success : File Name("+info.getFile_name()+")");
                resInfo.setRes_data("[Service-MemoCRUD][saveImageFile][saveImage] Save File Success : File Name("+info.getFile_name()+") Path("+info.getPath()+")");
                log.info("[Service-MemoCRUD][saveImageFile][saveImage][{}] Save File Success...", req_id);
            }
            // 이미지 데이터가 존재하지 않을 때
            else {
                log.info("[Service-MemoCRUD][saveImageFile][saveImage][{}] Save File Started... : {}", req_id, info.getFile());
                log.info("[Service-MemoCRUD][saveImageFile][saveImage][{}] Save File Failed : File Is Null",req_id);
                resInfo.setRes_status("-1");
                resInfo.setMsg("Save File Failed : File Is Null");
                resInfo.setRes_data("[Service-MemoCRUD][saveImageFile][saveImage] Save File Failed : File Is Null");
                resInfo.setErr_code("UN");
                throw new FunctionException("Save File Failed : File Is Null");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][saveImageFile][saveImage][{}] Save File Failed : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][saveImageFile][saveImage]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Save File Failed : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][saveImageFile][saveImage] Save File Failed : "+e.getMessage());
            throw new FunctionException("Save File Failed : "+e.getMessage());
        }
    }
    public void insertImageInfo(String req_id, MemoImageInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = -1;
            result = memoCRUDMapper.insertMemoImage(info);
            if(result == 1){
                log.info("[Service-UserManage][saveImageFile][insertImageInfo][{}] Image Info DB Insert Success : File Name({}) Path({})",req_id, info.getFile_name(), info.getPath());
            } else {
                throw new Exception("Result("+result+")");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][saveImageFile][insertImageInfo][{}] Image Info DB Insert Failed : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][saveImageFile][insertImageInfo]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Image Info DB Insert Failed : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][saveImageFile][insertImageInfo] Image Info DB Insert Failed : "+e.getMessage());
            throw new FunctionException("Image Info DB Insert Failed : "+e.getMessage());
        }
    }
}