package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.domain.ContentInfo;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.*;
import com.dyonyon.The10000HourRule.service.APIVerificationService;
import com.dyonyon.The10000HourRule.service.memo.MemoCRUDService;
import com.dyonyon.The10000HourRule.service.memo.MemoFollowService;
import com.dyonyon.The10000HourRule.service.memo.MemoManageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/memo")
public class MemoEventController {

    private MemoCRUDService memoCRUDService;
    private MemoManageService memoManageService;
    private MemoFollowService memoFollowService;
    private APIVerificationService apiVerificationService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public MemoEventController(MemoCRUDService memoCRUDService, MemoManageService memoManageService, APIVerificationService apiVerificationService, MemoFollowService memoFollowService) {
        this.memoCRUDService = memoCRUDService;
        this.memoManageService = memoManageService;
        this.memoFollowService = memoFollowService;
        this.apiVerificationService = apiVerificationService;
    }


    // 메모 생성
    @PostMapping("/create")
    public ResponseInfo memoCreateController(HttpServletRequest req, @RequestBody MemoInfo memoInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/][{}] BODY : {}",req.getAttribute("req_id"), memoInfo);
            log.info("[Controller-MemoEvent][/][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_CREATE); verifyInfo.setContent_type(memoInfo.getMemo_type()); verifyInfo.setUser_id(memoInfo.getUser_id()); verifyInfo.setOwner_id(memoInfo.getOwner_id()); verifyInfo.setGroup_id(memoInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/][{}] Call MemoCRUDService....",req.getAttribute("req_id"));
            result = memoCRUDService.createMemo(req, memoInfo);
            log.info("[Controller-MemoEvent][/][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Create Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/] Memo Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 이미지 저장
    @PostMapping("/image")
    public ResponseInfo memoImageController(HttpServletRequest req, MultipartFile file, String json) throws ParseException, JsonProcessingException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/image][{}] URL : {}", req.getAttribute("req_id"), req.getRequestURL());
            MemoImageInfo memoImageInfo = objectMapper.readValue(json, MemoImageInfo.class); memoImageInfo.setFile(file);
            log.info("[Controller-MemoEvent][/image][{}] BODY : ({})", req.getAttribute("req_id"), memoImageInfo);
            log.info("[Controller-MemoEvent][/image][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            if(req.getAttribute("user_idx")!=null) memoImageInfo.setUser_idx((String) req.getAttribute("user_idx"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoImageInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/image][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_READ_UPDATE); verifyInfo.setContent_idx(memoImageInfo.getMemo_idx()); verifyInfo.setContent_type(memoImageInfo.getMemo_type());             verifyInfo.setUser_id(memoImageInfo.getUser_id()); verifyInfo.setOwner_id(memoImageInfo.getOwner_id()); verifyInfo.setGroup_id(memoImageInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/image][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            // 이미지 저장
            log.info("[Controller-MemoEvent][/image][{}] Call MemoCRUDService....", req.getAttribute("req_id"));
            result = memoCRUDService.saveImageFile(req, memoImageInfo);
            log.info("[Controller-MemoEvent][/image][{}] RESULT : STATUS({}) RES_STATUS({})", req.getAttribute("req_id"), result.getStatus(), result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/image][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/image]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Image Create Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/image] Image Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 수정
    @PatchMapping("/update")
    public ResponseInfo memoUpdateController(HttpServletRequest req, @RequestBody MemoDetailInfo memoDetailInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/update][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoDetailInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/update][{}] BODY : {}",req.getAttribute("req_id"), memoDetailInfo);
            log.info("[Controller-MemoEvent][/update][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoDetailInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/update][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_READ_UPDATE); verifyInfo.setContent_idx(memoDetailInfo.getMemo_idx());
            verifyInfo.setContent_type(memoDetailInfo.getMemo_type()); verifyInfo.setUser_id(memoDetailInfo.getUser_id()); verifyInfo.setOwner_id(memoDetailInfo.getOwner_id()); verifyInfo.setGroup_id(memoDetailInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/update][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/update][{}] Call MemoCRUDService....",req.getAttribute("req_id"));
            result = memoCRUDService.updateMemo(req, memoDetailInfo);
            log.info("[Controller-MemoEvent][/update][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/update][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/update]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Update Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/update] Memo Update Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 읽기
    @GetMapping("/read")
    public ResponseInfo memoReadController(HttpServletRequest req, @ModelAttribute MemoInfo memoInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/read][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/read][{}] BODY : {}",req.getAttribute("req_id"), memoInfo);
            log.info("[Controller-MemoEvent][/read][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/read][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_READ); verifyInfo.setContent_idx(memoInfo.getMemo_idx());
            verifyInfo.setContent_type(memoInfo.getMemo_type()); verifyInfo.setUser_id(memoInfo.getUser_id()); verifyInfo.setOwner_id(memoInfo.getOwner_id()); verifyInfo.setGroup_id(memoInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/read][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/read][{}] Call MemoCRUDService....",req.getAttribute("req_id"));
            result = memoCRUDService.readMemo(req, memoInfo);
            log.info("[Controller-MemoEvent][/read][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/read][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/read]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Read Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/read] Memo Read Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 삭제
    @DeleteMapping("/delete")
    public ResponseInfo memoDeleteController(HttpServletRequest req, @RequestBody MemoInfo memoInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/delete][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/delete][{}] BODY : {}",req.getAttribute("req_id"), memoInfo);
            log.info("[Controller-MemoEvent][/delete][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/delete][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_DELETE); verifyInfo.setContent_idx(memoInfo.getMemo_idx());
            verifyInfo.setContent_type(memoInfo.getMemo_type()); verifyInfo.setUser_id(memoInfo.getUser_id()); verifyInfo.setOwner_id(memoInfo.getOwner_id()); verifyInfo.setGroup_id(memoInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/delete][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/delete][{}] Call MemoCRUDService....",req.getAttribute("req_id"));
            result = memoCRUDService.deleteMemo(req, memoInfo);
            log.info("[Controller-MemoEvent][/delete][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/delete][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/delete]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Delete Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/delete] Memo Delete Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 목록 읽기 (own, group, follow)
    @GetMapping("/list")
    public ResponseInfo memoListReadController(HttpServletRequest req, @RequestParam String user_id, @RequestParam String target) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/list][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            log.info("[Controller-MemoEvent][/list][{}] BODY : User({}) Target({})",req.getAttribute("req_id"), user_id, target);
            log.info("[Controller-MemoEvent][/list][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), user_id, req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/list][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/list][{}] Call MemoCRUDService....",req.getAttribute("req_id"));
            result = memoCRUDService.readMemoList(req, user_id, target);
            log.info("[Controller-MemoEvent][/list][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/list][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/list]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo List Read Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/list] Memo List Read Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 공유키 생성 (소유자만 가능)
    @PostMapping("/share/create")
    public ResponseInfo memoSharedKeyCreateController(HttpServletRequest req, @RequestBody MemoDetailInfo memoDetailInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/share/create][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoDetailInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/share/create][{}] BODY : {}",req.getAttribute("req_id"), memoDetailInfo);
            log.info("[Controller-MemoEvent][/share/create][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoDetailInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/share/create][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인 => 본인은 CRUD 다 있음을 확인. 밑에 서비스에서 자기 소유인지 더블 체크
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_ALL); verifyInfo.setContent_idx(memoDetailInfo.getMemo_idx());
            verifyInfo.setContent_type(memoDetailInfo.getMemo_type()); verifyInfo.setUser_id(memoDetailInfo.getUser_id()); verifyInfo.setOwner_id(memoDetailInfo.getOwner_id()); verifyInfo.setGroup_id(memoDetailInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/share/create][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/share/create][{}] Call MemoManageService....",req.getAttribute("req_id"));
            result = memoManageService.createSharedKey(req, memoDetailInfo);
            log.info("[Controller-MemoEvent][/share/create][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/share/create][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/share/create]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Shared Key Create Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/share/create] Memo Shared Key Create Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 공유키 삭제 (소유자만 가능)
    @DeleteMapping("/share/delete")
    public ResponseInfo memoSharedKeyDeleteController(HttpServletRequest req, @RequestBody MemoDetailInfo memoDetailInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/share/delete][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoDetailInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/share/delete][{}] BODY : {}",req.getAttribute("req_id"), memoDetailInfo);
            log.info("[Controller-MemoEvent][/share/delete][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoDetailInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/share/delete][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_ALL); verifyInfo.setContent_idx(memoDetailInfo.getMemo_idx());
            verifyInfo.setContent_type(memoDetailInfo.getMemo_type()); verifyInfo.setUser_id(memoDetailInfo.getUser_id()); verifyInfo.setOwner_id(memoDetailInfo.getOwner_id()); verifyInfo.setGroup_id(memoDetailInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/share/delete][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/share/delete][{}] Call MemoManageService....",req.getAttribute("req_id"));
            result = memoManageService.deleteSharedKey(req, memoDetailInfo);
            log.info("[Controller-MemoEvent][/share/delete][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/share/delete[{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/share/delete]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Create Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/share/delete] Memo Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 공유 메모 읽기
    @GetMapping("/share/read")
    public ResponseInfo memoSharedReadController(HttpServletRequest req, @ModelAttribute MemoInfo memoInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/share/read][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/share/read][{}] BODY : {}",req.getAttribute("req_id"), memoInfo);
            log.info("[Controller-MemoEvent][/share/read][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/share/read][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/share/read][{}] Call MemoManageService....",req.getAttribute("req_id"));
            result = memoManageService.readSharedMemo(req, memoInfo);
            log.info("[Controller-MemoEvent][/share/read][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/share/read][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/share/read]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Create Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/share/read] Memo Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 정보(상태, 권한, 카테고리, 즐겨찾기) 변경
    @PatchMapping("/change")
    public ResponseInfo memoInfoChangeController(HttpServletRequest req, @RequestBody MemoInfo memoInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/change][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/change][{}] BODY : {}",req.getAttribute("req_id"), memoInfo);
            log.info("[Controller-MemoEvent][/change][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/change][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_UPDATE); verifyInfo.setContent_idx(memoInfo.getMemo_idx());
            verifyInfo.setContent_type(memoInfo.getMemo_type()); verifyInfo.setUser_id(memoInfo.getUser_id()); verifyInfo.setOwner_id(memoInfo.getOwner_id()); verifyInfo.setGroup_id(memoInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/change][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/change][{}] Call MemoManageService....",req.getAttribute("req_id"));
            result = memoManageService.changeMemoInfo(req, memoInfo);
            log.info("[Controller-MemoEvent][/change][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/change][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/change]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Info Change Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/change] Memo Info Change Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 복제
    @PostMapping("/copy")
    public ResponseInfo memoCopyController(HttpServletRequest req, @RequestBody MemoCopyInfo memoInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/copy][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
            if(req.getAttribute("user_idx")!=null) memoInfo.setUser_idx((String) req.getAttribute("user_idx"));
            log.info("[Controller-MemoEvent][/copy][{}] BODY : {}",req.getAttribute("req_id"), memoInfo);
            log.info("[Controller-MemoEvent][/copy][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoInfo.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/copy][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
                return result;
            }
            // 권한 확인
            ContentInfo verifyInfo = new ContentInfo();
            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_READ); verifyInfo.setContent_idx(memoInfo.getMemo_idx());
            verifyInfo.setContent_type(memoInfo.getMemo_type()); verifyInfo.setUser_id(memoInfo.getUser_id()); verifyInfo.setOwner_id(memoInfo.getOwner_id()); verifyInfo.setGroup_id(memoInfo.getOwner_id());
            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
            if ("-1".equals(result.getRes_status())) {
                log.info("[Controller-MemoEvent][/copy][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
                return result;
            }
            log.info("[Controller-MemoEvent][/copy][{}] Call MemoManageService....",req.getAttribute("req_id"));
            result = memoManageService.copyMemo(req, memoInfo);
            log.info("[Controller-MemoEvent][/copy][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
        } catch (Exception e){
            log.error("[Controller-MemoEvent][/copy][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
            log.error("[Controller-MemoEvent][/copy]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
            result.setStatus("-1");
            result.setRes_status("-1");
            result.setMsg("Memo Copy Fail: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/copy] Memo Info Change Controller Fail : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // 메모 팔로우 요청
    @PostMapping("/follow/request")
    public ResponseInfo memoFollowRequestController(HttpServletRequest req, @RequestBody MemoFollowInfo memoFollowInfo) throws ParseException {
        ResponseInfo result = new ResponseInfo();
//        try {
//            log.info("[Controller-MemoEvent][/follow/request][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
//            if(req.getAttribute("user_idx")!=null) memoFollowInfo.setUser_idx((String) req.getAttribute("user_idx"));
//            log.info("[Controller-MemoEvent][/follow/request][{}] BODY : {}",req.getAttribute("req_id"), memoFollowInfo);
//            log.info("[Controller-MemoEvent][/follow/request][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
//            // 로그인 세션 확인
//            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), memoFollowInfo.getUser_id(), req.getSession().getId());
//            if ("-1".equals(result.getRes_status())) {
//                log.info("[Controller-MemoEvent][/follow/request][{}] API Verification Fail... : Check Login Session", req.getAttribute("req_id"));
//                return result;
//            }
//            // 권한 확인 => 해당 메모에 공유 키 설정 되어 있는지 & 공유 키가 일치하는지
//            ContentInfo verifyInfo = new ContentInfo();
//            verifyInfo.setService(GlobalConstants.SERVICE_MEMO); verifyInfo.setAccess(GlobalConstants.ACCESS_CREATE); verifyInfo.setContent_type(memoFollowInfo.getMemo_type()); verifyInfo.setUser_id(memoFollowInfo.getUser_id()); verifyInfo.setOwner_id(memoFollowInfo.getOwner_id()); verifyInfo.setGroup_id(memoFollowInfo.getOwner_id());
//            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
//            if ("-1".equals(result.getRes_status())) {
//                log.info("[Controller-MemoEvent][/follow/request][{}] API Verification Fail... : Check Authority", req.getAttribute("req_id"));
//                return result;
//            }
//            log.info("[Controller-MemoEvent][/follow/request][{}] Call MemoCRUDService....",req.getAttribute("req_id"));
//            result = memoCRUDService.createMemo(req, memoFollowInfo);
//            log.info("[Controller-MemoEvent][/follow/request][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
//        } catch (Exception e){
//            log.error("[Controller-MemoEvent][/follow/request][{}] ERROR OCCURRED {}",req.getAttribute("req_id"),e.getMessage());
//            log.error("[Controller-MemoEvent][/follow/request]["+req.getAttribute("req_id")+"] Error PrintStack : ",e);
//            result.setStatus("-1");
//            result.setRes_status("-1");
//            result.setMsg("Memo Create Fail: Exception Occurred");
//            result.setRes_data("[Controller-MemoEvent][/follow/request] Memo Controller Fail : "+e.getMessage());
//            result.setErr_code("UN");
//        }
        return result;
    }
}