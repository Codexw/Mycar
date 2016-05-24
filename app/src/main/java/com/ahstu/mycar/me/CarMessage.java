package com.ahstu.mycar.me;

/**
 * Created by Administrator on 2016/5/18.
 */
public class CarMessage {
    private static int notificationId = 1;

    public int getNotificationId() {
        notificationId++;
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        CarMessage.notificationId = notificationId;
    }


}
