export enum ObjectKeys {
    loginInfo_id = "id",
    loginInfo_pw = "pw"
}

export type LoginInfo = {
    user_id : string;
    pw : string;
}

export type authKeyInfo = {
    name : string;
    type : string;  // findIdByPhone, findIdByEmail, resetPwByPhone, resetPwByEmail
    id : string;
    phone : string;
    email : string;
    key : string;
}

export type signInfo = {
    user_id : string,
    pw : string,
    name : string,
    nickname : string,
    sex : string,
    birth : string,
    region : string,
    phone : string,
    mail : string,
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