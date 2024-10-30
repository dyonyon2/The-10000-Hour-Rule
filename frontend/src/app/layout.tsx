"use client"

import { usePathname } from "next/navigation";
import "./globals.css";
import Layout from "@/component/layout";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const pathName = usePathname();

  function pathCheck(){ // 로그인 페이지는 Layout 적용하지 않도록 세팅!
    const layoutExcept = ["login"];
    for( const url of layoutExcept){
      if(pathName.includes(url))
        return <>{children}</>;
    }
    return <Layout>{children}</Layout>;
  }

  return (
    <html lang="en">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" />
        <link href="https://fonts.googleapis.com/css2?family=Noto+Serif+KR:wght@200..900&display=swap" rel="stylesheet" />
      </head>
      <body>
        {pathCheck()}
      </body>
    </html>
  );
}