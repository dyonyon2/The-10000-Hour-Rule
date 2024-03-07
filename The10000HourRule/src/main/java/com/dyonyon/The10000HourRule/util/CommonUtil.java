package com.dyonyon.The10000HourRule.util;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Slf4j
public class CommonUtil {
    public static boolean isNull(String key, Object param){
        boolean result = (param == null);
        if(result)
            log.error("[CommonUtil-isNull] {} Is Null",key);
        return result;
    }

    public static boolean isFormat(String key, String value){
        boolean res = true;
        switch (key){
            case GlobalConstants.NAME:  //영문(대/소문자),한글, 공백
                for (char c : value.toCharArray()) {
                    if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES) {
                    } else if(Character.isLetter(c)){
                    } else if(Character.isWhitespace(c)) {
                    } else {
                        log.error("[CommonUtil-isFormat] {}({}) Is Wrong Format",key,value);
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.PW:    //영문(대/소문자), 숫자
                for (char c : value.toCharArray()) {
                    if(Character.isLetter(c)){
                    } else if(Character.isDigit(c)) {
                    } else {
                        log.error("[CommonUtil-isFormat] {}({}) Is Wrong Format",key,value);
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.PHONE: //숫자, 특수기호(-)
                for (char c : value.toCharArray()) {
                    if(Character.isDigit(c)) {
                    } else if('-' == c) {
                    } else {
                        log.error("[CommonUtil-isFormat] {}({}) Is Wrong Format",key,value);
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.MAIL:  //영문(대/소문자), 숫자, 특수기호(@, .)
                for (char c : value.toCharArray()) {
                    if(Character.isLetter(c)){
                    } else if(Character.isDigit(c)) {
                    } else if('.' == c) {
                    } else if('@' == c) {
                    } else {
                        log.error("[CommonUtil-isFormat] {}({}) Is Wrong Format",key,value);
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.BIRTH: // 숫자, 특수기호(/)
                for (char c : value.toCharArray()) {
                    if(Character.isDigit(c)) {
                    } else if('/' == c) {
                    } else {
                        log.error("[CommonUtil-isFormat] {}({}) Is Wrong Format",key,value);
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.NICKNAME: //영문(대/소문자), 한글, 숫자
                for (char c : value.toCharArray()) {
                    if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES) {
                    } else if(Character.isLetter(c)){
                    } else if(Character.isDigit(c)) {
                    } else {
                        log.error("[CommonUtil-isFormat] {}({}) Is Wrong Format",key,value);
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.SEX:   //영문(대문자), M or W
                if("M".equals(value)){
                } else if("W".equals(value)){
                } else {
                    log.error("[CommonUtil-isFormat] {}({}) Is Wrong Format",key,value);
                    res = false;
                }
                break;
            case GlobalConstants.USER_ID:   //영문(대/소문자), 숫자
                for (char c : value.toCharArray()) {
                    if(Character.isLetter(c)){
                    } else if(Character.isDigit(c)) {
                    } else {
                        log.error("[CommonUtil-isFormat] {}({}) Is Wrong Format",key,value);
                        res = false;
                        break;
                    }
                }
                break;
            default:
                log.error("[CommonUtil-isFormat] {}({}) Is Unknown Case",key,value);
                res = false;
                break;
        }
        return res;
    }

    public static String getAuthKey(){
        Random random = new Random();
        // 0부터 999999 사이의 임의의 6자리 숫자를 생성
        int randomNumber = random.nextInt(1000000);
        // 결과를 6자리로 만들기 위해 문자열로 변환하고, 앞에 0을 채웁니다.
        return String.format("%06d", randomNumber);
    }

    public static String getReqId(){
        SimpleDateFormat now = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        return now.format(new Date())+String.format("%03d", randomNumber);
    }

    public static String getImgPath(String basePath){
        SimpleDateFormat now = new SimpleDateFormat("yyyyMMdd/HH");
        return basePath+ File.separator +now.format(new Date());
    }
}
