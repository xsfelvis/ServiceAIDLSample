package com.serviceaidl.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appsever.IAidlService;

/**
 * Author: xushangfei
 * Time: created at 2017/10/31.
 * Description:
 */

public class HelloAidlService extends Service {
    public static final String TAG = "HelloAidlService";

    /**
     * 实例化AIDL的stub类(Binder子类)
     */

    IAidlService.Stub mBinder = new IAidlService.Stub() {
        @Override
        public void aidlService() throws RemoteException {
            Log.d(TAG, "AIDL-客户端通过AIDL与远程后台成功通信");
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
