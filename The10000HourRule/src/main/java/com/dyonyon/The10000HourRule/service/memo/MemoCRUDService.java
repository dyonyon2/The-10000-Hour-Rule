package com.dyonyon.The10000HourRule.service.memo;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoDetailInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoListInfo;
import com.dyonyon.The10000HourRule.mapper.memo.MemoCRUDMapper;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@Slf4j
public class MemoCRUDService {

    @Autowired
    private PlatformTransactionManager transactionManager;
    private MemoCRUDMapper memoCRUDMapper;

    @Value("${path.img}")
    String imgPath;

    @Autowired
    Gson gson = new Gson();

    public MemoCRUDService(MemoCRUDMapper memoCRUDMapper){
        this.memoCRUDMapper = memoCRUDMapper;
    }


    // 메모 생성 : setOwnerIdx, createMemoAndGetMemoIdx
    public ResponseInfo createMemo(HttpServletRequest req, MemoInfo memoInfo) {
        // owner_idx 세팅 -> 메모 생성 및 메모 IDX 세팅
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");
        memoInfo.setReq_id(req_id);

        try{
            log.info("[Service-MemoCRUD][createMemo][{}] Memo Create Started...", req_id);

            // owner_idx 세팅
            setOwnerIdx(req_id, memoInfo, responseInfo);

            // 메모 생성
            createMemoAndGetMemoIdx(req_id, memoInfo, responseInfo);

            log.info("[Service-MemoCRUD][createMemo][{}] Memo Create Success...: Memo({})", req_id, memoInfo.getMemo_idx());
            memoInfo.setStatus("1"); memoInfo.setAccess("-1");
            responseInfo.setRes_data(gson.toJson(memoInfo));
            responseInfo.setMsg("Memo Create Success");
        } catch (FunctionException e){
            log.error("[Service-MemoCRUD][createMemo][{}] Memo Create Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][createMemo][{}] Memo Create Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][createMemo]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Create Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][createMemo] Memo Create Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void setOwnerIdx(String req_id, MemoInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String ownerIdx = null;
            ownerIdx = memoCRUDMapper.getOwnerIdx(info.getMemo_type(), info.getOwner_id());
            if(ownerIdx!=null && !ownerIdx.isEmpty()){
                log.info("[Service-MemoCRUD][createMemo][setOwnerIdx][{}] Owner IDX Get Success : Owner ID({}), IDX({})",req_id, info.getOwner_id(), info.getOwner_idx());
                info.setOwner_idx(ownerIdx);
            }
            else {
                throw new Exception("Owner IDX Get Result("+ownerIdx+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][createMemo][setOwnerIdx][{}] Owner IDX Get Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][createMemo][setOwnerIdx]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Create Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][createMemo][setOwnerIdx] Owner IDX Get Fail : "+e.getMessage());
            throw new FunctionException("Owner IDX Get Fail : "+e.getMessage());
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
            log.error("[Service-UserManage][createMemo][createMemoAndGetMemoIdx][{}] Memo Insert Fail : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][createMemo][createMemoAndGetMemoIdx]["+req_id+"] Error PrintStack : ",e);
            transactionManager.rollback(status);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Create Fail : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][createMemo][createMemoAndGetMemoIdx] Memo Insert Fail : "+e.getMessage());
            throw new FunctionException("Memo Insert Fail : "+e.getMessage());
        }
    }


    // 이미지 파일 저장 : setOwnerIdx, saveImage, insertImageInfo
    public ResponseInfo saveImageFile(HttpServletRequest req, MemoImageInfo memoImageInfo) {
        // owner_idx 세팅 -> 이미지 파일 저장 -> 이미지 파일 정보 DB 저장
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoCRUD][saveImageFile][{}] File Save & Insert Started...", req_id);

            // owner_idx 세팅
            setOwnerIdx(req_id, memoImageInfo, responseInfo);
            // 이미지 파일 저장
            saveImage(req_id, memoImageInfo, responseInfo);
            // 이미지 파일 정보 DB에 저장
            insertImageInfo(req_id, memoImageInfo, responseInfo);

            log.info("[Service-MemoCRUD][saveImageFile][{}] File Save & Insert Success...", req_id);
        } catch (FunctionException e){
            log.error("[Service-MemoCRUD][saveImageFile][{}] File Save & Insert Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][saveImageFile][{}] File Save & Insert Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][saveImageFile]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Image File Save Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][saveImageFile] File Save & Insert Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void setOwnerIdx(String req_id, MemoImageInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String ownerIdx = null;
            ownerIdx = memoCRUDMapper.getOwnerIdx(info.getMemo_type(), info.getOwner_id());
            if(ownerIdx!=null && !ownerIdx.isEmpty()){
                info.setOwner_idx(ownerIdx);
                log.info("[Service-MemoCRUD][saveImageFile][setOwnerIdx][{}] Owner IDX Get Success : Owner ID({}), IDX({})",req_id, info.getOwner_id(), info.getOwner_idx());
            }
            else {
                throw new Exception("Owner IDX Get Result("+ownerIdx+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][saveImageFile][setOwnerIdx][{}] Owner IDX Get Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][saveImageFile][setOwnerIdx]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Image File Save Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][saveImageFile][setOwnerIdx] Owner IDX Get Fail : "+e.getMessage());
            throw new FunctionException("Owner IDX Get Fail : "+e.getMessage());
        }
    }
    public void saveImage(String req_id, MemoImageInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            // 이미지 데이터가 존재할 때
            if(info.getFile()!=null) {
                String orgFileName = info.getFile().getOriginalFilename();
                String fileExtension = "";
                if(orgFileName!=null) {
                    int lastIndex = orgFileName.lastIndexOf('.');
                    if (lastIndex >= 0) {
                        fileExtension = orgFileName.substring(lastIndex);
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

                // Return 데이터 세팅
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("memo_idx", info.getMemo_idx());
                jsonObject.put("memo_type", info.getMemo_type());
                jsonObject.put("user_id", info.getUser_id());
                jsonObject.put("owner_id", info.getOwner_id());
                jsonObject.put("path", info.getPath());
                jsonObject.put("file_name", info.getFile_name());

                resInfo.setRes_data(jsonObject.toString());
                log.info("[Service-MemoCRUD][saveImageFile][saveImage][{}] Save File Success...", req_id);
            }
            // 이미지 데이터가 존재하지 않을 때
            else {
                log.info("[Service-MemoCRUD][saveImageFile][saveImage][{}] File Save Fail : File Is Null",req_id);
                resInfo.setRes_status("-1");
                resInfo.setMsg("Image File Save Fail : File Is Null");
                resInfo.setRes_data("[Service-MemoCRUD][saveImageFile][saveImage] Save File Fail : File Is Null");
                resInfo.setErr_code("UN");
                throw new FunctionException("File Save Fail : File Is Null");
            }
        } catch (Exception e) {
            log.error("[Service-UserManage][saveImageFile][saveImage][{}] File Save Fail : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][saveImageFile][saveImage]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Image File Save Fail : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][saveImageFile][saveImage] File Save Fail : "+e.getMessage());
            throw new FunctionException("File Save Fail : "+e.getMessage());
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
            log.error("[Service-UserManage][saveImageFile][insertImageInfo][{}] Image Info DB Insert Fail : {}",req_id,e.getMessage());
            log.error("[Service-UserManage][saveImageFile][insertImageInfo]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Image File Save Fail : Exception Occurred");
            resInfo.setRes_data("[Service-UserManage][saveImageFile][insertImageInfo] Image Info DB Insert Fail : "+e.getMessage());
            throw new FunctionException("Image Info Insert Fail : "+e.getMessage());
        }
    }


    // 메모 수정 : setOwnerIdx, updateMemo
    public ResponseInfo updateMemo(HttpServletRequest req, MemoDetailInfo memoDetailInfo) {
        // owner_idx 세팅, 메모 수정
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");
        memoDetailInfo.setReq_id(req_id);

        try{
            log.info("[Service-MemoCRUD][updateMemo][{}] Memo Update Started...", req_id);

            // owner_idx 세팅
            setOwnerIdx(req_id, memoDetailInfo, responseInfo);

            // 메모 수정 update
            updateMemo(req_id, memoDetailInfo, responseInfo);

            log.info("[Service-MemoCRUD][updateMemo][{}] Memo Update Success...: Memo({})", req_id, memoDetailInfo.getMemo_idx());
            responseInfo.setMsg("Memo Update Success");
        } catch (FunctionException e){
            log.error("[Service-MemoCRUD][updateMemo][{}] Memo Update Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][updateMemo][{}] Memo Update Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][updateMemo]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Update Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][updateMemo] Memo Update Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void setOwnerIdx(String req_id, MemoDetailInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String ownerIdx = null;
            ownerIdx = memoCRUDMapper.getOwnerIdx(info.getMemo_type(), info.getOwner_id());
            if(ownerIdx!=null && !ownerIdx.isEmpty()){
                info.setOwner_idx(ownerIdx);
                log.info("[Service-MemoCRUD][updateMemo][setOwnerIdx][{}] Owner IDX Get Success : Owner ID({}), IDX({})",req_id, info.getOwner_id(), info.getOwner_idx());
            }
            else {
                throw new Exception("Owner IDX Get Result("+ownerIdx+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][updateMemo][setOwnerIdx][{}] Owner IDX Get Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][updateMemo][setOwnerIdx]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Update Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][updateMemo][setOwnerIdx] Owner IDX Get Fail : "+e.getMessage());
            throw new FunctionException("Owner IDX Get Fail : "+e.getMessage());
        }
    }
    public void updateMemo(String req_id, MemoDetailInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = memoCRUDMapper.updateMemo(info);
            if(result==1){
                log.info("[Service-MemoCRUD][updateMemo][updateMemo][{}] Memo Update Success : Owner ID({}), IDX({})",req_id, info.getOwner_id(), info.getOwner_idx());
                resInfo.setRes_data(gson.toJson(info));
            }
            else {
                throw new Exception("Memo Update Result("+result+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][updateMemo][updateMemo][{}] Memo Update Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][updateMemo][updateMemo]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Update Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][updateMemo][updateMemo] Memo Update Fail : "+e.getMessage());
            throw new FunctionException("Memo Update Fail : "+e.getMessage());
        }
    }


    // 메모 읽기 : setOwnerIdx, readMemoDetail
    public ResponseInfo readMemo(HttpServletRequest req, MemoInfo memoInfo) {
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoCRUD][readMemo][{}] Memo Read Started...", req_id);

            // owner_idx 세팅
            setOwnerIdx2(req_id, memoInfo, responseInfo);

            // 메모 읽기 read
            readMemoDetail(req_id, memoInfo, responseInfo);

            log.info("[Service-MemoCRUD][readMemo][{}] Memo Read Success...: Memo({})", req_id, memoInfo.getMemo_idx());
            responseInfo.setMsg("Memo Read Success");
        } catch (FunctionException e){
            log.error("[Service-MemoCRUD][readMemo][{}] Memo Read Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][readMemo][{}] Memo Read Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][readMemo]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Read Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][readMemo] Memo Read Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void setOwnerIdx2(String req_id, MemoInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String ownerIdx = null;
            ownerIdx = memoCRUDMapper.getOwnerIdx(info.getMemo_type(), info.getOwner_id());
            if(ownerIdx!=null && !ownerIdx.isEmpty()){
                log.info("[Service-MemoCRUD][readMemo][setOwnerIdx][{}] Owner IDX Get Success : Owner ID({}), IDX({})",req_id, info.getOwner_id(), info.getOwner_idx());
                info.setOwner_idx(ownerIdx);
            }
            else {
                throw new Exception("Owner IDX Get Result("+ownerIdx+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][readMemo][setOwnerIdx][{}] Owner IDX Get Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][readMemo][setOwnerIdx]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Read Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][readMemo][setOwnerIdx] Owner IDX Get Fail : "+e.getMessage());
            throw new FunctionException("Owner IDX Get Fail : "+e.getMessage());
        }
    }
    public void readMemoDetail(String req_id, MemoInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            MemoDetailInfo memoDetailInfo = memoCRUDMapper.readMemo(info);
            if(memoDetailInfo!=null){
                log.info("[Service-MemoCRUD][readMemo][readMemoDetail][{}] Memo Read Success : {}",req_id, memoDetailInfo);
                resInfo.setRes_data(gson.toJson(memoDetailInfo));
            }
            else {
                throw new Exception("Memo Read Result("+memoDetailInfo+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][readMemo][readMemoDetail][{}] Memo Read Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][readMemo][readMemoDetail]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Read Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][readMemo][readMemoDetail] Memo Read Fail : "+e.getMessage());
            throw new FunctionException("Memo Read Fail : "+e.getMessage());
        }
    }


    // 메모 삭제 : setOwnerIdx, deleteMemo
    public ResponseInfo deleteMemo(HttpServletRequest req, MemoInfo memoInfo) {
        // owner_idx 세팅, 메모 수정
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoCRUD][deleteMemo][{}] Memo Delete Started...", req_id);

            // owner_idx 세팅
            setOwnerIdx3(req_id, memoInfo, responseInfo);

            // 메모 삭제 (Status값 변경 => -1)
            deleteMemo(req_id, memoInfo, responseInfo);

            log.info("[Service-MemoCRUD][deleteMemo][{}] Memo Delete Success...: Memo({})", req_id, memoInfo.getMemo_idx());
            responseInfo.setMsg("Memo Delete Success");
        } catch (FunctionException e){
            log.error("[Service-MemoCRUD][deleteMemo][{}] Memo Delete Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][deleteMemo][{}] Memo Delete Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][deleteMemo]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Delete Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][deleteMemo] Memo Delete Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void setOwnerIdx3(String req_id, MemoInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String ownerIdx = null;
            ownerIdx = memoCRUDMapper.getOwnerIdx(info.getMemo_type(), info.getOwner_id());
            if(ownerIdx!=null && !ownerIdx.isEmpty()){
                log.info("[Service-MemoCRUD][deleteMemo][setOwnerIdx][{}] Owner IDX Get Success : Owner ID({}), IDX({})",req_id, info.getOwner_id(), info.getOwner_idx());
                info.setOwner_idx(ownerIdx);
            }
            else {
                throw new Exception("Owner IDX Get Result("+ownerIdx+")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][deleteMemo][setOwnerIdx][{}] Owner IDX Get Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][deleteMemo][setOwnerIdx]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Delete Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][deleteMemo][setOwnerIdx] Owner IDX Get Fail : "+e.getMessage());
            throw new FunctionException("Owner IDX Get Fail : "+e.getMessage());
        }
    }
    public void deleteMemo(String req_id, MemoInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            info.setStatus(GlobalConstants.CONTENT_STATUS_DELETE);
            int result = memoCRUDMapper.updateStatusMemo(info);
            if(result==1)
                log.info("[Service-MemoCRUD][deleteMemo][updateStatusMemo][{}] Memo Delete Success : Idx ({}) Type({})",req_id, info.getMemo_idx(),info.getMemo_type());
            else
                throw new Exception("Memo Status Update Result("+result+")");
            resInfo.setRes_data(gson.toJson(info));
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][deleteMemo][updateStatusMemo][{}] Memo Delete Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][deleteMemo][updateStatusMemo]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Delete Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][deleteMemo][updateStatusMemo] Memo Delete Fail : "+e.getMessage());
            throw new FunctionException("Memo Status Update Fail : "+e.getMessage());
        }
    }


    //메모 목록 읽기 : readOwn/Group/FollowMemoList
    public ResponseInfo readMemoList(HttpServletRequest req, String user_id, String target) {
        // owner_idx 세팅, 메모 수정
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoCRUD][readMemoList][{}] Memo List Read Started... User ({}) Target({})", req_id, user_id, target);
            // 메모 정보를 Array로 담아서 전달
            switch (target){
                // 1. 내 소유 메모 목록
                case GlobalConstants.CONTENT_LIST_OWN:
                    readOwnMemoList(req_id, user_id, responseInfo);
                    break;
                // 2. 내 그룹 메모 목록
                case GlobalConstants.CONTENT_LIST_GROUP:
                    readGroupMemoList(req_id, user_id, responseInfo);
                    break;
                // 3. 내가 팔로우한 메모 목록
                case GlobalConstants.CONTENT_LIST_FOLLOW:
                    readFollowMemoList(req_id, user_id, responseInfo);
                    break;
                default:
                    responseInfo.setRes_status("-1");
                    responseInfo.setMsg("Invalid Target : "+ target);
                    responseInfo.setRes_data("[Service-MemoCRUD][readMemoList] Invalid Target("+target+")");
                    throw new FunctionException("Invalid Target("+target+")");
            }

            log.info("[Service-MemoCRUD][readMemoList][{}] Memo List Read Success...", req_id);
            responseInfo.setMsg("Memo List Read Success");
        } catch (FunctionException e){
            log.error("[Service-MemoCRUD][readMemoList][{}] Memo List Read Fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoCRUD][readMemoList][{}] Memo List Read Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][readMemoList]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo List Read Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoCRUD][readMemoList] Memo List Read Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void readOwnMemoList(String req_id, String user_id, ResponseInfo resInfo) throws FunctionException {
        try {
            MemoListInfo[] list = memoCRUDMapper.readOwnMemoList(user_id);
            if(list!=null)
                log.info("[Service-MemoCRUD][readMemoList][readOwnMemoList][{}] Memo List Read Success : List Count ({})",req_id, list.length);
            else
                throw new Exception("Memo List Read Result("+list+")");
            resInfo.setRes_data(gson.toJson(list));
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][readMemoList][readOwnMemoList][{}] Memo List Read Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][readMemoList][readOwnMemoList]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo List Read Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][readMemoList][readOwnMemoList] Memo List Read Fail : "+e.getMessage());
            throw new FunctionException("Memo Status Update Fail : "+e.getMessage());
        }
    }
    public void readGroupMemoList(String req_id, String user_id, ResponseInfo resInfo) throws FunctionException {
        try {
            MemoListInfo[] list = memoCRUDMapper.readGroupMemoList(user_id);
            if(list!=null)
                log.info("[Service-MemoCRUD][readMemoList][readGroupMemoList][{}] Memo List Read Success : List Count ({})",req_id, list.length);
            else
                throw new Exception("Memo List Read Result("+list+")");
            resInfo.setRes_data(gson.toJson(list));
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][readMemoList][readGroupMemoList][{}] Memo List Read Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][readMemoList][readGroupMemoList]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo List Read Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][readMemoList][readGroupMemoList] Memo List Read Fail : "+e.getMessage());
            throw new FunctionException("Memo Status Update Fail : "+e.getMessage());
        }
    }
    public void readFollowMemoList(String req_id, String user_id, ResponseInfo resInfo) throws FunctionException {
        try {
            MemoListInfo[] list = memoCRUDMapper.readFollowMemoList(user_id);
            if(list!=null)
                log.info("[Service-MemoCRUD][readMemoList][readFollowMemoList][{}] Memo List Read Success : List Count ({})",req_id, list.length);
            else
                throw new Exception("Memo List Read Result("+list+")");
            resInfo.setRes_data(gson.toJson(list));
        } catch (Exception e) {
            log.error("[Service-MemoCRUD][readMemoList][readFollowMemoList][{}] Memo List Read Fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoCRUD][readMemoList][readFollowMemoList]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo List Read Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoCRUD][readMemoList][readFollowMemoList] Memo List Read Fail : "+e.getMessage());
            throw new FunctionException("Memo Status Update Fail : "+e.getMessage());
        }
    }
}