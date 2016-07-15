package com.ahstu.mycar.me;

/**
 * Created by 徐伟 on 2016/6/8.
 * 功能：共享位置
 */
public class ShareLocationMessage {
    private static boolean shareconnect = false;
    private static boolean firstconnect = false;
    private static String username;
    private static String other_username;
    private static int objection;//接收方或是发送方，发送方0，接收方1

    public static int getObjection() {
        return objection;
    }

    public static void setObjection(int objection) {
        ShareLocationMessage.objection = objection;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        ShareLocationMessage.username = username;
    }

    public static String getOther_username() {
        return other_username;
    }

    public static void setOther_username(String other_username) {
        ShareLocationMessage.other_username = other_username;
    }

    public static boolean isFirstconnect() {
        return firstconnect;
    }

    public static void setFirstconnect(boolean firstconnect) {
        ShareLocationMessage.firstconnect = firstconnect;
    }


    public static boolean isShareconnect() {
        return shareconnect;
    }

    public static void setShareconnect(boolean shareconnect) {
        ShareLocationMessage.shareconnect = shareconnect;
    }
}
