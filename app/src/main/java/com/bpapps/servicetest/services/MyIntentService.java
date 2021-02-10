package com.bpapps.servicetest.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyIntentService extends IntentService {
    private static final String TAG = "TAG.MyIntentService";

    public static final String EXTRA_DATA = "EXTRA_DATA";

    private static MyIntentService instance;
    private static boolean isRunning = false;

    public static boolean isRunning() {
        return isRunning;
    }

    private static void setIsRunning(boolean isRunning) {
        MyIntentService.isRunning = isRunning;
    }

    public static void stopService() {
        Log.d(TAG, "stopService: service is stopping");
        setIsRunning(false);
        instance.stopSelf();
    }

    public MyIntentService() {
        super("MyIntentService");

        instance = this;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        setIsRunning(true);

        final String dataString = intent.getStringExtra(EXTRA_DATA);

        for (int i = 0; i < 10 && isRunning(); i++) {
            if (dataString != null) {
                Log.d(TAG, "MyIntentService: service is running...msg = " + dataString);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            } else {
                Log.d(TAG, "MyIntentService: service is running...msg = null");
            }
        }

        setIsRunning(false);
    }
}
