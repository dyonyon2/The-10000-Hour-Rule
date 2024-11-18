"use client"

import MemoDetail from "@/component/memo/memoDetail";
type pageParam ={
  memo_idx:string
}

export default function MemoDetailPage({params}:{params:pageParam}) {
  return (
    <MemoDetail memo_idx={params.memo_idx}/>
  );
}
