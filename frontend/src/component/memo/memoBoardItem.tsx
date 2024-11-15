import useStyles from "@/app/style";
import { ApiCall, apiUrl } from "@/util/apiCall";
import { MemoListInfo } from "@/util/types";
import { NotificationsActiveOutlined, Star, StarOutline, Start, ViewHeadline, ViewModule } from "@mui/icons-material";
import { Box, Button, Divider, Grid, Grid2, MenuItem, Select, SelectChangeEvent, Typography } from "@mui/material";
import { useState } from "react";

type props = {
    style   : string;
    data    : MemoListInfo;
    changeFavorite : (target:MemoListInfo) => void;
}



const MemoBoardItem: React.FC<props> = ({style, data, changeFavorite}) => {    

    return (
        <Grid2 container className={style} alignContent={"flex-start"}>
            <Grid2 container size={12} justifyContent="space-between" wrap="nowrap">
                <Typography variant="h4" noWrap>
                    {data.title}
                </Typography>
                {data.favorites=="Y"?<Star fontSize="small" color="error" onClick={()=>{changeFavorite(data)}}/>:<StarOutline fontSize="small" onClick={()=>{changeFavorite(data)}}/>}
            </Grid2>
            <Grid2 container size={12} justifyContent="right">
                <Typography variant="body1" noWrap>
                    {(!data.category_no)?"분류없음":data.category_no}_{data.update_dt}
                </Typography>
            </Grid2>
            <Grid2 size={12} >
                <Divider  sx={{ borderColor: 'black', borderWidth: '1px' }} />
            </Grid2>
            
            <Grid2 size={12} sx={{height: '50%'}}> 
                <Box
                    sx={{
                        wordWrap: 'break-word', // 긴 단어가 화면을 넘어가지 않게 자동으로 줄바꿈
                        display: '-webkit-box',
                        overflow: 'scroll',
                        WebkitBoxOrient: 'vertical',
                        textOverflow: 'ellipsis',
                        height: '100%'
                    }}
                >
                    {data.content}
                </Box>
            </Grid2>
        </Grid2>
    );
};

export default MemoBoardItem;