import useStyles from "@/app/style";
import { ArrowBackOutlined, NotificationAddRounded, NotificationImportantRounded, NotificationsActiveOutlined, NotificationsActiveRounded, Person2Outlined, Person2Rounded, PowerSettingsNew, Settings, SettingsOutlined } from "@mui/icons-material";
import { Box, Button, Grid2, IconButton, Typography } from "@mui/material";
// import Image from "next/image";
import AccessAlarmsOutlinedIcon from '@mui/icons-material/AccessAlarmsOutlined';
import { useEffect, useState } from "react";
import { pageUrl } from "@/util/values";
import { ApiCall, apiUrl } from "@/util/apiCall";


export default function Header() {
  const style = useStyles();  // Style 세팅
  const [userId,setUserId] = useState<string|null>("");
  // const [userInfo,setUserInfo] = useState({name:"최종영"});
  
  useEffect(()=>{
    loginCheck();
    // TDL : 사용자 이미지, 사용자 이름 정보 불러오기! => userInfo에 저장
  },[])

  function loginCheck(){
    if(sessionStorage.getItem('user_id')){
      setUserId(sessionStorage.getItem('user_id'));
      console.log("로그인 되어 있음.");
    }
    else {
      console.log("로그인 되어 있지 않음. 로그인 페이지로 이동");
      window.location.href = pageUrl.login;
    }
  }

  async function logout() {
    if(confirm("로그아웃 하시겠습니까?")){
      console.log("로그아웃");
      await logoutCall();
      sessionStorage.setItem("user_id","");
      window.location.href = pageUrl.login;
    }
  }

  async function logoutCall(){ // LoginFormInfo로 서버에 로그인 요청
    var result= await ApiCall.call(apiUrl.logout,'post',{user_id:userId},false);
    if(result.res_status){  // 정상적인 API CALL Return
      alert(result.msg);
      if(result.res_status==="1") {  // 로그인 성공
        console.log("로그아웃 성공");
      } else {
        console.log("로그아웃 실패");
      } 
      console.log(result);
    } else {    // 비정상적인 API CALL Return
      alert("올바르지 않은 Response 입니다."+result);
    }
  }

  return (
    <Grid2 container size={12} className={style.header} justifyContent="space-between" >   
      <Grid2 size={1.5} className={style.logo} justifyContent={"center"} alignContent={"center"} onClick={()=>{window.location.href=pageUrl.main}}></Grid2>
      <Grid2 container size={3} className={style.headerMenu} justifyContent={"right"} alignContent={"center"}>
        <Button type="submit" variant="text" className={style.icon} onClick={()=>{window.location.href=pageUrl.user}} sx={{backgroundImage:"url(/logo/아이유.png)", backgroundPosition:"center", backgroundSize:"cover"}}></Button> 
        <Typography alignContent={"center"} className={style.margin_LR} fontFamily={"Noto Serif KR"}>{userId}님</Typography>
        <Button type="submit" variant="text" className={style.icon} onClick={logout}><PowerSettingsNew /></Button> 
        <Button type="submit" variant="text" className={style.icon}><Person2Outlined /></Button>
        <Button type="submit" variant="text" className={style.icon}><NotificationsActiveOutlined /></Button>
        <Button type="submit" variant="text" className={style.icon}><SettingsOutlined /></Button>
      </Grid2>
    </Grid2>
  );
}
