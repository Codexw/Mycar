package com.ahstu.mycar.me;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ahstu.mycar.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by 徐伟 on 2016/5/16.
 * 功能：推送消息
 */
public class PushMessageReceiver extends BroadcastReceiver {

    private String s = null, message = null;
    private CarMessage carMessage = new CarMessage();
    private String friend_name, mobile_id;
    private ShareLocationMessage shareLocationMessage = new ShareLocationMessage();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            String str = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            //获取json数据
            try {
                JSONObject jsonObject = new JSONObject(str);
                s = jsonObject.getString("alert");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (s.contains("请求位置共享")) {
                int index = s.lastIndexOf("请求位置共享");
                friend_name = s.substring(0, index);
                mobile_id = s.substring(index + 6, s.length());
                Intent friendShare = new Intent(context, FriendNotificationService.class);
                friendShare.putExtra("FRIEND_NAME", friend_name);
                friendShare.putExtra("MOBILE_ID", mobile_id);
                context.startService(friendShare);
            } else {
                NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                Notification notification;

//            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                //启动通知栏
                notification = new Notification.Builder(context).setContentTitle("MyCar").setContentText(s).setTicker(s).setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.notification_head).build();
                manager.notify(carMessage.getNotificationId(), notification);
                if (s.contains("接受了位置共享")) {
                    shareLocationMessage.setShareconnect(true);
                    shareLocationMessage.setFirstconnect(true);
                    shareLocationMessage.setObjection(0);
                }
            }

        }
    }
}
