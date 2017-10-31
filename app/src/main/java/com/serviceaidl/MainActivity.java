package com.serviceaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.appsever.IAidlService;
import com.serviceaidl.service.HelloAidlService;
import com.serviceaidl.service.HelloBindService;
import com.serviceaidl.service.HelloFrontService;
import com.serviceaidl.service.HelloIntentService;
import com.serviceaidl.service.HelloStartService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    @BindView(R.id.btnStartService)
    Button mBtnStarService;
    @BindView(R.id.btnStopService)
    Button mBtnStopService;
    @BindView(R.id.btnBindService)
    Button mBtnBindService;
    @BindView(R.id.btnUnbindService)
    Button mBtnUnBindService;
    @BindView(R.id.btnShowNumber)
    Button mBtnShowRandomNum;
    @BindView(R.id.btnStartIntentService)
    Button mBtnStartIntentService;
    @BindView(R.id.btnStartFrontService)
    Button mBtnStartFrontService;
    @BindView(R.id.btnStopFrontService)
    Button mBtnStopFrontService;
    @BindView(R.id.btnStartAidlService)
    Button mBtnStartAidlService;

    private Intent mStartServiceIntent;
    private Intent mBindServiceIntent;
    private Intent mFrontServiceIntent;
    private Intent mAidlServiceInten;
    private ServiceConnection mServiceConnection;
    private HelloBindService mHelloBinderService;
    private boolean isBound;
    private ServiceConnection mAidlServiceConnection;
    private IAidlService mServerAidlService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        mStartServiceIntent = new Intent(MainActivity.this, HelloStartService.class);
        mBindServiceIntent = new Intent(MainActivity.this, HelloBindService.class);
        mFrontServiceIntent = new Intent(this, HelloFrontService.class);
        mAidlServiceInten = new Intent(this, HelloAidlService.class);
        //bindService通信使用
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //已经绑定到LocalService了，对IBinder进行类型转换（cast）并获得LocalService对象的实例
                HelloBindService.LocalBinder localBinder = (HelloBindService.LocalBinder) service;
                mHelloBinderService = localBinder.getHelloBoundService();
                isBound = true;
                Log.d(TAG, "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //系统在内存不足的时候可以优先杀死这个服务而已。 unbindService并不会触发
                isBound = false;
                Log.d(TAG, "onServiceDisconnected");
            }
        };

        mAidlServiceConnection = new ServiceConnection() {

            //重写onServiceConnected()方法和onServiceDisconnected()方法
            //在Activity与Service建立关联和解除关联的时候调用
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            //在Activity与Service建立关联时调用
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //使用AIDLService1.Stub.asInterface()方法将传入的IBinder对象传换成了mServerAidlService对象
                mServerAidlService = IAidlService.Stub.asInterface(service);

                try {
                    //通过该对象调用在MyAIDLService.aidl文件中定义的接口方法,从而实现跨进程通信
                    mServerAidlService.aidlService();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @OnClick({R.id.btnStartService, R.id.btnStopService,
            R.id.btnBindService, R.id.btnUnbindService,
            R.id.btnShowNumber, R.id.btnStartIntentService,
            R.id.btnStartFrontService, R.id.btnStopFrontService,
            R.id.btnStartAidlService})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.btnStartService:
                startService();
                break;
            case R.id.btnStopService:
                stopService();
                break;
            case R.id.btnBindService:
                bindService();
                break;
            case R.id.btnUnbindService:
                unBindService();
                break;
            case R.id.btnShowNumber:
                showRandomNumber();
                break;
            case R.id.btnStartIntentService:
                startIntentService();
                break;
            case R.id.btnStartFrontService:
                startFrontService();
                break;
            case R.id.btnStopFrontService:
                stopFrontService();
                break;
            case R.id.btnStartAidlService:
                startAidlService();
                break;
            default:
                break;
        }
    }

    private void startAidlService() {
        Log.d(TAG, "AIDL-点击了[绑定服务]按钮");
        bindService(mAidlServiceInten, mAidlServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopFrontService() {
        stopService(mFrontServiceIntent);
    }

    private void startFrontService() {

        startService(mFrontServiceIntent);
    }

    private void startIntentService() {
        //Android5.0之后必须使用显示intent来启动
        Intent intentOne = new Intent(this, HelloIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putString("taskName", "task1");
        intentOne.putExtras(bundle);
        startService(intentOne);

        Intent intentTwo = new Intent(this, HelloIntentService.class);
        Bundle bundle2 = new Bundle();
        bundle2.putString("taskName", "task2");
        intentTwo.putExtras(bundle2);
        startService(intentTwo);

        startService(intentOne);  //多次启动

    }

    private void showRandomNumber() {
        if (isBound) {
            //调用LocalService中的方法,如果该调用会导致某些操作的挂起，那么该调用应该放到单独的线程中进行
            //以免降低activity的性能
            int num = mHelloBinderService.getRandomNumber();
            Toast.makeText(MainActivity.this, "随机数 " + num, Toast.LENGTH_SHORT).show();
        }
    }

    private void unBindService() {
        //unbindService 不可在无binder之后调用，会报Service not registered crash
        if (isBound) {
            unbindService(mServiceConnection);
            isBound = false;
        }
        Log.d(TAG, "unBindService");
    }

    private void bindService() {
        //绑定到LocalService
        bindService(mBindServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "bindService");
    }

    private void stopService() {
        Log.d(TAG, "stopService");
        stopService(mStartServiceIntent);
    }

    private void startService() {
        Log.d(TAG, "startService");
        mStartServiceIntent.putExtra("startService", "startServiceTest");
        startService(mStartServiceIntent);
    }


}
