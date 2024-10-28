export enum ObjectKeys {
    loginInfo_id = "id",
    loginInfo_pw = "pw"
}

export type LoginInfo = {
    id : string;
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