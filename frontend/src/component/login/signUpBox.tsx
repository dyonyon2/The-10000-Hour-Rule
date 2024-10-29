import useStyles from "@/app/style";
import { toUpperString } from "@/util/stringUtil";
import { authKeyInfo, signInfo } from "@/util/types";
import { pageUrl } from "@/util/values";
import { ArrowBackOutlined, CheckBoxOutlined, ConfirmationNumberOutlined } from "@mui/icons-material";
import { Box, Button, FormControlLabel, Grid2, Radio, RadioGroup, TextField, Typography } from "@mui/material";
import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function SignUpBox() {
  const [signInfo,setSignInfo] = useState<signInfo>({user_id : "",pw : "",name : "",nickname : "",sex : "",birth : "",region : "",phone : "",mail : ""});  // 회원가입 데이터
  const style = useStyles();  // Style 세팅

  function checkSignInfo(){
    if((!signInfo.user_id)||(!signInfo.pw)||(!signInfo.name)||(!signInfo.sex)||(!signInfo.birth)||(!signInfo.phone)||(!signInfo.mail))
      return true;
    else
      return false;
  }

  function signUpCall() {

  }

  function setInfo(key:string, value:string){ // 회원가입 정보 입력
    console.log(key,value);
    var newInfo = {...signInfo};
    if(key==="user_id") newInfo.user_id = value;
    else if(key==="pw") newInfo.pw = value;
    else if(key==="name") newInfo.name = value;
    else if(key==="nickname") newInfo.nickname = value;
    else if(key==="sex") newInfo.sex = value;
    else if(key==="birth") newInfo.birth = value;
    else if(key==="region") newInfo.region = value;
    else if(key==="phone") newInfo.phone = value;
    else if(key==="mail") newInfo.mail = value;
    setSignInfo(newInfo);
  }

  return (
    <>
      <Grid2 container  className={style.container} direction={"column"} wrap="nowrap">
        <Grid2 container className={style.paper} direction={"column"} wrap="nowrap">
          <Typography variant="h3"> 회원가입 </Typography>
          <div className={style.divider} />
          {/* <Grid2 container size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"} wrap="nowrap" > */}
          <Grid2 container size={12} columns={2} spacing={2} alignItems={"center"} wrap="nowrap" >
            <Grid2 size={4} className={style.white}>
              <Typography variant="h5" > 이름</Typography>
            </Grid2>
            <Grid2 size={8} className={style.white}>
              <TextField
                onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("name",event.target.value)} value={signInfo.name}
                variant="outlined" margin="normal" required id="name" label="홍길동" name="name" fullWidth autoFocus sx={{ margin: '0 auto' }}
              />
            </Grid2>
          </Grid2>
          {/* <Grid2 container className={style.margin_textField} size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"} wrap="nowrap" > */}
          <Grid2 container className={style.margin_textField} size={12} columns={2} spacing={2} alignItems={"center"} wrap="nowrap" >
            <Grid2 size={4} className={style.white}>
              <Typography variant="h5" > 아이디</Typography>
            </Grid2>
            <Grid2 size={8} className={style.white}>
              <TextField
                onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("user_id",event.target.value)} value={signInfo.user_id}
                variant="outlined" margin="normal" required id="user_id" label="id" name="user_id" fullWidth autoFocus sx={{ margin: '0 auto' }}
              />
            </Grid2>
          </Grid2>
          {/* <Grid2 container size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"} wrap="nowrap" > */}
          <Grid2 container size={12} columns={2} spacing={2} alignItems={"center"} wrap="nowrap" >
            <Grid2 size={4} className={style.white}>
              <Typography variant="h5" >비밀번호</Typography>
            </Grid2>
            <Grid2 size={8} className={style.white}>
              <TextField
                onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("pw",event.target.value)} value={signInfo.pw}
                variant="outlined" margin="normal" required id="pw" label="******" name="pw" type="password" fullWidth autoFocus sx={{ margin: '0 auto' }}
              />
            </Grid2>
          </Grid2>
          {/* <Grid2 container className={style.margin_textField}  size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"} wrap="nowrap" > */}
          <Grid2 container className={style.margin_textField}  size={12} columns={2} spacing={2} alignItems={"center"} wrap="nowrap" >
            <Grid2 size={4} className={style.white}>
              <Typography variant="h5" > 이메일</Typography>
            </Grid2>
            <Grid2 size={8} className={style.white}>
              <TextField
                onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("mail",event.target.value)} value={signInfo.mail}
                variant="outlined" margin="normal" required id="mail" label="abc@naver.com" name="mail" fullWidth autoFocus sx={{ margin: '0 auto' }}
              />
            </Grid2>
          </Grid2>
          {/* <Grid2 container size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"} wrap="nowrap" > */}
          <Grid2 container size={12} columns={2} spacing={2} alignItems={"center"} wrap="nowrap" >
            <Grid2 size={4} className={style.white}>
              <Typography variant="h5" > 휴대폰</Typography>
            </Grid2>
            <Grid2 size={8} className={style.white}>
              <TextField
                onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("phone",event.target.value)} value={signInfo.phone}
                variant="outlined" margin="normal" required id="phone" label="010-0000-0000" name="phone" fullWidth autoFocus sx={{ margin: '0 auto' }}
              />
            </Grid2>
          </Grid2>
          {/* <Grid2 container size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"} wrap="nowrap" sx={{ marginTop: '20px' }}> */}
          <Grid2 container size={12} columns={2} spacing={2} alignItems={"center"} wrap="nowrap" sx={{ marginTop: '20px' }}>
            <Grid2 size={4} className={style.white}>
              <Typography variant="h5" > 생년월일</Typography>
            </Grid2>
            <Grid2 size={8} className={style.white}>
              <TextField
                onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("birth",event.target.value)} value={signInfo.birth}
                variant="outlined" margin="normal" required id="birth" label="YYYYMMDD" name="birth" fullWidth autoFocus sx={{ margin: '0 auto' }}
              />
            </Grid2>
          </Grid2>
          {/* <Grid2 container size={12} columns={2} spacing={2} direction={"row"} justifyContent={"flex-start"} alignItems={"center"}  wrap="nowrap" sx={{ marginTop: '20px' }} > */}
          <Grid2 container size={12} columns={2} spacing={2} alignItems={"center"}  wrap="nowrap" sx={{ marginTop: '20px' }} >
            <Grid2 size={4} >
              <Typography variant="h5" className={style.white} >성별</Typography>
            </Grid2>
            <Grid2 size={8} className={style.white} >
              <RadioGroup value={signInfo.sex} row onChange={(event:React.ChangeEvent<HTMLInputElement>)=>{setInfo("sex",event.target.value)}} >
                <Box display="flex" justifyContent="center" width="100%" >
                  <FormControlLabel value="남자" control={<Radio />} label="남자" />
                  <FormControlLabel value="여자" control={<Radio />} label="여자" />
                </Box>
              </RadioGroup>
            </Grid2>
          </Grid2>
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
              onClick = {signUpCall}
              disabled={checkSignInfo()}
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