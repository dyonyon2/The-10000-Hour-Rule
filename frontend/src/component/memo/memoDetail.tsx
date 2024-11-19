import useStyles from "@/app/style";
import { ApiCall, apiUrl } from "@/util/apiCall";
import { MemoListInfo } from "@/util/types";
import { useEffect, useState } from "react";

type pageParam ={
    memo_idx:string
  }

export default function MemoDetail({memo_idx}:pageParam) {    
    const style = useStyles();  // Style 세팅
    const [memoData,SetMemoData] = useState<MemoListInfo>();

    useEffect(()=>{
        loadMemo();
    },[]);

    async function loadMemo(){
        var result= await ApiCall.call(ApiCall.queryStringFormat(apiUrl.memoRead,[sessionStorage.getItem('user_id'),'own']),'get',false);
        // console.log(result);
        // if(result.res_status){  // 정상적인 API CALL Return
        //     // alert(result.msg);
        //     if(result.res_status==="1") {  // 메모 읽기
        //         console.log("메모 읽기 성공");
        //         var tmp = JSON.parse(result.res_data);  // res_data(JSON)을 객체로 파싱
        //         SetMemoList(tmp);
        //         setMaxPage(Math.ceil(tmp.length/parseInt(sortCnt)/parseInt(sortCnt)));
        //     } else {
        //         console.log("로그인 되어 있지 않음. 로그인 페이지로 이동");
        //         sessionStorage.setItem("user_id","");
        //         window.location.href = pageUrl.login;
        //     } 
        // } else {    // 비정상적인 API CALL Return
        //     alert("올바르지 않은 Response 입니다."+result);
        // }
    }

    return (
        <>
            <div>test</div>
            <div>{memo_idx}</div>
            <div>test!!!</div>
        </>  
    );
};