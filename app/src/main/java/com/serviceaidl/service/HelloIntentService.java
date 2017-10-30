package com.serviceaidl.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Author: xushangfei
 * Time: created at 2017/10/30.
 * Description:
 */

public class HelloIntentService extends IntentService {
    public static final String TAG = "HelloIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public HelloIntentService(String name) {
        super(name);
    }

    public HelloIntentService() {
        super("HelloIntentService");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    /*复写onStartCommand()方法*/
    //默认实现将请求的Intent添加到工作队列里
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }


    /**
     * IntentService从缺省的工作线程中调用本方法，并用启动服务的intent作为参数。
     * 本方法返回后，IntentService将适时终止这个服务。
     */

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //根据Intent的不同进行不同的事务处理
        String taskName = intent.getExtras().getString("taskName");
        switch (taskName) {
            case "task1":
                Log.d(TAG, "do task1");
                break;
            case "task2":
                Log.d(TAG, "do task2");
                break;
            default:
                break;
        }

    }
}
