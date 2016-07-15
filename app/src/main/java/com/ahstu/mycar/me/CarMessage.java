package com.ahstu.mycar.me;

/**
 * Created by 徐伟 on 2016/5/18.
 */
public class CarMessage {
    private static int notificationId = 2;

    public int getNotificationId() {
        notificationId++;
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        CarMessage.notificationId = notificationId;
    }


}
