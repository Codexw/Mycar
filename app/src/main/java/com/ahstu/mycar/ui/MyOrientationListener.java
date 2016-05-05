package com.ahstu.mycar.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by redowu on 2016/5/5.
 */
public class MyOrientationListener implements SensorEventListener {
    private SensorManager mSensorManager;
    private Context context;
    private Sensor mSensor;
    private float lastX;

    public MyOrientationListener(Context context) {
        this.context = context;
    }

    public void start() {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            //获得方向传感器
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }

        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void stop() {

        mSensorManager.unregisterListener(this);
    }

    //方向发生变化
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = event.values[SensorManager.DATA_X];

            if (Math.abs(x - lastX) > 1.0) {
                if (mOnOrientationListener != null) {
                    mOnOrientationListener.onOrientationChanged(lastX);
                }
            }
            lastX = x;
        }
    }

    private OnOrientationListener mOnOrientationListener;

    public void setmOnOrientationListener(OnOrientationListener mOnOrientationListener) {
        this.mOnOrientationListener = mOnOrientationListener;
    }

    //精度发生改变时
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public interface OnOrientationListener {
        void onOrientationChanged(float x);
    }


}
