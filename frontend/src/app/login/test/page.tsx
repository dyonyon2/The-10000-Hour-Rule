"use client"
import useStyles from "@/app/style";
import { Box, Grid2, Typography } from "@mui/material";
import React from "react";

export default function Test() {
  const style = useStyles();  // Style 세팅
  return(
    <Box
      sx={{
        whiteSpace: 'pre-wrap', // 줄바꿈 및 공백 유지
        wordWrap: 'break-word', // 긴 단어가 화면을 넘어가지 않게 자동으로 줄바꿈
        backgroundColor: "red",
        padding: 2,
      }}
    >
      111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111s111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
    </Box>
    // <Grid2 container size={12}>
    //   <Grid2 size={1} sx={{backgroundColor:"red"}}>
    //     <div>First</div>
    //   </Grid2>
    //   <Grid2 size={11} sx={{backgroundColor:"white"}} >
    //   <div>First</div>

    //   </Grid2>
    //   <Grid2 size={12} sx={{backgroundColor:"gray"}}>
    //   <div>First</div>

    //   </Grid2>
    //   <Grid2 size={12} sx={{backgroundColor:"green",height:"50vh"}}>
    //   <div>First</div>

    //   </Grid2>
    // </Grid2>

    // <Grid2 container size={12} sx={{height:"100vh"}} >
    //   <Grid2 className={style.red} size={4}>
    //       asdf
    //   </Grid2>
    //   <Grid2 className={style.orange} size={4}>
    //       asdf
    //   </Grid2>
    //   <Grid2 className={style.yellow} size={4}>
    //       asdf
    //   </Grid2>
    //   <Grid2 className={style.gray} size={4}>
    //       asdf
    //   </Grid2>
    // </Grid2>
    // <>
    //   <Grid2 container size={12} >
    //     <Grid2 className={style.red} size={4}>
    //         asdfff
    //     </Grid2>
    //     <Grid2 className={style.orange} size={4}>
    //         asdf
    //     </Grid2>
    //     <Grid2 className={style.yellow} size={4}>
    //         asdf
    //     </Grid2>
    //   </Grid2>
    // </>
  );
}