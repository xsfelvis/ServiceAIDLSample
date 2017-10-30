package com.serviceaidl.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Author: xushangfei
 * Time: created at 2017/10/24.
 * Description:
 */

public class HelloStartService extends Service {
    public static final String TAG = "HelloStartService";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    public String msgStr;


    @Override
    public void onCreate() {
        //这里配置一些信息
        //启动运行服务的线程。
        //请记住我们要创建一个单独的线程，因为服务通常运行于进程的主线程中，可我们不想阻塞主线程。
        //我们还要赋予它后台运行的优先级，以便计算密集的工作不会干扰我们的UI。
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        //获取handlerThread的loop队列并用于Handler
        mServiceLooper = handlerThread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        msgStr = intent.getStringExtra("startService");
        Log.d(TAG, "onStartCommand getExtraString = " + msgStr);
        //对于每一个启动请求，都发送一个消息来启动一个处理
        //同时传入启动ID，以便任务完成后我们知道该终止哪一个请求。
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = 1;
        mServiceHandler.sendMessage(message);
        //如果我们被杀死了，那从这里返回之后被重启
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //通常我们在这里执行一些耗时工作，如下载之类的
            try {
                Log.d(TAG, "模拟耗时操作!");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            //根据startId终止服务，这样我们就不会在处理其它工作的过程中再来终止服务
            //如果组件通过调用startService()（这会导致onStartCommand()的调用）启动了服务，那么服务将一直保持运行，直至自行用stopSelf()终止或由其它组件调用stopService()来终止它。
            //如果组件调用bindService()来创建服务（那onStartCommand()就不会被调用），则服务的生存期就与被绑定的组件一致。一旦所有客户端都对服务解除了绑定，系统就会销毁该服务。
            stopSelf(msg.arg1);
            Log.d(TAG, "HelloStartService 处理完数据后自动停止");
        }
    }
}
