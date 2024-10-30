import useStyles from "@/app/style";
import { ArrowBackOutlined, NotificationAddRounded, NotificationImportantRounded, NotificationsActiveOutlined, NotificationsActiveRounded, Person2Outlined, Person2Rounded, PowerSettingsNew, Settings, SettingsOutlined } from "@mui/icons-material";
import { Box, Button, Grid2, IconButton, Typography } from "@mui/material";
// import Image from "next/image";
import AccessAlarmsOutlinedIcon from '@mui/icons-material/AccessAlarmsOutlined';
import { useEffect, useState } from "react";
import { pageUrl } from "@/util/values";


export default function Header() {
  const style = useStyles();  // Style 세팅
  const [userInfo,setUserInfo] = useState({name:"최종영"});
  
  useEffect(()=>{

  },[])

  return (
    <Grid2 container size={12} className={style.header} justifyContent="space-between" >   
      <Grid2 size={1.5} className={style.logo} justifyContent={"center"} alignContent={"center"} onClick={()=>{window.location.href=pageUrl.main}}></Grid2>
      <Grid2 container size={3} className={style.headerMenu} justifyContent={"right"} alignContent={"center"}>
        <Button type="submit" variant="text" className={style.icon} onClick={()=>{window.location.href=pageUrl.user}} sx={{backgroundImage:"url(/logo/아이유.png)", backgroundPosition:"center", backgroundSize:"cover"}}></Button> 
        <Typography alignContent={"center"} className={style.margin_LR} fontFamily={"Noto Serif KR"}>{userInfo.name}님</Typography>
        <Button type="submit" variant="text" className={style.icon}><PowerSettingsNew /></Button> 
        <Button type="submit" variant="text" className={style.icon}><Person2Outlined /></Button>
        <Button type="submit" variant="text" className={style.icon}><NotificationsActiveOutlined /></Button>
        <Button type="submit" variant="text" className={style.icon}><SettingsOutlined /></Button>
      </Grid2>
    </Grid2>
  );
}
