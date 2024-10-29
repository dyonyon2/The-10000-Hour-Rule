import useStyles from "@/app/style";
import { toUpperString } from "@/util/stringUtil";
import { authKeyInfo } from "@/util/types";
import { pageUrl } from "@/util/values";
import { ArrowBackOutlined, CheckBoxOutlined } from "@mui/icons-material";
import { Box, Button, FormControlLabel, Grid2, Radio, RadioGroup, TextField, Typography } from "@mui/material";
import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function FindBox() {
  const searchParams = useSearchParams();
  const [type, setType] = useState<any>("id");
  const [requestFlag, setRequestFlag] = useState<boolean>(false);
  const [authKeyInfo,setAuthKeyInfo] = useState<authKeyInfo>({name:"", type:"", id:"", phone:"", email:"", key:""});  // 로그인 입력 데이터
  const style = useStyles();  // Style 세팅

  function authKeyRequest(){  // 인증키 요청
    setRequestFlag(true);
  }

  function authKeyValidation(){  // 인증키 검증
  }

  function setInfo(key:string, value:string){ // 인증키 입력
    console.log(key,value);
    var newInfo = {...authKeyInfo};
    if(key==="name") newInfo.name = value;
    else if(key==="type") newInfo.type = value;
    else if(key==="id") newInfo.id = value;
    else if(key==="phone") newInfo.phone = value;
    else if(key==="email") newInfo.email = value;
    else if(key==="key") newInfo.key = value;
    setAuthKeyInfo(newInfo);
  }

  function logInCall(){ // LoginFormInfo로 서버에 로그인 요청
    authKeyRequest();
  }

  function checkSendAble(){ // 인증 종류에 해당하는 값 존재하는지 체크
    if(requestFlag) // 인증키 전송 여부
      return true;

    if(!authKeyInfo.type) // Type 세팅 여부
      return true;
      
    if(authKeyInfo.type==="email"){ // Type이 email인 경우
      if(!authKeyInfo.email) // email 값 세팅 여부
        return true;
    } else if(authKeyInfo.type==="phone"){  // Type이 phone인 경우
      if(!authKeyInfo.phone) // phone 값 세팅 여부
        return true;
    }

    return false;
  }

  // 쿼리스트링으로 ID를 찾을지 PW를 찾을지 세팅한다.
  useEffect(()=>{
    if(searchParams.get('type')){
      const tmp = searchParams.get('type');
      if(tmp!=="id"&&tmp!=="pw"){
        alert("잘못된 접근입니다.");
        window.location.href=pageUrl.login;
      } else setType(tmp);
    }
  },[])

  return (
    <>
      <Grid2 container  className={style.container} direction={"column"} wrap="nowrap">
        <Grid2 container className={style.paper} direction={"column"} wrap="nowrap">
          <Typography variant="h3"> {toUpperString(type)} 찾기 </Typography>
          <div className={style.divider} />
          {(type==="id"?
            // <Grid2 container size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"} wrap="nowrap" >
            <Grid2 container size={12} columns={2} spacing={2} alignItems={"center"} wrap="nowrap" >
              <Grid2 size={4} className={style.white}>
                <Typography variant="h5" > 이름</Typography>
              </Grid2>
              <Grid2 size={8} className={style.white}>
                <TextField
                  onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("name",event.target.value)} value={authKeyInfo.name}
                  variant="outlined" margin="normal" required id="name" label="이름" name="name" fullWidth autoFocus sx={{ margin: '0 auto' }}
                />
              </Grid2>
            </Grid2>
            :
            // <Grid2 container size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"} wrap="nowrap" >
            <Grid2 container size={12} columns={2} spacing={2} alignItems={"center"} wrap="nowrap" >
              <Grid2 size={4} className={style.white}>
                <Typography variant="h5" > 아이디 </Typography>
              </Grid2>
              <Grid2 size={8} className={style.white}>
                <TextField
                  onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("id",event.target.value)} value={authKeyInfo.id}
                  variant="outlined" margin="normal" required fullWidth id="id" label="id" name="id" autoFocus sx={{ margin: '0 auto' }}
                />
              </Grid2>
            </Grid2>
          )}
          {/* <Grid2 container size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"}  wrap="nowrap" sx={{ marginTop: '20px' }} > */}
          <Grid2 container size={12} columns={2} spacing={2} alignItems={"center"}  wrap="nowrap" sx={{ marginTop: '20px' }} >
            <Grid2 size={4} >
              <Typography variant="h5" className={style.white} >인증 수단</Typography>
            </Grid2>
            <Grid2 size={8} className={style.white} >
              <RadioGroup value={authKeyInfo.type} row onChange={(event:React.ChangeEvent<HTMLInputElement>)=>{setInfo("type",event.target.value)}} >
                <Box display="flex" justifyContent="center" width="100%" >
                  <FormControlLabel value="email" control={<Radio />} label="email" />
                  <FormControlLabel value="phone" control={<Radio />} label="phone" />
                </Box>
              </RadioGroup>
            </Grid2>
          </Grid2>
          {(!authKeyInfo.type)?
          <></>
          // : <Grid2 container size={12} columns={3} spacing={1} direction={"row"} justifyContent={"space-between"} alignItems={"center"} wrap="nowrap">
          : <Grid2 container size={12} columns={3} spacing={1} alignItems={"center"} wrap="nowrap">
              <Grid2 size={4} >
                  {(authKeyInfo.type==="email")?
                  <Typography variant="h5" className={style.white} > 이메일</Typography>
                  :<Typography variant="h5" className={style.white} > 휴대폰</Typography>
                  }
              </Grid2>
              <Grid2 size={8} className={style.margin_textField}>
                <TextField
                  onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo(authKeyInfo.type,event.target.value)} value={(authKeyInfo.type==="email")?authKeyInfo.email:authKeyInfo.phone}
                  variant="outlined" margin="normal" required fullWidth id={authKeyInfo.type} label="이메일" name={authKeyInfo.type} autoFocus
                  disabled={(!authKeyInfo.type)?true:false} sx={{ margin: '0 auto' }}
                />
              </Grid2>
              <Grid2 size={2} display={"flex"} justifyContent={"center"} className={style.margin_textField}>
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  className={style.submit}
                  onClick = {logInCall}
                  disabled={checkSendAble()}
                  sx={{ width: '60%' }}
                >
                  전송
                </Button>
              </Grid2>
            </Grid2>
          }
          {(!requestFlag)?
          <></>
          // : <Grid2 container size={12} columns={2} spacing={1} direction={"row"} justifyContent={"space-between"} alignItems={"center"} wrap="nowrap">
          : <Grid2 container size={12} columns={2} spacing={1} alignItems={"center"} wrap="nowrap">
              <Grid2 size={4} >
                  <Typography variant="h5" className={style.white} >인증키</Typography>
              </Grid2>
              <Grid2 size={8} className={style.white} >
                <TextField
                  onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("key",event.target.value)} value={authKeyInfo.key}
                  variant="outlined" margin="normal" required fullWidth id="authKey" label="인증키" name="authKey" autoFocus
                  disabled={(!requestFlag)?true:false} sx={{ margin: '0 auto' }}
                />
              </Grid2>
            </Grid2>
          }
          {/* <Grid2 container size={12} spacing={1} direction={"row"} justifyContent={"space-between"} alignItems={"center"} wrap="nowrap"> */}
          <Grid2 container size={12} spacing={1} justifyContent={"space-between"} alignItems={"center"} wrap="nowrap">
            <Button
              type="submit"
              variant="contained"
              color="success"
              className={style.submit}
              onClick = {()=>{window.location.href=pageUrl.login}}
              startIcon={<ArrowBackOutlined />}
            >
              Back
            </Button>
            
            <Button
              type="submit"
              variant="contained"
              color="primary"
              className={style.submit}
              onClick = {authKeyValidation}
              disabled={(!authKeyInfo.key)}
              startIcon={<CheckBoxOutlined />}
            >
              Confirm
            </Button>
          </Grid2>
        </Grid2>
      </Grid2>
    </>
  );
}