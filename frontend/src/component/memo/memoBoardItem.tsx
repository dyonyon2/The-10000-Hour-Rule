import useStyles from "@/app/style";
import { MemoListInfo } from "@/util/types";
import { NotificationsActiveOutlined, ViewHeadline, ViewModule } from "@mui/icons-material";
import { Button, Grid, Grid2, MenuItem, Select, SelectChangeEvent, Typography } from "@mui/material";
import { useState } from "react";

type props = {
    style   : string;
    data    : MemoListInfo;
}

const MemoBoardItem: React.FC<props> = ({style, data}) => {    
    return (
        <Grid2 className={style}>
            {data.title}
        </Grid2>
    );
};

export default MemoBoardItem;