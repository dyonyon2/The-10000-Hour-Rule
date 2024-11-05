import useStyles from "@/app/style";
import { NotificationsActiveOutlined, ViewHeadline, ViewModule } from "@mui/icons-material";
import { Button, Grid, Grid2, MenuItem, Select, SelectChangeEvent, Typography } from "@mui/material";
import { useState } from "react";

export default function MemoBoard() {
    const style = useStyles();  // Style 세팅
    const [viewType,setViewType] = useState<"GRID"|"LIST">("GRID");
    const [sortCnt,setSortCnt] = useState<string>("9");

    function changeViewType(type:"GRID"|"LIST"){
        if(type=="GRID"){
            setViewType("GRID");
            setSortCnt("9");
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
                            <MenuItem value={"9"}>3x3</MenuItem>
                            <MenuItem value={"16"}>4x4</MenuItem>
                            <MenuItem value={"25"}>5x5</MenuItem>
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
                <Grid2 container size={12} justifyContent={"space-around"} alignContent={"space-around"}>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                </Grid2>
                <Grid2 container size={12} justifyContent={"space-around"} alignContent={"space-around"}>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                </Grid2>
                <Grid2 container size={12} justifyContent={"space-around"} alignContent={"space-around"}>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                </Grid2>
                <Grid2 container size={12} justifyContent={"space-around"} alignContent={"space-around"}>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                    <Grid2 className={style.memoItem4x4}>
                        asdf
                    </Grid2>
                </Grid2>
            </Grid2>
        </>
    );
}