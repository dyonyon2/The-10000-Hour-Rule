package com.dyonyon.The10000HourRule.util;

import com.dyonyon.The10000HourRule.code.GlobalConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {
    public static boolean isNull(Object param){
        return param == null;
    }

    public static boolean isFormat(String key, String value){
        boolean res = true;
        switch (key){
            case GlobalConstants.name:  //영문(대/소문자),한글, 공백
                for (char c : value.toCharArray()) {
                    if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES) {
                    } else if(Character.isLetter(c)){
                    } else if(Character.isWhitespace(c)) {
                    } else {
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.pw:    //영문(대/소문자), 숫자
                for (char c : value.toCharArray()) {
                    if(Character.isLetter(c)){
                    } else if(Character.isDigit(c)) {
                    } else {
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.phone: //숫자, 특수기호(-)
                for (char c : value.toCharArray()) {
                    if(Character.isDigit(c)) {
                    } else if('-' == c) {
                    } else {
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.mail:  //영문(대/소문자), 숫자, 특수기호(@, .)
                for (char c : value.toCharArray()) {
                    if(Character.isLetter(c)){
                    } else if(Character.isDigit(c)) {
                    } else if('.' == c) {
                    } else if('@' == c) {
                    } else {
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.birth: // 숫자, 특수기호(/)
                for (char c : value.toCharArray()) {
                    if(Character.isDigit(c)) {
                    } else if('/' == c) {
                    } else {
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.nickname: //영문(대/소문자), 한글, 숫자
                for (char c : value.toCharArray()) {
                    if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES) {
                    } else if(Character.isLetter(c)){
                    } else if(Character.isDigit(c)) {
                    } else {
                        res = false;
                        break;
                    }
                }
                break;
            case GlobalConstants.sex:   //영문(대문자), M or W
                if("M".equals(value)){
                } else if("W".equals(value)){
                } else {
                    res = false;
                }
                break;
            case GlobalConstants.user_id:   //영문(대/소문자), 숫자
                for (char c : value.toCharArray()) {
                    if(Character.isLetter(c)){
                    } else if(Character.isDigit(c)) {
                    } else {
                        res = false;
                        break;
                    }
                }
                break;
            default:
                log.error("[CommonUtil-formatCheck] {} Is Unknown Case",key);
                res = false;
                break;
        }
        return res;
    }
}