"use client"

import Image from "next/image";
import Header from "./header";
import Navigation from "./navigation";
import { Grid2 } from "@mui/material";

export default function Layout({children,}:{
  children: React.ReactNode
}) {
  return (
    <Grid2 container size={12}>
      <Header />
      <Navigation />
      <Grid2 size={11} sx={{backgroundColor:"orange"}} height="100vh">
        <main>{children}</main>
      </Grid2>
    </Grid2>
  );
}