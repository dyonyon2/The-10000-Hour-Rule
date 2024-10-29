// "use client"
import { Grid2 } from "@mui/material";
import React from "react";

export default function Test() {
  return(
    <Grid2 container size={12}>
      <Grid2 size={1} sx={{backgroundColor:"red"}}>
        <div>First</div>
      </Grid2>
      <Grid2 size={11} sx={{backgroundColor:"white"}} >
      <div>First</div>

      </Grid2>
      <Grid2 size={12} sx={{backgroundColor:"gray"}}>
      <div>First</div>

      </Grid2>
      <Grid2 size={12} sx={{backgroundColor:"green"}}>
      <div>First</div>

      </Grid2>
    </Grid2>
  );
}