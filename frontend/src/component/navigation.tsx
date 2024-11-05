import useStyles from "@/app/style";
import { pageUrl } from "@/util/values";
import { Book, CalendarMonth, EditCalendar, EditCalendarSharp, LibraryBooks, List, ListAlt, Note, NoteAdd, PostAdd, PowerSettingsNew, RuleOutlined } from "@mui/icons-material";
import { Button, Grid2 } from "@mui/material";
import { green } from "@mui/material/colors";
import Image from "next/image";

export default function Navigation() {
  const style = useStyles();  // Style 세팅

  return (
    <Grid2 spacing={2} className={style.navigation}>
      <Button type="submit" variant="text" disableRipple className={style.nav_icon} onClick={()=>{window.location.href=pageUrl.memo}}><LibraryBooks /></Button> 
      <Button type="submit" variant="text" disableRipple className={style.nav_icon} onClick={()=>{window.location.href=pageUrl.calendar}}><EditCalendarSharp /></Button> 
      <Button type="submit" variant="text" disableRipple className={style.nav_icon} onClick={()=>{window.location.href=pageUrl.routine}}><ListAlt /></Button>       
    </Grid2>
  );
}
