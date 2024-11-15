import axios, { AxiosRequestConfig } from "axios";
import { ApiReturnForm } from "./types";

const url = "http://127.0.0.1:8080/api";
export enum apiUrl {
    /* User 관련 API */
    login=url+"/user/login",    // 로그인 (POST)
    logout=url+"/user/logout",    // 로그아웃 (POST)
    authRequest=url+"/user/auth",    // 이메일 인증번호 요청 (POST)
    authCheck=url+"/user/auth/check?user_id={0}&key={1}",     // 이메일 인증번호 확인 (GET)

    signUp=url+"/user", // 회원가입 (POST)
    idDupCheck=url+"/user/check?user_id={0}",   // 아이디 중복확인 (GET)
    nickDupCheck=url+"/user/check?nickname={0}",    // 닉네임 중복확인 (GET)
    emailDupCheck=url+"/user/check?mail={0}",   // 이메일 중복확인 (GET)
    phoneDupCheck=url+"/user/check?phone={0}",   // 핸드폰 중복확인 (GET)

    userInfoChange=url+"/user", // 닉네임,비밀번호,휴대폰,이메일 변경 (PATCH)


    /* Memo 관련 API */
    memoCreate=url+"/memo", // 메모 생성 (POST)
    memoRead=url+"/memo",   // 메모 읽기 (GET)
    memoUpdate=url+"/memo", // 메모 수정 (PATCH)
    memoDelete=url+"/memo", // 메모 삭제 (DELETE)

    memoImageCreate=url+"/memo/image", // 메모 이미지 생성 (POST)
    
    memoListRead=url+"/memo/list?user_id={0}&target={1}",   // 메모 목록 읽기 (GET)
    
    memoSettingUpdate=url+"/memo/setting", // 메모 정보(상태, 권한, 카테고리, 즐겨찾기) (PATCH)
    
    memocopy=url+"/memo/copy", // 메모 복제 (POST)
    
    memoSharingKeyCreate=url+"/memo/share", // 메모 공유키 생성 (POST)
    memoShareRead=url+"/memo/share", // 공유 메모 읽기 (GET)
    memoSharingKeyDelete=url+"/memo/share", // 메모 공유키 삭제 (DELETE)

}

export class ApiCall{
    public static queryStringFormat(url:string, args:any[]){
        for(let i=0;i<args.length;i++){
            url = url.replace("{"+i+"}",args[i]);
        }
        return url;
    }
    public static async call(url:string, method:'get'|'post'|'delete'|'patch', body?:any){
        try{
            const config: AxiosRequestConfig = {
                method: method,
                url:url,
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json',
                },
                ...(body && { data: {...body,user_id:sessionStorage.getItem('user_id')} })
            };

            console.log('API CALL REQUEST:',config);
            let response = await axios(config);
            console.log('API CALL RESPONSE:',response.data);
            return response.data;
        } catch(err:any){
            console.error('API CALL ERROR:',err);
            return err;
        }
    }
}