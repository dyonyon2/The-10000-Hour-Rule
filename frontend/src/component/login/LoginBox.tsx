import useStyles from "@/app/style";
import { LoginInfo } from "@/util/types";
import { Avatar, Button, Grid2, Link, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { pageUrl } from "@/util/values";
import { ApiCall, apiUrl } from "@/util/apiCall";

export default function LoginBox() {
  const [loginInfo,setLoginInfo] = useState<LoginInfo>({user_id:"", pw :""});  // 로그인 입력 데이터
  const style = useStyles();  // Style 세팅

  // 로그인 화면 useEffect 로직
  // - 로그인 정보가 존재하는지 체크하여 로그인 Flag 세팅 (SessionStorage에서 loginFlag와 loginInfo로 확인)
  useEffect(()=>{
    logInCheck(); 
  },[]);

  function logInCheck(){  // 로그인 체크
    if(sessionStorage.getItem('user_id')){ 
      console.log("로그인 되어 있음. 메인 페이지로 이동!");
      window.location.href = pageUrl.main;
    } else console.log("로그인 되어 있지 않음.");
  }

  function setInfo(key:string, value:string){ // 로그인 정보 입력
    var newInfo = {...loginInfo};
    if(key==="id") newInfo.user_id = value;
    else if(key==="pw") newInfo.pw = value;
    setLoginInfo(newInfo);
  }

  async function logInCall(){ // LoginFormInfo로 서버에 로그인 요청
    var result= await ApiCall.call(apiUrl.login,'post',loginInfo,false);
    if(result.res_status){  // 정상적인 API CALL Return
      alert(result.msg);
      if(result.res_status==="1") {  // 로그인 성공
        console.log("로그인 성공");
        var tmp = JSON.parse(result.res_data);  // res_data(JSON)을 객체로 파싱
        sessionStorage.setItem("user_id",tmp.user_id);  // 로그인 된 user_id를 sessionStorage에 저장
        window.location.href = pageUrl.main;
      } else {
        console.log("로그인 실패");
        sessionStorage.setItem("user_id","");
      } 
    } else {    // 비정상적인 API CALL Return
      alert("올바르지 않은 Response 입니다."+result);
    }
  }

  return (
    <>
      <Grid2 container className={style.container} direction={"column"}>
        <Grid2 container className={style.paper} direction={"column"}>
          <Avatar className={style.avatar} />
          <TextField
            onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("id",event.target.value)} value={loginInfo.user_id} 
            variant="outlined" margin="normal" required fullWidth id="user_id" label="id" name="user_id" autoFocus
          />
          <TextField
            onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("pw",event.target.value)} value={loginInfo.pw}
            variant="outlined" margin="normal" required fullWidth name="password" label="password" type="password" id="password"
          />
          <Button
            type="submit" fullWidth variant="contained" color="primary"
            className={style.submit} onClick = {logInCall} disabled={(!loginInfo.user_id)||(!loginInfo.pw)}
          >
            Sign In
          </Button>
          <Grid2 container columns={2} direction={"row"} alignItems={"center"}>
            <Typography>
              <Link className={style.margin} href={pageUrl.findId} variant="body2">
                아이디 찾기
              </Link>
              |
              <Link className={style.margin} href={pageUrl.findPw} variant="body2">
                비밀번호 찾기
              </Link>
              |
              <Link className={style.margin} href={pageUrl.signup} variant="body2">
                회원가입
              </Link>
            </Typography>
          </Grid2>
        </Grid2>
      </Grid2>
    </>
  );
}