package com.example.open_bus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BusForegroundService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("Test 입니다.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Test 입니다.ㄴㅇㅇㅇ");
        return super.onStartCommand(intent, flags, startId);
    }
}
