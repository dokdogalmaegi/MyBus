package com.example.open_bus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import android.widget.Toast;
import retrofit2.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    String busText = "ㅋㅋ 이러면 버스 정보 올줄 알았냐 ㅋㅋ";
    JSONObject jsonObj;
    Timer timer;

    boolean checkToggle = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button TestBtn = findViewById(R.id.APIBtn);


        TestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread getBusThread = new Thread(() -> {
                    busText = getBus();
                    try {
                        jsonObj = XML.toJSONObject(busText);
                    } catch (JSONException e) {
                        System.out.println("Test입니다.");
                        e.printStackTrace();
                    }

                    try {
                        System.out.println(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("stNm"));
                        System.out.println(jsonObj.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList").getJSONObject(5).get("arsId"));

                        System.out.println(getTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    runOnUiThread(() -> {
//                        Toast.makeText(MainActivity.this, busText, Toast.LENGTH_LONG).show();
//                    });
                });
                getBusThread.start();
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