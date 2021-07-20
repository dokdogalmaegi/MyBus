package com.example.open_bus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import android.widget.CompoundButton;
import android.widget.TextView;
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
    TextView noticeTitle;
    String result = "현재 버스가 운행중입니다.";

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
                            String arrmsg1 = jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg1").toString();
                            String arrmsg2 = jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg2").toString();
                            String stNm = jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("stNm").toString();
                            String arsId = jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arsId").toString();

                            System.out.println(arrmsg1);
                            System.out.println(arrmsg2);
                            System.out.println(stNm);
                            System.out.println(arsId);

                            intent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Notification.BigTextStyle test = new Notification.BigTextStyle();

                                test.bigText(arrmsg1 + "\n" + arrmsg2);

                                Notification.Builder builder = new Notification.Builder(getApplicationContext(), "busbus")
                                        .setSmallIcon(R.drawable.bus_eyes)
                                        .setStyle(test)
                                        .setContentTitle(stNm)
                                        .setContentText(arrmsg1 + "\n" + arrmsg2)
                                        .setTicker("한줄 출력")
                                        .setContentIntent(intent);

                                CharSequence name = getString(R.string.channel_name);
                                String description = getString(R.string.channel_description);
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel channel = new NotificationChannel("busbus", name, importance);
                                channel.setDescription(description);

                                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(channel);

                                if(arrmsg1.contains("5분") || arrmsg1.contains("6분") || arrmsg1.contains("곧") || arrmsg2.contains("5분") || arrmsg2.contains("6분") || arrmsg2.contains("곧"))
                                notificationManager.notify(0, builder.build());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    getBusThread.start();
                }
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

        noticeTitle = findViewById(R.id.noticeTitle);

        Thread startGetBus = new Thread(new Runnable() {
            @Override
            public void run() {
                busText = getBus();

                try {
                    jsonObj = XML.toJSONObject(busText);
                } catch (JSONException e) {
                    System.out.println("API 에러 입니다.");
                    e.printStackTrace();
                }

                try {
                    if(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arrmsg1").toString().equals("운행종료")) {
                        result = "현재 버스가 운행종료 되었습니다.";
                        System.out.println(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        startGetBus.start();
        try {
            startGetBus.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        noticeTitle.setText(result);

        ToggleButton toggleBtn = findViewById(R.id.toggleBtn);

        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleBtn.isChecked()) {
                    startTimer();
                    toggleBtn.setBackgroundResource(R.drawable.bus_eyes);
                } else {
                    stopTimer();
                    toggleBtn.setBackgroundResource(R.drawable.bus_eyes_close);
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