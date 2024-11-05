"use client"

import Image from "next/image";
import Header from "./header";
import Navigation from "./navigation";
import { Grid2 } from "@mui/material";
import useStyles from "@/app/style";

export default function Layout({children,}:{
  children: React.ReactNode
}) {
  const style = useStyles();  // Style 세팅

  return (
    <Grid2 container size={12}>
      <Header />
      <Navigation />
      <Grid2 className={style.mainBody}>
        <main>{children}</main>
      </Grid2>
    </Grid2>
  );
}