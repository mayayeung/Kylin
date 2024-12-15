package com.martin.cmpt.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;

import com.martin.core.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
//        findViewById(R.id.test_https).setOnClickListener(this);
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, DemoActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        ToastUtils.showToastOnce("haha");

/*        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest("https://www.bing.com");
            }
        }).start();*/

    }

    private void sendRequest(String urlString){
        BufferedReader bfReader = null;
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(urlString);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            InputStream in= urlConn.getInputStream();
            bfReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response=new StringBuilder();
            String line;
            while ((line = bfReader.readLine()) != null) {
                response.append(line);
            }
            Log.e("demo", response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bfReader != null) {
                try {
                    bfReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != urlConn) {
                urlConn.disconnect();
            }

        }

    }
}


