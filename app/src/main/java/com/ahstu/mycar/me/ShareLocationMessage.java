package com.ahstu.mycar.me;

/**
 * Created by Administrator on 2016/6/8.
 */
public class ShareLocationMessage {
    private static boolean shareconnect=false;
    private static boolean firstconnect=false;
    private static String username;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        ShareLocationMessage.username = username;
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
