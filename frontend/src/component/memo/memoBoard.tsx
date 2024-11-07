import useStyles from "@/app/style";
import { MemoListInfo } from "@/util/types";
import { NotificationsActiveOutlined, ViewHeadline, ViewModule } from "@mui/icons-material";
import { Button, Grid, Grid2, MenuItem, Select, SelectChangeEvent, Typography } from "@mui/material";
import { useState } from "react";
import MemoBoardItem from "./memoBoardItem";

export default function MemoBoard() {
    const style = useStyles();  // Style 세팅
    const [viewType,setViewType] = useState<"GRID"|"LIST">("GRID");
    const [sortCnt,setSortCnt] = useState<string>("3");
    const [memoList,SetMemoList] = useState<MemoListInfo[]>([{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title1",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title2",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title3",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title4",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title5",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title6",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title7",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title8",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title9",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title10",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title11",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title12",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title13",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title14",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title15",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title16",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title17",content     : "content",create_dt   : "create",update_dt   : "update",}]);
    const [memo,SetMemo] = useState<MemoListInfo>({memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title",content     : "content",create_dt   : "create",update_dt   : "update",});

    function changeViewType(type:"GRID"|"LIST"){
        if(type=="GRID"){
            setViewType("GRID");
            setSortCnt("3");
        } else if (type=="LIST"){
            setViewType("LIST");
            setSortCnt("10");
        }
    }
    return (
        <>
            <Grid2 container className={style.white} size={12}>
                <Grid2 size={6}>
                    <Typography variant="h3">Memo Board</Typography>
                </Grid2>
                <Grid2 size={6} display="flex" justifyContent="flex-end">
                    <Button type="submit" variant="text" className={style.icon_square} sx={{backgroundColor: (viewType=="GRID")?"gray":"white"}} onClick={()=>{changeViewType("GRID")}}><ViewModule /></Button>
                    <Button type="submit" variant="text" className={style.icon_square} sx={{backgroundColor: (viewType=="LIST")?"gray":"white"}} onClick={()=>{changeViewType("LIST")}}><ViewHeadline /></Button>
                    {
                        (viewType=="GRID")?
                        <Select id="demo-simple-select" value={sortCnt} label="type" sx={{width:"100px"}} onChange={(event : SelectChangeEvent)=>{setSortCnt(event.target.value);}}>
                            <MenuItem value={"3"}>3x3</MenuItem>
                            <MenuItem value={"4"}>4x4</MenuItem>
                            <MenuItem value={"5"}>5x5</MenuItem>
                        </Select>
                        :<Select id="demo-simple-select" value={sortCnt} label="type" sx={{width:"100px"}} onChange={(event : SelectChangeEvent)=>{setSortCnt(event.target.value);}}>
                            <MenuItem value={"10"}>10</MenuItem>
                            <MenuItem value={"15"}>15</MenuItem>
                            <MenuItem value={"20"}>20</MenuItem>
                        </Select>
                    }
                </Grid2>
            </Grid2>
            <Grid2 container size={12} className={style.memoBoard} justifyContent={"space-around"} alignContent={"space-around"}>
                {
                    Array.from({length:parseInt(sortCnt)},(_, index) => (
                        <Grid2 container size={12} justifyContent={"space-around"} alignContent={"space-around"}>
                            {
                                memoList.map((memo, idx) => (
                                    ((index*parseInt(sortCnt))<=idx&&idx<((index+1)*parseInt(sortCnt)))?
                                    <MemoBoardItem style={sortCnt=="3"?style.memoItem3x3:(sortCnt=="4"?style.memoItem4x4:style.memoItem5x5)} data={memo} />
                                    :<></>
                                ))
                            }
                        </Grid2>
                    ))
                }
            </Grid2>
        </>
    );
}