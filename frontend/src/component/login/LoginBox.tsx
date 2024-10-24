import useStyles from "@/app/style";
import { LoginInfo } from "@/util/types";
import { Avatar, Button, Grid, Grid2, Link, Paper, TextField } from "@mui/material";
import { useState } from "react";
import Image from "next/image";

export default function LoginBox() {
  const [loginInfo,setLoginInfo] = useState<LoginInfo>({id:"", pw :""});
  const style = useStyles();

  function setInfo(key:string, value:string){
    var newInfo = {...loginInfo};
    if(key==="id") newInfo.id = value;
    else if(key==="pw") newInfo.pw = value;
    setLoginInfo(newInfo);
  }

  return (
    <>
      <Grid2 container className={style.container} >
        <Grid2 className={style.paper}>
          <Image src="/logo/Logo1.png" width={100} height={100} alt="test" className={style.logo} onClick={()=>{
            // 새로고침
          }}/>
          <Avatar className={style.avatar} />
          {loginInfo.id}
          {loginInfo.pw}
          <TextField
            onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("id",event.target.value)}
            value={loginInfo.id}
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id="username"
            label="Username"
            name="username"
            autoFocus
          />
          <TextField
            onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("pw",event.target.value)}
            value={loginInfo.pw}
            variant="outlined"
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="current-password"
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            className={style.submit}
            // onClick = {handelLogin}
          >
            Sign In
          </Button>
          <Link className={style.margin} href="/login/find" variant="body2">
            {"Don't remeber ID/PW?"}
          </Link>
          <Link className={style.margin} href="/login/signup" variant="body2">
            {"Don't have an account? Sign Up"}
          </Link>
        </Grid2>
      </Grid2>
    </>
  );
}