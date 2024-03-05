package com.dyonyon.The10000HourRule.code;

public class GlobalConstants {

    // System Config Value
    final public static int LoginAttemptCount = 10;
    final public static int MaxDataLength = 1024;

    // Table Column Name
    final public static String user_id = "user_id";
    final public static String pw = "pw";
    final public static String name = "name";
    final public static String phone = "phone";
    final public static String mail = "mail";
    final public static String nickname = "nickname";
    final public static String sex = "sex";
    final public static String birth = "birth";
    final public static String region = "region";

    // Service Name
    final public static String service_memo = "MEMO";
    final public static String service_calender = "CALENDER";
    final public static String service_routine = "ROUTINE";

    // Content type (개인, 그룹)
    final public static String content_type_user = "U";
    final public static String content_type_group = "G";

    // Memo Form-Data 전달 받을 때 사용하는 값
    final public static String file = "file";
    final public static String json = "json";

    // 권한 값
    final public static String access_all = "CRUD";
    final public static String access_create = "C";
    final public static String access_read = "R";
    final public static String access_update = "U";
    final public static String access_delete = "D";
    final public static String access_read_update = "RU";
    final public static String access_read_update_delete = "RUD";


}