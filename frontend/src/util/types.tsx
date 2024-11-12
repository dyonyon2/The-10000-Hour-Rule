export enum ObjectKeys {
    loginInfo_id = "id",
    loginInfo_pw = "pw"
}

export type LoginInfo = {
    user_id     : string;
    pw          : string;
}

export type UserInfo = {
    user_id     : string;
    pw          : string;
}

export type AuthKeyInfo = {
    name        : string;
    type        : string;  // findIdByPhone, findIdByEmail, resetPwByPhone, resetPwByEmail
    id          : string;
    phone       : string;
    email       : string;
    key         : string;
}

export type SignInfo = {
    user_id     : string,
    pw          : string,
    name        : string,
    nickname    : string,
    sex         : string,
    birth       : string,
    region      : string,
    phone       : string,
    mail        : string,
}

export type MemoListInfo = {
    memo_idx    : string,
    memo_type   : string,
    owner_id    : string,
    title       : string,
    content     : string,
    category_no : string,
    favorites   : string,
    create_dt   : string,
    update_dt   : string,
}

export type ApiReturnForm = {
    status      : string, // 0 처리중, 1 처리완료, -1 처리중 에러
    msg         : string, // 사용자에게 전달할 데이터
    res_status  : string,  // 정상 000000, 에러 DB0001
    res_data    : string,  // STATUS가 1일 때에는 특정 객체, 1일때 데이터 없으면 안된것으로 판단하면 됨, -1 에러일때에는 에러 메시지
    err_code    : string
}

// {
//     "user_id": "dyonyon",
//     "pw": "1234",
//     "name": "최종영",
//     "nickname": "됸",
//     "sex": "M",
//     "birth": "1997/12/20",
//     "region": "경기도",
//     "phone": "010-6342-8173",
//     "mail": "jychoi9712@naver.com"
// }