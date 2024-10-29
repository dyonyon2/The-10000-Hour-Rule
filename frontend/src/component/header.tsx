import useStyles from "@/app/style";
import { Box, Grid2 } from "@mui/material";
import Image from "next/image";

export default function Header() {
  const style = useStyles();  // Style 세팅

  return (
    <Grid2 container size={12} className={style.red} justifyContent="space-between" height="10vh">   
      <Grid2 size={1.5} className={style.logo} justifyContent={"center"} alignContent={"center"} onClick={()=>{console.log("Click!!")}}>
      </Grid2>
      <Grid2 size={3} className={style.blue}>
      <div>2</div>
      </Grid2>
    </Grid2>
  );
}
