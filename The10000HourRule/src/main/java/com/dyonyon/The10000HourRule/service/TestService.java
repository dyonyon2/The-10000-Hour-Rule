package com.dyonyon.The10000HourRule.service;

import com.dyonyon.The10000HourRule.common.FunctionException;
import com.dyonyon.The10000HourRule.domain.ResponseInfo;
import com.dyonyon.The10000HourRule.mapper.UserLoginMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.naming.factory.MailSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;


// 로그인 이후, 필요한 공통 서비스
@Service
@Slf4j
public class TestService {

    @Value("${spring.mail.username}")
    String user;

    @Autowired
    private JavaMailSender javaMailSender;


    public ResponseInfo mailSend(String req_id, String url){

        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus("1"); responseInfo.setRes_status("1"); responseInfo.setErr_code("000000");

        try {
            log.info("[Controller-TestService][mailSend] User : {}",user);

//            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//            simpleMailMessage.setFrom(user);
//            simpleMailMessage.setTo("jychoi9712@gmail.com");
//            simpleMailMessage.setSubject("Mail Send Test");
//            simpleMailMessage.setText("TEST1 : "+req_id+"\n TEST2 : "+url);
//            javaMailSender.send(simpleMailMessage);

            MimeMessage m = javaMailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(m,"UTF-8");
            h.setFrom(user);
            h.setTo("jychoi9712@gmail.com");
            h.setSubject("테스트메일");
            h.setText("메일테스트");
            javaMailSender.send(m);

        } catch (Exception e){
            e.printStackTrace();
            log.error("[Service-TestService][mailSend][{}] Mail Send Fail : ERROR OCCURRED {}",req_id,e.getLocalizedMessage());
            log.error("[Service-TestService][mailSend][{}] Mail Send Fail : ERROR OCCURRED {}",req_id,e.getStackTrace());
            log.error("[Service-TestService][mailSend][{}] Mail Send Fail : ERROR OCCURRED {}",req_id,e.getMessage());
            responseInfo.setStatus("-1");
            responseInfo.setRes_status("-1");
            responseInfo.setMsg("Mail Send Fail : Exception Occurred");
            responseInfo.setRes_data("[Service-TestService][mailSend] Mail Send Fail : "+e.getMessage());
            responseInfo.setErr_code("UN");
        }
        return responseInfo;
    }

}
