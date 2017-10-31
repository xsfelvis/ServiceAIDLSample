package com.serviceaidl.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.serviceaidl.MainActivity;
import com.serviceaidl.R;

/**
 * Author: xushangfei
 * Time: created at 2017/10/31.
 * Description:
 */

public class HelloFrontService extends Service {
    public static final String TAG = "HelloFrontService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public HelloFrontService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*//添加下列代码将后台Service变成前台Service
        //构建"点击通知后打开MainActivity"的Intent对象
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //新建Builer对象
        Notification.Builder builer = new Notification.Builder(this);
        //设置通知的标题
        builer.setContentTitle("这是前台服务通知的标题~");
        //设置通知的内容
        builer.setContentText("这是前台服务通知的内容~");
        //设置通知的图标
        builer.setSmallIcon(R.mipmap.ic_launcher);
        //设置点击通知后的操作
        builer.setContentIntent(pendingIntent);
        //将Builder对象转变成普通的notification
        Notification notification = builer.getNotification();
        //让Service变成前台Service,并在系统的状态栏显示出来
        startForeground(1, notification);*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(this);
        Intent frontServiceIntent = new Intent(this, MainActivity.class);
        PendingIntent frontServicePeningIntent = PendingIntent.getActivity(this, 0, frontServiceIntent, 0);
        builder.setContentIntent(frontServicePeningIntent)
                .setContentTitle("下拉列表中的Title")
                .setContentText("要显示的内容")
                .setSmallIcon(R.mipmap.ic_front_small)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_front_big))
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.getNotification();
        notification.defaults = Notification.DEFAULT_SOUND;
        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopForeground(true);// 停止前台服务
        super.onDestroy();
    }
}
