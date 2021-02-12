package com.bpapps.servicetest.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class MyBoundedService extends Service {
    private static final String TAG = "TAG.MyBoundedService";
    private IBinder mBinder = new MyBinder();
    private IOnProgressChangeListener mCallback = null;
    private MutableLiveData<Integer> mProgress = new MutableLiveData<>();
    //    private int mProgress;
    private int mCurrentValue, mMaxValue;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        mMaxValue = 100;
        mCurrentValue = 0;
        mProgress.setValue(0);
//        mProgress = 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: MyBoundedService bind service ");
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        unRegisterOnProgressChangedListener();
        stopSelf();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: MyBoundedService  bind service unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: bounded service finished");
    }

    public void startLongRunningTask() {
        mCurrentValue = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mCurrentValue < mMaxValue) {
                    mCurrentValue += 1;
//                    mProgress = (int) (100 * ((double) mCurrentValue / (double) mMaxValue));
//                    mProgress.setValue((int) (100 * ((double) mCurrentValue / (double) mMaxValue)));

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setValue((int) (100 * ((double) mCurrentValue / (double) mMaxValue)));

//                            if (mCallback != null) {
//                                mCallback.onProgressChanged(mProgress);
//                            }
                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public int getMaxValue() {
        return mMaxValue;
    }

//    public int getProgress() {
//        return mProgress;
//    }

    public MutableLiveData<Integer> getProgress() {
        return mProgress;
    }

    public void registerProgressChangedListener(@NonNull IOnProgressChangeListener callback) {
        mCallback = callback;
    }

    public void unRegisterOnProgressChangedListener() {
        mCallback = null;
    }


    public interface IOnProgressChangeListener {
        void onProgressChanged(int newProgress);
    }

    public class MyBinder extends Binder {
        public MyBoundedService getService() {
            return MyBoundedService.this;
        }
    }
}
