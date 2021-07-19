package com.example.open_bus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import retrofit2.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    NotificationManager notifiaManager;
    PendingIntent intent;
    long mNow;
    Date mDate;
    SimpleDateFormat timeFormat = new SimpleDateFormat("ss");

    String busText = "ㅋㅋ 이러면 버스 정보 올줄 알았냐 ㅋㅋ";
    JSONObject jsonObj;
    Timer timer;

    public void startTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(getTime().equals("00")) {
                    Thread getBusThread = new Thread(() -> {
                        busText = getBus();
                        try {
                            jsonObj = XML.toJSONObject(busText);
                        } catch (JSONException e) {
                            System.out.println("API 요청 에러입니다.");
                            e.printStackTrace();
                        }

                        try {
                            System.out.println(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg1"));
                            System.out.println(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg2"));
                            System.out.println(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("stNm"));
                            System.out.println(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arsId"));

                            intent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Notification.BigTextStyle test = new Notification.BigTextStyle();

                                test.bigText(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg1").toString() + "\n" + jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg2").toString());

                                Notification.Builder builder = new Notification.Builder(getApplicationContext(), "busbus")
                                        .setSmallIcon(R.drawable.ic_stat_name)
                                        .setStyle(test)
                                        .setContentTitle(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("stNm").toString())
                                        .setContentText(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg1").toString() + "\n" + jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg2").toString())
                                        .setTicker("한줄 출력")
                                        .setContentIntent(intent);

                                CharSequence name = getString(R.string.channel_name);
                                String description = getString(R.string.channel_description);
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel channel = new NotificationChannel("busbus", name, importance);
                                channel.setDescription(description);

                                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(channel);

                                notificationManager.notify(0, builder.build());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                    runOnUiThread(() -> {
//                        Toast.makeText(MainActivity.this, busText, Toast.LENGTH_LONG).show();
//                    });
                    });
                    getBusThread.start();
                } else System.out.println(getTime());
            }
        };

        timer = new Timer();

        timer.schedule(timerTask, 0, 1000);
    }

    public void stopTimer() {
        timer.cancel();
    }

    public void startMainService() {
        Intent intent = new Intent(this, BusForegroundService.class);

        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startMainService();

        Button TestBtn = findViewById(R.id.APIBtn);
        ToggleButton toggleBtn = findViewById(R.id.toggleBtn);

        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleBtn.isChecked()) {
                    startTimer();

                } else {
                    stopTimer();
                }
            }
        });
    }

    public String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return timeFormat.format(mDate);
    }

    public String getBus() {
        String result = "";
        Call<String> get507 = BusApiImpl.getApiService().get507();

        try {
            result = get507.execute().body().toString();
        } catch (IOException e) {
            result = "님아 버그남";
            e.printStackTrace();
        }
        return result;
    }

}