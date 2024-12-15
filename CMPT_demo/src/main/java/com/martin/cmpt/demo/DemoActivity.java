package com.martin.cmpt.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;

import com.martin.cmpt.demo.video.VideoPlayerActivity;
import com.martin.core.jzvd.JZDataSource;
import com.martin.core.jzvd.Jzvd;
import com.martin.core.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {
    AGVideo myPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        findViewById(R.id.video_test).setOnClickListener(this);

    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, DemoActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        String[] urls = {
                "http://vjs.zencdn.net/v/oceans.mp4",
                "https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4",
                "http://www.w3school.com.cn/example/html5/mov_bbb.mp4",
                "https://www.w3schools.com/html/movie.mp4",
                "https://media.w3.org/2010/05/sintel/trailer.mp4"
        };

        ArrayList<String> uriList = new ArrayList<>();
        uriList.add(urls[1]);
        uriList.add(urls[0]);
        uriList.add(urls[2]);
        uriList.add(urls[3]);
        uriList.add(urls[4]);
        Intent it = new Intent(this, VideoPlayerActivity.class);
        it.putStringArrayListExtra("videoUrlList", uriList);
        startActivity(it);

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


