package com.dyonyon.The10000HourRule.service.memo;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoDetailInfo;
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
            log.error("[Service-MemoManage][createSharedKey][{}] Memo Shared Key Create Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoManage][createSharedKey][{}]  Memo Shared Key Create Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][createSharedKey]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Shared Key Create Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoManage][createSharedKey] Memo Shared Key Create Failed : "+e.getMessage());
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
                    throw new FunctionException("Memo Owner Check Failed : Invalid Memo Type("+type+")");
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
                    log.info("[Service-MemoManage][createSharedKey][checkMemoOwner][{}] Memo({}) Owner Check Failed : User({}) Owner({})", req_id, type, info.getUser_id(), owner_id);
                    resInfo.setRes_data("[Service-MemoManage][createSharedKey][checkMemoOwner] Memo(" + type + ") Owner Check Failed : Invalid User(" + info.getUser_id() + ") Owner(" + owner_id + ")");
                    throw new FunctionException("Memo(" + type + ") Owner Check Failed : Invalid User(" + info.getUser_id() + ") Owner(" + owner_id + ")");
                } else if (type.equals(GlobalConstants.CONTENT_TYPE_GROUP)) {
                    log.info("[Service-MemoManage][createSharedKey][checkMemoOwner][{}] Memo({}) Owner Check Failed : User({}) Group({})'s Owner({})", req_id, type, info.getUser_id(), info.getOwner_id(), owner_id);
                    resInfo.setRes_data("[Service-MemoManage][createSharedKey][checkMemoOwner] Memo(" + type + ") Owner Check Failed : Invalid User(" + info.getUser_id() + ") Group(" + info.getOwner_id() + ")'s Owner(" + owner_id + ")");
                    throw new FunctionException("Memo(" + type + ") Owner Check Failed : Invalid User(" + info.getUser_id() + ") Group(" + info.getOwner_id() + ")'s Owner(" + owner_id + ")");
                }
            }
        } catch (Exception e) {
            log.error("[Service-MemoManage][createSharedKey][checkMemoOwner][{}] Owner ID Get Failed : {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][createSharedKey][checkMemoOwner]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Shared Key Create Failed : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][createSharedKey][checkMemoOwner] Owner ID Get Failed : "+e.getMessage());
            throw new FunctionException("Owner ID Get Failed : "+e.getMessage());
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
            log.error("[Service-MemoManage][createSharedKey][updateSharedKey][{}] Memo Shared Key Insert Failed : {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][createSharedKey][updateSharedKey]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Shared Key Create Failed : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][createSharedKey][updateSharedKey] Memo Shared Key Insert Failed : "+e.getMessage());
            throw new FunctionException("Memo Shared Key Insert Failed : "+e.getMessage());
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
            log.error("[Service-MemoManage][deleteSharedKey][{}] Memo Shared Key Delete Failed : ERROR OCCURRED {}",req_id,e.getMessage());
        } catch (Exception e){
            log.error("[Service-MemoManage][deleteSharedKey][{}]  Memo Shared Key Delete Failed : ERROR OCCURRED {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][deleteSharedKey]["+req_id+"] Error PrintStack : ",e);
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Memo Shared Key Delete Failed : Exception Occurred");
            responseInfo.setRes_data("[Service-MemoManage][deleteSharedKey] Memo Shared Key Delete Failed : "+e.getMessage());
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
                    throw new FunctionException("Memo Owner Check Failed : Invalid Memo Type("+type+")");
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
                    log.info("[Service-MemoManage][deleteSharedKey][checkMemoOwner][{}] Memo({}) Owner Check Failed : User({}) Owner({})", req_id, type, info.getUser_id(), owner_id);
                    resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][checkMemoOwner] Memo(" + type + ") Owner Check Failed : Invalid User(" + info.getUser_id() + ") Owner(" + owner_id + ")");
                    throw new FunctionException("Memo(" + type + ") Owner Check Failed : Invalid User(" + info.getUser_id() + ") Owner(" + owner_id + ")");
                } else if (type.equals(GlobalConstants.CONTENT_TYPE_GROUP)) {
                    log.info("[Service-MemoManage][deleteSharedKey][checkMemoOwner][{}] Memo({}) Owner Check Failed : User({}) Group({})'s Owner({})", req_id, type, info.getUser_id(), info.getOwner_id(), owner_id);
                    resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][checkMemoOwner] Memo(" + type + ") Owner Check Failed : Invalid User(" + info.getUser_id() + ") Group(" + info.getOwner_id() + ")'s Owner(" + owner_id + ")");
                    throw new FunctionException("Memo(" + type + ") Owner Check Failed : Invalid User(" + info.getUser_id() + ") Group(" + info.getOwner_id() + ")'s Owner(" + owner_id + ")");
                }
            }
        } catch (Exception e) {
            log.error("[Service-MemoManage][deleteSharedKey][checkMemoOwner][{}] Owner ID Get Failed : {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][deleteSharedKey][checkMemoOwner]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Shared Key Delete Failed : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][checkMemoOwner] Owner ID Get Failed : "+e.getMessage());
            throw new FunctionException("Owner ID Get Failed : "+e.getMessage());
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
            log.error("[Service-MemoManage][deleteSharedKey][deleteKey][{}] Memo Shared Key Delete Failed : {}",req_id,e.getMessage());
            log.error("[Service-MemoManage][deleteSharedKey][deleteKey]["+req_id+"] Error PrintStack : ",e);
            resInfo.setStatus("-1");
            resInfo.setRes_status("-1");
            resInfo.setMsg("Memo Shared Key Delete Failed : Exception Occurred");
            resInfo.setRes_data("[Service-MemoManage][deleteSharedKey][deleteKey] Memo Shared Key Delete Failed : "+e.getMessage());
            throw new FunctionException("Memo Shared Key Delete Failed : "+e.getMessage());
        }
    }
}