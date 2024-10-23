import Image from "next/image";
import Header from "./header";
import Footer from "./footer";

export default function Layout({children,}:{
  children: React.ReactNode
}) {
  return (
    <div>
      <Header />
      <main>{children}</main>
      <Footer />
    </div>
  );
}
