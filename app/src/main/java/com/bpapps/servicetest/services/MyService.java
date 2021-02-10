package com.bpapps.servicetest.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends Service {
    private static final String TAG = "TAG.MyService";

    private static int counter = 0;

    public MyService() {
        counter++;
    }

    public static final String EXTRA_DATA = "EXTRA_DATA";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(TAG, "onStartCommand: service id = " + counter);
        final String dataString = intent.getStringExtra(EXTRA_DATA);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dataString != null) {
                    for (int i = 0; i < dataString.length(); i++) {
                        Log.d(TAG, "onStartCommand : run: data[" + i + "] = " + dataString.charAt(i));
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d(TAG, "run: dataString is null");
                }
            }
        }).start();

//        return START_REDELIVER_INTENT;
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: destroying service");
    }
}
