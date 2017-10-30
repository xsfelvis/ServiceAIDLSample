package com.serviceaidl.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Random;

/**
 * Author: xushangfei
 * Time: created at 2017/10/27.
 * Description:
 */

public class HelloBindService extends Service {
    /**
     * 给客户端的Binder
     */
    public IBinder mBinder = new LocalBinder();
    /**
     * 生成随机数
     */
    private final Random mGenerator = new Random();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * 用于客户端的Binder类
     * 因为本服务测试在客户端相同的进程中，因此无需IPC处理
     */

    public class LocalBinder extends Binder {
        public HelloBindService getHelloBoundService() {
            return HelloBindService.this;
        }
    }

    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
}
