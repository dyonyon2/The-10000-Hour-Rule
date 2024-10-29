import useStyles from "@/app/style";
import { Grid2 } from "@mui/material";
import { green } from "@mui/material/colors";
import Image from "next/image";

export default function Navigation() {
  const style = useStyles();  // Style 세팅

  return (
    <Grid2 size={1} className={style.navigation} >
      <div>nav</div>  
    </Grid2>
  );
}
