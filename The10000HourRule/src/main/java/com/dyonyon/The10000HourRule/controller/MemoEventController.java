package com.dyonyon.The10000HourRule.controller;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import com.dyonyon.The10000HourRule.domain.ContentInfo;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.domain.memo.MemoImageInfo;
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
    public void memoController(HttpServletRequest req, @RequestBody UserLoginInfo userLoginInfo) throws ParseException {
        ResponseInfo result;
        log.info("[Controller-MemoEvent][/][{}] URL : {}",req.getAttribute("req_id"), req.getRequestURL());
        log.info("[Controller-MemoEvent][/][{}] BODY : {}",req.getAttribute("req_id"), userLoginInfo);
        log.info("[Controller-MemoEvent][/][{}] Call UserLoginService....",req.getAttribute("req_id"));
//        result = userLoginService.login(req, userLoginInfo);
//        log.info("[Controller-MemoEvent][/][{}] RESULT : STATUS({}) RES_STATUS({})",req.getAttribute("req_id"),result.getStatus(),result.getRes_status());
//        return result;
    }

    // 메모 이미지 저장
    @PostMapping("/image")
    public ResponseInfo memoImageController(HttpServletRequest req, MultipartFile file, String json) throws ParseException, JsonProcessingException {
        ResponseInfo result = new ResponseInfo();
        try {
            log.info("[Controller-MemoEvent][/image][{}] URL : {}", req.getAttribute("req_id"), req.getRequestURL());
            MemoImageInfo data = objectMapper.readValue(json, MemoImageInfo.class); data.setFile(file);
            log.info("[Controller-MemoEvent][/image][{}] BODY : ({})", req.getAttribute("req_id"), data);
            log.info("[Controller-MemoEvent][/image][{}] Call API ApiVerificationService....", req.getAttribute("req_id"));
            // 로그인 세션 확인
            result = apiVerificationService.checkLoginSession((String) req.getAttribute("req_id"), data.getUser_id(), req.getSession().getId());
            if ("-1".equals(result.getRes_status()))
                return result;
            // 권한 확인
//            ContentInfo verifyInfo = new ContentInfo(GlobalConstants.service_memo, data.getUser_id(),data.getOwner_id(),data.getMemo_type(),data.getGroup_idx(),data.getGroup_name(),null);
//            result = apiVerificationService.verifyAuthority((String) req.getAttribute("req_id"), verifyInfo);
//            if ("-1".equals(result.getRes_status()))
//                return result;
            // 이미지 저장
            log.info("[Controller-MemoEvent][/image][{}] Call MemoCRUDService....", req.getAttribute("req_id"));
            result = memoCRUDService.saveImageFile(req, data);
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
