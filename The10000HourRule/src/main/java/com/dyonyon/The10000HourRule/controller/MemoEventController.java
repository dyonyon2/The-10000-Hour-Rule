package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.domain.ContentInfo;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoDetailInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoInfo;
import com.dyonyon.The10000HourRule.domain.user.UserLoginInfo;
import com.dyonyon.The10000HourRule.service.APIVerificationService;
import com.dyonyon.The10000HourRule.service.TestService;
import com.dyonyon.The10000HourRule.service.user.MemoCRUDService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/memo")
public class MemoEventController {

    private MemoCRUDService memoCRUDService;
    private APIVerificationService apiVerificationService;
    private TestService testService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public MemoEventController(MemoCRUDService memoCRUDService, APIVerificationService apiVerificationService, TestService testService) {
        this.memoCRUDService = memoCRUDService;
        this.apiVerificationService = apiVerificationService;
        this.testService = testService;
    }


    // 메모 생성
    @PostMapping("")
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
            verifyInfo.setService(GlobalConstants.service_memo); verifyInfo.setAccess(GlobalConstants.access_update); verifyInfo.setContent_type(memoInfo.getMemo_type()); verifyInfo.setUser_id(memoInfo.getUser_id()); verifyInfo.setOwner_id(memoInfo.getOwner_id()); verifyInfo.setGroup_id(memoInfo.getOwner_id());
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
            result.setMsg("Memo Create Failed: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/] Memo Controller Failed : "+e.getMessage());
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
            verifyInfo.setService(GlobalConstants.service_memo); verifyInfo.setAccess(GlobalConstants.access_update); verifyInfo.setContent_idx(memoImageInfo.getMemo_idx()); verifyInfo.setContent_type(memoImageInfo.getMemo_type());             verifyInfo.setUser_id(memoImageInfo.getUser_id()); verifyInfo.setOwner_id(memoImageInfo.getOwner_id()); verifyInfo.setGroup_id(memoImageInfo.getOwner_id());
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
            result.setMsg("Image Create Failed: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/image] Image Controller Failed : "+e.getMessage());
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
            verifyInfo.setService(GlobalConstants.service_memo); verifyInfo.setAccess(GlobalConstants.access_create); verifyInfo.setContent_type(memoDetailInfo.getMemo_type()); verifyInfo.setUser_id(memoDetailInfo.getUser_id()); verifyInfo.setOwner_id(memoDetailInfo.getOwner_id()); verifyInfo.setGroup_id(memoDetailInfo.getOwner_id());
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
            result.setMsg("Image Create Failed: Exception Occurred");
            result.setRes_data("[Controller-MemoEvent][/update] Image Controller Failed : "+e.getMessage());
            result.setErr_code("UN");
        }
        return result;
    }


    // Auth Key 검증 처리
//    @GetMapping("/auth/check")
//    public ResponseInfo authCheckController(HttpServletRequest req, @ModelAttribute UserAuthInfo userAuthInfo) throws ParseException {
//        ResponseInfo result = new ResponseInfo();
//        log.info("[Controller-UserEvent][/auth/check][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
//        log.info("[Controller-UserEvent][/auth/check][{}] BODY : {}",req.getAttribute("req_id"), userAuthInfo);
//        log.info("[Controller-UserEvent][/auth/check][{}] Call API ApiVerificationService....",req.getAttribute("req_id"));
//        result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), userAuthInfo.getUser_id(),req.getSession().getId());
//        if("-1".equals(result.getRes_status()))
//            return result;
//        log.info("[Controller-UserEvent][/auth/check][{}] Call UserManageService....",req.getAttribute("req_id"));
//        result = userManageService.verifyAuthKey(req, userAuthInfo);
//        log.info("[Controller-UserEvent][/auth/check][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
//        return result;
//    }

    @RequestMapping("/test")
//    public ResponseInfo testController(HttpServletRequest req, @RequestBody UserDetailInfo userDetailInfo) throws ParseException, IOException {
    public ResponseInfo testController(HttpServletRequest req) throws ParseException, IOException {

//        objectMapper 쓰는 방법
//        ServletInputStream inputStream = req.getInputStream();
//        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//        log.info("messageBody={}", messageBody);
//        UserAuthInfo data = objectMapper.readValue(messageBody, UserAuthInfo.class);
//        log.info("id={}, pw={}", data.getUser_id(), data.getPw());
//        return "test";

//        ResponseInfo result;
//        log.info("[Controller-UserEvent][/test][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
//        result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), userDetailInfo.getUser_id(),req.getSession().getId());
//        if("-1".equals(result.getStatus())){
//            result.setStatus("1");
//            return result;
//        }
//        return result;

        ResponseInfo result;
        log.info("[Controller-UserEvent][/test][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        result = testService.mailSend((String) req.getAttribute("req_id"), String.valueOf(req.getRequestURL()));
        return result;
    }
}
