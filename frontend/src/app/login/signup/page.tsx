"use client"

import useStyles from "@/app/style";
import { Avatar, FormControlLabel, Grid2, Radio, RadioGroup, TextField, Typography } from "@mui/material";
import React, { useState } from "react";

export default function Signup() {
  const style = useStyles();  // Style 세팅
  const [gender, setGender] = useState('');

  return(
    <>
    {/* <RadioGroup value={gender} onChange={handleChange} row >
      <FormControlLabel value="male" control={<Radio />} label="Male" />
      <FormControlLabel value="female" control={<Radio />} label="Female" />
    </RadioGroup> */}
      <Grid2 container spacing={2} justifyContent={"flex-start"} alignItems={"center"} direction={"row"}  className={style.red}>
        {/* <Grid2 container> */}
          <Grid2 container direction={"row"}>
              <Avatar />
              <Avatar />
          </Grid2>
          <Grid2>
          <Typography> name </Typography>
          </Grid2>
        {/* </Grid2> */}
        <Grid2>
          <TextField
            // onChange={(event:React.ChangeEvent<HTMLInputElement>)=>setInfo("name",event.target.value)}
            value={"TEST"}
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id="name"
            label="name"
            name="name"
            autoFocus
            sx={{ margin: '0 auto' }} 
          />
        </Grid2>
      </Grid2>
    </>
  );
}