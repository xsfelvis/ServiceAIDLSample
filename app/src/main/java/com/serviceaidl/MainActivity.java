package com.serviceaidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.serviceaidl.service.HelloBindService;
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

    private Intent mStartServiceIntent;
    private Intent mBindServiceIntent;
    private ServiceConnection mServiceConnection;
    private HelloBindService mHelloBinderService;
    private boolean isBound;

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
    }

    @OnClick({R.id.btnStartService, R.id.btnStopService,
            R.id.btnBindService, R.id.btnUnbindService, R.id.btnShowNumber})
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
            default:
                break;
        }
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
