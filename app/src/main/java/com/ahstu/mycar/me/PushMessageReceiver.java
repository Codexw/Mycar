package com.ahstu.mycar.me;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.ahstu.mycar.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by Administrator on 2016/5/16.
 */
public class PushMessageReceiver extends BroadcastReceiver {

    private String s = null, message = null;
    private CarMessage carMessage = new CarMessage();

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

            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            Notification notification;

            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            //启动通知栏
            notification = new Notification.Builder(context).setContentTitle("MyCar").setContentText(s).setTicker(s).setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_launcher).build();
            manager.notify(carMessage.getNotificationId(), notification);

        }
    }
}
