package com.dyonyon.The10000HourRule.service.memo;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoDetailInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoInfo;
import com.dyonyon.The10000HourRule.mapper.memo.MemoManageMapper;
import com.dyonyon.The10000HourRule.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Service
@Slf4j
public class MemoManageService {

    @Autowired
    private PlatformTransactionManager transactionManager;
    private MemoManageMapper memoManageMapper;

    @Value("${path.img}")
    String imgPath;

    public MemoManageService(MemoManageMapper memoManageMapper){
        this.memoManageMapper = memoManageMapper;
    }


    // 메모 공유 키 생성 : checkMemoOwner, updateKey
    public ResponseInfo updateKey(HttpServletRequest req, MemoDetailInfo memoDetailInfo) {
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoManage][createSharedKey][{}] Memo Shared Key Create Started...", req_id);

            // 메모 Owner 인지 확인
            checkMemoOwner(req_id, memoDetailInfo, responseInfo);
            // 메모 공유 키 생성
            updateKey(req_id, memoDetailInfo, responseInfo);

            log.info("[Service-MemoManage][createSharedKey][{}] Memo Shared Key Create Success...: Memo({})", req_id, memoDetailInfo.getMemo_idx());
            responseInfo.setMsg("Memo Shared Key Create Success");
        } catch (FunctionException e){
            log.error("[Service-MemoManage][createSharedKey][{}] Memo Shared Key Create fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoManage][createSharedKey][{}] Memo Shared Key Create fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][createSharedKey]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Shared Key Create fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoManage][createSharedKey] Memo Shared Key Create fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void checkMemoOwner(String req_id, MemoDetailInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String type = info.getMemo_type();
            String owner_id=null;
            switch(type){
                case GlobalConstants.CONTENT_TYPE_USER :
                    owner_id = memoManageMapper.getOwnerId(info);
                    break;
                case GlobalConstants.CONTENT_TYPE_GROUP:
                    owner_id = memoManageMapper.getGroupOwnerId(info);
                    break;
                default:
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Memo Shared Key Create Fail : Invalid Memo Type("+type+")");
                    resInfo.setRes_data("[Service-MemoManage][createSharedKey][checkMemoOwner] Invalid Memo Type("+type+")");
                    throw new FunctionException("Memo Owner Check fail : Invalid Memo Type("+type+")");
            }

            // User가 Owner일 때!
            if(owner_id!=null && owner_id.equals(info.getUser_id())){
                // Memo(U)일 때 User ID = Memo 의 Owner ID
                if(type.equals(GlobalConstants.CONTENT_TYPE_USER))
                    log.info("[Service-MemoManage][createSharedKey][checkMemoOwner][{}] Memo({}) Owner Check Success : User({}) Owner({})",req_id, type, info.getUser_id() ,owner_id);
                // Memo(G)일 때 User ID = Memo 의 소유자인 Group 의 Owner ID
                else if(type.equals(GlobalConstants.CONTENT_TYPE_GROUP))
                    log.info("[Service-MemoManage][createSharedKey][checkMemoOwner][{}] Memo({}) Owner Check Success : User({}) Group({})'s Owner({})",req_id, type, info.getUser_id(), info.getOwner_id() ,owner_id);
            }
            // User가 Owner가 아닐 때!
            else {
                resInfo.setRes_status("-1");
                resInfo.setMsg("Memo Shared Key Create Fail : Invalid User");
                if (type.equals(GlobalConstants.CONTENT_TYPE_USER)) {
                    log.info("[Service-MemoManage][createSharedKey][checkMemoOwner][{}] Memo({}) Owner Check fail : User({}) Owner({})", req_id, type, info.getUser_id(), owner_id);
                    resInfo.setRes_data("[Service-MemoManage][createSharedKey][checkMemoOwner] Memo(" + type + ") Owner Check fail : Invalid User(" + info.getUser_id() + ") Owner(" + owner_id + ")");
                    throw new FunctionException("Memo(" + type + ") Owner Check fail : Invalid User(" + info.getUser_id() + ") Owner(" + owner_id + ")");
                } else if (type.equals(GlobalConstants.CONTENT_TYPE_GROUP)) {
                    log.info("[Service-MemoManage][createSharedKey][checkMemoOwner][{}] Memo({}) Owner Check fail : User({}) Group({})'s Owner({})", req_id, type, info.getUser_id(), info.getOwner_id(), owner_id);
                    resInfo.setRes_data("[Service-MemoManage][createSharedKey][checkMemoOwner] Memo(" + type + ") Owner Check fail : Invalid User(" + info.getUser_id() + ") Group(" + info.getOwner_id() + ")'s Owner(" + owner_id + ")");
                    throw new FunctionException("Memo(" + type + ") Owner Check fail : Invalid User(" + info.getUser_id() + ") Group(" + info.getOwner_id() + ")'s Owner(" + owner_id + ")");
                }
            }
        } catch (Exception e) {
            log.error("[Service-MemoManage][createSharedKey][checkMemoOwner][{}] Owner ID Get fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][createSharedKey][checkMemoOwner]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Shared Key Create fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][createSharedKey][checkMemoOwner] Owner ID Get fail : "+e.getMessage());
            throw new FunctionException("Owner ID Get fail : "+e.getMessage());
        }
    }
    public void updateKey(String req_id, MemoDetailInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String sharedKey = CommonUtil.getSharedKey();
            info.setShared_key(sharedKey);
            int result = memoManageMapper.updateSharedKey(info);
            if(result == 1)
                log.info("[Service-MemoManage][createSharedKey][updateSharedKey][{}] Memo Shared Key Insert Success : Memo IDX({}), Shared Key({})",req_id, info.getMemo_idx(), info.getShared_key());
            else
                throw new Exception("Shared Key Insert Result("+result+")");
            resInfo.setRes_data(info);
        } catch (Exception e) {
            log.error("[Service-MemoManage][createSharedKey][updateSharedKey][{}] Memo Shared Key Insert fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][createSharedKey][updateSharedKey]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Shared Key Create fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][createSharedKey][updateSharedKey] Memo Shared Key Insert fail : "+e.getMessage());
            throw new FunctionException("Memo Shared Key Insert fail : "+e.getMessage());
        }
    }


    // 메모 공유키 삭제 : checkMemoOwner2, deleteKey
    public ResponseInfo deleteSharedKey(HttpServletRequest req, MemoDetailInfo memoDetailInfo) {
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoManage][deleteSharedKey][{}] Memo Shared Key Delete Started...", req_id);

            // 메모 Owner 인지 확인
            checkMemoOwner2(req_id, memoDetailInfo, responseInfo);
            // 메모 공유 키 생성
            deleteKey(req_id, memoDetailInfo, responseInfo);

            log.info("[Service-MemoManage][deleteSharedKey][{}] Memo Shared Key Delete Success...: Memo({})", req_id, memoDetailInfo.getMemo_idx());
            responseInfo.setMsg("Memo Create Success");
        } catch (FunctionException e){
            log.error("[Service-MemoManage][deleteSharedKey][{}] Memo Shared Key Delete fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoManage][deleteSharedKey][{}] Memo Shared Key Delete fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][deleteSharedKey]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Shared Key Delete fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoManage][deleteSharedKey] Memo Shared Key Delete fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void checkMemoOwner2(String req_id, MemoDetailInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String type = info.getMemo_type();
            String owner_id=null;
            switch(type){
                case GlobalConstants.CONTENT_TYPE_USER :
                    owner_id = memoManageMapper.getOwnerId(info);
                    break;
                case GlobalConstants.CONTENT_TYPE_GROUP:
                    owner_id = memoManageMapper.getGroupOwnerId(info);
                    break;
                default:
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Memo Shared Key Delete Fail : Invalid Memo Type("+type+")");
                    resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][checkMemoOwner] Invalid Memo Type("+type+")");
                    throw new FunctionException("Memo Owner Check fail : Invalid Memo Type("+type+")");
            }

            // User가 Owner일 때!
            if(owner_id!=null && owner_id.equals(info.getUser_id())){
                // Memo(U)일 때 User ID = Memo 의 Owner ID
                if(type.equals(GlobalConstants.CONTENT_TYPE_USER))
                    log.info("[Service-MemoManage][deleteSharedKey][checkMemoOwner][{}] Memo({}) Owner Check Success : User({}) Owner({})",req_id, type, info.getUser_id() ,owner_id);
                    // Memo(G)일 때 User ID = Memo 의 소유자인 Group 의 Owner ID
                else if(type.equals(GlobalConstants.CONTENT_TYPE_GROUP))
                    log.info("[Service-MemoManage][deleteSharedKey][checkMemoOwner][{}] Memo({}) Owner Check Success : User({}) Group({})'s Owner({})",req_id, type, info.getUser_id(), info.getOwner_id() ,owner_id);
            }
            // User가 Owner가 아닐 때!
            else {
                resInfo.setRes_status("-1");
                resInfo.setMsg("Memo Shared Key Delete Fail : Invalid User");
                if (type.equals(GlobalConstants.CONTENT_TYPE_USER)) {
                    log.info("[Service-MemoManage][deleteSharedKey][checkMemoOwner][{}] Memo({}) Owner Check fail : User({}) Owner({})", req_id, type, info.getUser_id(), owner_id);
                    resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][checkMemoOwner] Memo(" + type + ") Owner Check fail : Invalid User(" + info.getUser_id() + ") Owner(" + owner_id + ")");
                    throw new FunctionException("Memo(" + type + ") Owner Check fail : Invalid User(" + info.getUser_id() + ") Owner(" + owner_id + ")");
                } else if (type.equals(GlobalConstants.CONTENT_TYPE_GROUP)) {
                    log.info("[Service-MemoManage][deleteSharedKey][checkMemoOwner][{}] Memo({}) Owner Check fail : User({}) Group({})'s Owner({})", req_id, type, info.getUser_id(), info.getOwner_id(), owner_id);
                    resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][checkMemoOwner] Memo(" + type + ") Owner Check fail : Invalid User(" + info.getUser_id() + ") Group(" + info.getOwner_id() + ")'s Owner(" + owner_id + ")");
                    throw new FunctionException("Memo(" + type + ") Owner Check fail : Invalid User(" + info.getUser_id() + ") Group(" + info.getOwner_id() + ")'s Owner(" + owner_id + ")");
                }
            }
        } catch (Exception e) {
            log.error("[Service-MemoManage][deleteSharedKey][checkMemoOwner][{}] Owner ID Get fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][deleteSharedKey][checkMemoOwner]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Shared Key Delete fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][checkMemoOwner] Owner ID Get fail : "+e.getMessage());
            throw new FunctionException("Owner ID Get fail : "+e.getMessage());
        }
    }
    public void deleteKey(String req_id, MemoDetailInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = memoManageMapper.deleteSharedKey(info);
            if(result == 1)
                log.info("[Service-MemoManage][deleteSharedKey][deleteKey][{}] Memo Shared Key Delete Success : Memo IDX({}), Shared Key(NULL)",req_id, info.getMemo_idx());
            else
                throw new Exception("Shared Key Delete Result("+result+")");
            resInfo.setRes_data(info);
        } catch (Exception e) {
            log.error("[Service-MemoManage][deleteSharedKey][deleteKey][{}] Memo Shared Key Delete fail : {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][deleteSharedKey][deleteKey]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Shared Key Delete fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][deleteKey] Memo Shared Key Delete fail : "+e.getMessage());
            throw new FunctionException("Memo Shared Key Delete fail : "+e.getMessage());
        }
    }


    // 공유 메모 읽기 : checkSharedKeyAndReadMemo
    public ResponseInfo readSharedMemo(HttpServletRequest req, MemoInfo memoInfo) {
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try{
            log.info("[Service-MemoManage][readSharedMemo][{}] Shared Memo Read Started...", req_id);

            // Shared Memo select
            checkSharedKeyAndReadMemo(req_id, memoInfo, responseInfo);

            log.info("[Service-MemoManage][readSharedMemo][{}] Shared Memo Read Success...: Memo({})", req_id, memoInfo.getMemo_idx());
            responseInfo.setMsg("Shared Memo Read Success");
        } catch (FunctionException e){
            log.error("[Service-MemoManage][readSharedMemo][{}] Shared Memo Read fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoManage][readSharedMemo][{}] Shared Memo Read fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][readSharedMemo]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Shared Memo Read fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoManage][readSharedMemo] Shared Memo Read fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void checkSharedKeyAndReadMemo(String req_id, MemoInfo info, ResponseInfo resInfo) throws FunctionException {
        try {
            String key = info.getShared_key();
            // Shared Key 가 있을 때
            if(key != null) {
                MemoDetailInfo memo = memoManageMapper.readSharedMemo(info);
                if (memo != null) {
                    log.info("[Service-MemoManage][readSharedMemo][checkSharedKeyAndReadMemo][{}] Shared Memo Check & Read Success : Memo IDX({}), Shared Key({}})", req_id, info.getMemo_idx(), info.getShared_key());
                    resInfo.setRes_data(memo);
                }
                else {
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Shared Memo Read Fail : No Matched Memo");
                    resInfo.setRes_data("[Service-MemoManage][readSharedMemo][checkSharedKeyAndReadMemo] Shared Memo Check & Read Fail : No Matched Memo IDX("+info.getMemo_idx()+") Shared Key("+info.getShared_key()+")");
                    throw new FunctionException("Shared Memo Check & Read Fail : No Matched Memo IDX("+info.getMemo_idx()+") Shared Key("+info.getShared_key()+")");
                }
            }
            // Shared Key 가 없을 때
            else{
                resInfo.setRes_status("-1");
                resInfo.setMsg("Shared Memo Read Fail : Shared Key Is NULL");
                resInfo.setRes_data("[Service-MemoManage][readSharedMemo][checkSharedKeyAndReadMemo] Shared Memo Check & Read Fail : Shared Key Is Null");
                throw new FunctionException("Shared Memo Check & Read Fail : Shared Key Is NUll");
            }
        } catch (Exception e) {
            log.error("[Service-MemoManage][readSharedMemo][checkSharedKeyAndReadMemo][{}] Shared Memo IDX({}) Shared Key({}) Check & Read Fail : {}",req_id, info.getMemo_idx(), info.getShared_key(), e.getMessage());
            log.error("[Service-MemoManage][readSharedMemo][checkSharedKeyAndReadMemo]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Shared Memo Read Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][readSharedMemo][checkSharedKeyAndReadMemo] Shared Memo IDX("+info.getMemo_idx()+") Shared Key("+info.getShared_key()+") Check & Read Fail : "+e.getMessage());
            throw new FunctionException("Shared Memo Check & Read Fail : "+e.getMessage());
        }
    }


    // 메모 정보(상태, 권한, 카테고리, 즐겨찾기) 변경 : checkSharedKeyAndReadMemo
    public ResponseInfo changeMemoInfo(HttpServletRequest req, MemoInfo memoInfo) {
        String req_id = String.valueOf(req.getAttribute("req_id"));
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");
        String key=null; String value=null;

        try{
            log.info("[Service-MemoManage][changeMemoInfo][{}] Memo Info Change Started...", req_id);

            if(memoInfo.getAccess()!=null&&!memoInfo.getAccess().isEmpty()){
                key = GlobalConstants.ACCESS;
                value = memoInfo.getAccess();
            }
            // 1-2. 비번 체크
            else if(memoInfo.getStatus()!=null&&!memoInfo.getStatus().isEmpty()){
                key = GlobalConstants.STATUS;
                value = memoInfo.getStatus();
            }
            // 1-3. 메일 체크
            else if(memoInfo.getCategory_no()!=null&&!memoInfo.getCategory_no().isEmpty()){
                key = GlobalConstants.CATEGORY_NO;
                value = memoInfo.getCategory_no();
            }
            // 1-4. 핸드폰 체크
            else if(memoInfo.getFavorites()!=null&&!memoInfo.getFavorites().isEmpty()){
                key = GlobalConstants.FAVORITES;
                value = memoInfo.getFavorites();
            }
            else{
                responseInfo.setRes_status("-1");
                responseInfo.setMsg("Memo Info Change fail : Empty Input Data");
                responseInfo.setRes_data("[Service-MemoManage][changeMemoInfo] Memo Info Change fail : Empty Input Data (access,status,category_no,favorites)");
                throw new FunctionException("Empty Input Data");
            }

            // 2. 유효성 체크
            if(!CommonUtil.isFormat(key, value)){
                responseInfo.setRes_status("-1");
                responseInfo.setMsg("Memo Info Change fail : Invalid Format");
                responseInfo.setRes_data("[Service-MemoManage][changeMemoInfo] Memo Info Change fail : Invalid Format Key("+key+") Value("+value+")");
                throw new FunctionException("Invalid Format Key("+key+") Value("+value+")");
            }

            // Memo Info 변경
            updateMemoInfo(req_id, key, value, memoInfo.getMemo_idx(), memoInfo.getMemo_type(), responseInfo);

            log.info("[Service-MemoManage][changeMemoInfo][{}] Memo Info Change Success...: Memo({})", req_id, memoInfo.getMemo_idx());
        } catch (FunctionException e){
            log.error("[Service-MemoManage][changeMemoInfo][{}] Memo Info Change fail : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoManage][changeMemoInfo][{}] Memo Info Change fail : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][changeMemoInfo]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Info Change fail : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoManage][changeMemoInfo] Memo Info Change fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }
    public void updateMemoInfo(String req_id, String key, String value, String memo_idx, String memo_type, ResponseInfo resInfo) throws FunctionException {
        try {
            int result = 0;
            switch (key){
                case GlobalConstants.ACCESS, GlobalConstants.STATUS: // MEMO 변경
                    result = memoManageMapper.updateMemoInfo(key, value, memo_idx, memo_type);
                    break;
                case GlobalConstants.CATEGORY_NO, GlobalConstants.FAVORITES: // MEMO_DETAIL 변경
                    result = memoManageMapper.updateMemoDetailInfo(key, value, memo_idx, memo_type);
                    break;
                default:
                    resInfo.setRes_status("-1");
                    resInfo.setMsg("Memo Info Update : Invalid Key");
                    resInfo.setRes_data("[Service-MemoManage][changeMemoInfo][updateMemoInfo] Invalid Key("+key+") : Value("+value+")");
                    throw new FunctionException("Memo Info Update Fail : Invalid Key("+key+") : Value("+value+")");
            }

            if(result==1) {
                log.info("[Service-UserManage][changeMemoInfo][updateMemoInfo][{}] Memo Info Update Success : Key({}), Value({}), Count({})",req_id,key,value,result);
                resInfo.setMsg("Memo Info Update : Success");
                resInfo.setRes_data("{\"memo_idx\":\""+memo_idx+"\", \"memo_type\":\""+memo_type+"\", \"key\":\""+key+"\", \"value\":\""+value+"\"}");
            } else {
                throw new Exception("Result(" + result + ")");
            }
        } catch (Exception e) {
            log.error("[Service-MemoManage][changeMemoInfo][updateMemoInfo][{}] Memo({}) Info Update Fail : {}",req_id, memo_idx, e.getMessage());
            log.error("[Service-MemoManage][changeMemoInfo][updateMemoInfo]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Info Update Fail : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][changeMemoInfo][updateMemoInfo] Memo("+memo_idx+") Info Update Fail : "+e.getMessage());
            throw new FunctionException("Memo("+memo_idx+") Info Update Fail : "+e.getMessage());
        }
    }

}