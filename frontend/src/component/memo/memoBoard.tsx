import useStyles from "@/app/style";
import { MemoListInfo } from "@/util/types";
import { AllInbox, AllInclusive, AllOut, Create, FollowTheSigns, Group, NoteAlt, NotificationsActiveOutlined, Person, PostAdd, Share, Star, StarOutline, ViewHeadline, ViewModule } from "@mui/icons-material";
import { Button, Grid, Grid2, MenuItem, Pagination, Paper, Select, SelectChangeEvent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import MemoBoardItem from "./memoBoardItem";
import { ApiCall, apiUrl } from "@/util/apiCall";
import { pageUrl } from "@/util/values";

export default function MemoBoard() {
    const style = useStyles();  // Style 세팅
    const [userId,setUserId] = useState<string|null>("");
    const [memoList,SetMemoList] = useState<MemoListInfo[]>([]);
    const [memoType,setMemoType] = useState<"own"|"group"|"follow">("own");
    const [viewType,setViewType] = useState<"GRID"|"LIST">("GRID");
    const [sortCnt,setSortCnt] = useState<string>("3");
    const [maxPage,setMaxPage] = useState<number>(1);
    const [page,setPage] = useState<number>(1);

    useEffect(()=>{
        // 1. 내 메모 불러오기
        // 2. 그룹 메모 불러오기 
        // 3. 내가 팔로우하는 메모 불러오기
        loginCheck();
        loadOwnMemo();
        console.log("IN effect!!");
        console.log(1/3);
    },[]);
    
    function loginCheck(){
        if(sessionStorage.getItem('user_id')){
            setUserId(sessionStorage.getItem('user_id'));
            console.log("로그인 되어 있음.");
        }
        else {
            console.log("로그인 되어 있지 않음. 로그인 페이지로 이동");
            window.location.href = pageUrl.login;
        }
    }

    async function loadOwnMemo(){
        var result= await ApiCall.call(ApiCall.queryStringFormat(apiUrl.memoListRead,[sessionStorage.getItem('user_id'),'own']),'get');
        console.log(result);
        if(result.res_status){  // 정상적인 API CALL Return
            // alert(result.msg);
            if(result.res_status==="1") {  // 메모 읽기
                console.log("메모 읽기 성공");
                var tmp = JSON.parse(result.res_data);  // res_data(JSON)을 객체로 파싱
                SetMemoList(tmp);
                setMaxPage(Math.ceil(tmp.length/parseInt(sortCnt)/parseInt(sortCnt)));
            } else {
                console.log("로그인 되어 있지 않음. 로그인 페이지로 이동");
                sessionStorage.setItem("user_id","");
                window.location.href = pageUrl.login;
            } 
        } else {    // 비정상적인 API CALL Return
            alert("올바르지 않은 Response 입니다."+result);
        }
    }

    const handleChange = (event: React.ChangeEvent<unknown>, value: number) => {
        setPage(value);
    };

    function changeViewSort(type:"GRID"|"LIST",value: string){
        if(type=="GRID"){
            setPage(1);
            setSortCnt(value);
            setMaxPage(Math.ceil(memoList.length/parseInt(value)/parseInt(value)));
        } else if (type=="LIST"){
            setPage(1);
            setSortCnt(value);
            setMaxPage(Math.ceil(memoList.length/parseInt(value)));
        }
    }

    function changeViewType(type:"GRID"|"LIST"){
        if(type=="GRID"){
            setPage(1);
            setViewType("GRID");
            setSortCnt("3");
            setMaxPage(Math.ceil(memoList.length/3/3));
        } else if (type=="LIST"){
            setPage(1);
            setViewType("LIST");
            setSortCnt("10");
            setMaxPage(Math.ceil(memoList.length/10));
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
                        <Select id="demo-simple-select" value={sortCnt} label="type" sx={{width:"100px"}} onChange={(event : SelectChangeEvent)=>{changeViewSort("GRID",event.target.value)}}>
                            <MenuItem value={"3"}>3x3</MenuItem>
                            <MenuItem value={"4"}>4x4</MenuItem>
                            <MenuItem value={"5"}>5x5</MenuItem>
                        </Select>
                        :<Select id="demo-simple-select" value={sortCnt} label="type" sx={{width:"100px"}} onChange={(event : SelectChangeEvent)=>{changeViewSort("LIST",event.target.value)}}>
                            <MenuItem value={"10"}>10</MenuItem>
                            <MenuItem value={"15"}>15</MenuItem>
                            <MenuItem value={"20"}>20</MenuItem>
                        </Select>
                    }
                </Grid2>
            </Grid2>
            <Grid2 container size={12} className={style.margin_TB10} />
            {   
                (viewType=="GRID")?
                <Grid2 container size={12} className={style.memoGridBoard} justifyContent={"space-around"} alignContent={"space-around"}>
                    <Grid2 container size={12} className={style.margin_TB10} />
                    {
                        Array.from({length:parseInt(sortCnt)},(_, index) => (
                            <Grid2 container size={12} justifyContent={"space-around"} alignContent={"space-around"}>
                                {
                                    (memoList.slice((page-1)*parseInt(sortCnt)*parseInt(sortCnt),page*parseInt(sortCnt)*parseInt(sortCnt))).map((memo, idx) => (
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
                :<Grid2 container size={12} justifyContent={"space-around"} alignContent={"space-around"}>
                    <Grid2 container size={12} className={style.margin_TB10} />
                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead sx={{backgroundColor:"lightgray"}}>
                                <TableRow>
                                    <TableCell sx={{width:'5%'}}>
                                        즐겨찾기
                                    </TableCell>
                                    <TableCell sx={{width:'5%'}}>
                                        카테고리
                                    </TableCell>
                                    <TableCell sx={{width:'20%'}}>
                                        제목
                                    </TableCell>
                                    <TableCell sx={{width:'60%'}}>
                                        내용
                                    </TableCell>
                                    <TableCell sx={{width:'10%'}}>
                                        업데이트 시간
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {
                                    (memoList.slice((page-1)*parseInt(sortCnt),page*parseInt(sortCnt))).map((memo, idx) => (
                                        <TableRow>
                                            <TableCell>
                                                {memo.favorites=="Y"?<Star fontSize="small" color="error" />:<StarOutline fontSize="small"/>}
                                            </TableCell>
                                            <TableCell>
                                                {(!memo.category_no)?"분류없음":memo.category_no}
                                            </TableCell>
                                            <TableCell>
                                                {memo.title}
                                            </TableCell>
                                            <TableCell>
                                                {memo.content}
                                            </TableCell>
                                            <TableCell>
                                                {memo.update_dt}
                                            </TableCell>
                                        </TableRow>
                                    ))
                                }
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <Grid2 container size={12} className={style.margin_TB10} />
                </Grid2>
            }           
            
            <Grid2 container size={12} justifyContent={"space-between"} alignContent={"space-around"} >
                <Grid2 container size={3} justifyContent={"space-around"} alignContent={"space-around"} >
                </Grid2>
                <Grid2 container size={3} justifyContent={"space-around"} alignContent={"space-around"} >
                    <Pagination count={maxPage} page={page} onChange={handleChange} showFirstButton showLastButton />
                </Grid2>
                <Grid2 container size={3} justifyContent={"flex-end"} alignContent={"space-around"} >
                    <Button type="submit" fullWidth variant="text" className={style.icon_square} onClick={()=>{setMemoType("follow")}}><Create />+</Button>
                </Grid2>
            </Grid2>
        </>
    );
}