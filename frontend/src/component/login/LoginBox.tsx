import useStyles from "@/app/style";
import { LoginInfo } from "@/util/types";
import { Avatar, Button, Grid2, Link, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import Image from "next/image";
import { pageUrl } from "@/util/values";

export default function LoginBox() {
  const [loginFlag,setLoginFlag] = useState<boolean>(false);  // 로그인 체크 Flag
  const [loginInfo,setLoginInfo] = useState<LoginInfo>({id:"", pw :""});  // 로그인 입력 데이터
  const style = useStyles();  // Style 세팅

  // 로그인 화면 useEffect 로직
  // - 로그인 정보가 존재하는지 체크하여 로그인 Flag 세팅 (SessionStorage에서 loginFlag와 loginInfo로 확인)
  useEffect(()=>{
    logInCheck(); 
  },[]);

  function logInCheck(){  // 로그인 체크
    if(sessionStorage.getItem('loginFlag')&&sessionStorage.getItem('loginInfo')){ 
      console.log("로그인 되어 있음. 메인 페이지로 이동! (In logInCall()");
      window.location.href = "/";
    } else {
      console.log("로그인 되어 있지 않음. (In logInCall()");
      setLoginFlag(false);
    }
  }

  function setInfo(key:string, value:string){ // 로그인 정보 입력
    var newInfo = {...loginInfo};
    if(key==="id") newInfo.id = value;
    else if(key==="pw") newInfo.pw = value;
    setLoginInfo(newInfo);
  }

  function logInCall(){ // LoginFormInfo로 서버에 로그인 요청
    setLoginFlag(true);
    window.location.href = "/";
  }

  return (
    <>
      <Grid2 container className={style.container} direction={"column"}>
        <Grid2 container className={style.paper} direction={"column"}>
          <Avatar className={style.avatar} />
          <TextField
            onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("id",event.target.value)} value={loginInfo.id} 
            variant="outlined" margin="normal" required fullWidth id="username" label="Username" name="username" autoFocus
          />
          <TextField
            onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("pw",event.target.value)} value={loginInfo.pw}
            variant="outlined" margin="normal" required fullWidth name="password" label="Password" type="password" id="password"
          />
          <Button
            type="submit" fullWidth variant="contained" color="primary"
            className={style.submit} onClick = {logInCall} disabled={(!loginInfo.id)||(!loginInfo.pw)}
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