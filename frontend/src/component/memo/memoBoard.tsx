import useStyles from "@/app/style";
import { MemoListInfo } from "@/util/types";
import { AllInbox, AllInclusive, AllOut, Create, FollowTheSigns, Group, NoteAlt, NotificationsActiveOutlined, Person, PostAdd, Share, ViewHeadline, ViewModule } from "@mui/icons-material";
import { Button, Grid, Grid2, MenuItem, Pagination, Select, SelectChangeEvent, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import MemoBoardItem from "./memoBoardItem";

export default function MemoBoard() {
    const style = useStyles();  // Style 세팅
    const [memoList,SetMemoList] = useState<MemoListInfo[]>([{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title1",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title2",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title3",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title4",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title5",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title6",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title7",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title8",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title1",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title2",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title3",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title4",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title5",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title6",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title7",content     : "content",create_dt   : "create",update_dt   : "update",},{memo_idx    : "memo_idx",memo_type   : "memo_type",owner_id    : "owner_id",title       : "title8",content     : "content",create_dt   : "create",update_dt   : "update",}]);
    const [memoType,setMemoType] = useState<"own"|"group"|"follow">("own");
    const [viewType,setViewType] = useState<"GRID"|"LIST">("GRID");
    const [sortCnt,setSortCnt] = useState<string>("3");
    const [maxPage,setMaxPage] = useState<number>(1);
    const [page,setPage] = useState<number>(1);

    useEffect(()=>{
        // 1. 내 메모 불러오기
        // 2. 그룹 메모 불러오기 
        // 3. 내가 팔로우하는 메모 불러오기
        console.log("IN effect!!");
    },[])
    
    const handleChange = (event: React.ChangeEvent<unknown>, value: number) => {
        setPage(value);
    };

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
                <Grid2 size={6} display="flex">
                    <Typography variant="h3">Memo Board - </Typography>
                    <Button type="submit" variant="text" className={style.icon_square} sx={{backgroundColor: (memoType=="own")?"gray":"white"}} onClick={()=>{setMemoType("own")}}><Person /></Button>
                    <Button type="submit" variant="text" className={style.icon_square} sx={{backgroundColor: (memoType=="group")?"gray":"white"}} onClick={()=>{setMemoType("group")}}><Group /></Button>
                    <Button type="submit" variant="text" className={style.icon_square} sx={{backgroundColor: (memoType=="follow")?"gray":"white"}} onClick={()=>{setMemoType("follow")}}><Share /></Button>
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
            <Grid2 container size={12} className={style.margin_TB10} />
            <Grid2 container size={12} className={style.memoBoard} justifyContent={"space-around"} alignContent={"space-around"}>
                <Grid2 container size={12} className={style.margin_TB10} />
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
                <Grid2 container size={12} className={style.margin_TB10} />
            </Grid2>
            
            <Grid2 container size={12} justifyContent={"space-around"} alignContent={"space-around"}>
                <Grid2 container size={3} justifyContent={"space-around"} alignContent={"space-around"}>
                    {/* <Pagination count={maxPage} page={page} onChange={handleChange} showFirstButton showLastButton /> */}
                </Grid2>
                <Grid2 container size={3} justifyContent={"space-around"} alignContent={"space-around"}>
                    <Pagination count={maxPage} page={page} onChange={handleChange} showFirstButton showLastButton />
                </Grid2>
                <Grid2 container size={3} justifyContent={"flex-end"} alignContent={"space-around"}>
                    <Button type="submit" variant="text" className={style.icon_square} onClick={()=>{setMemoType("follow")}}><Create /></Button>
                </Grid2>
            </Grid2>
        </>
    );
}