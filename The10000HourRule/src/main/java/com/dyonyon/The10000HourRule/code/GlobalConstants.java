package com.dyonyon.The10000HourRule.code;

public class GlobalConstants {

    // System Config Value
    final public static int LoginAttemptCount = 10;
    final public static int MaxDataLength = 800;

    // Table Column Name
    final public static String USER_ID = "user_id";
    final public static String PW = "pw";
    final public static String NAME = "name";
    final public static String PHONE = "phone";
    final public static String MAIL = "mail";
    final public static String NICKNAME = "nickname";
    final public static String SEX = "sex";
    final public static String BIRTH = "birth";
    final public static String REGION = "region";
    final public static String ACCESS = "access";
    final public static String STATUS = "status";
    final public static String CATEGORY_NO = "category_no";
    final public static String FAVORITES = "favorites";

    // Service Name
    final public static String SERVICE_MEMO = "MEMO";
    final public static String SERVICE_CALENDER = "CALENDER";
    final public static String SERVICE_ROUTINE = "ROUTINE";

    // Content type (개인, 그룹)
    final public static String CONTENT_TYPE_USER = "U";
    final public static String CONTENT_TYPE_GROUP = "G";

    // Memo Form-Data 전달 받을 때 사용하는 값
    final public static String FILE = "file";
    final public static String JSON = "json";

    // 권한 값
    final public static String ACCESS_ALL = "CRUD";
    final public static String ACCESS_CREATE = "C";
    final public static String ACCESS_READ = "R";
    final public static String ACCESS_UPDATE = "U";
    final public static String ACCESS_DELETE = "D";
    final public static String ACCESS_CREATE_READ = "CR";
    final public static String ACCESS_READ_UPDATE = "RU";
    final public static String ACCESS_READ_UPDATE_DELETE = "RUD";

    // Status
    final public static String CONTENT_STATUS_DELETE = "-1";
    final public static String CONTENT_STATUS_NORMAL = "1";
    final public static String CONTENT_STATUS_LOCK = "2";

    // List Type
    final public static String CONTENT_LIST_OWN = "own";
    final public static String CONTENT_LIST_GROUP = "group";
    final public static String CONTENT_LIST_FOLLOW = "follow";


}