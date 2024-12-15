package com.martin.cmpt.demo.video;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.martin.cmpt.demo.AGVideo;
import com.martin.cmpt.demo.MediaExo;
import com.martin.cmpt.demo.R;
import com.martin.cmpt.demo.view.SectorProgressView;
import com.martin.cmpt.demo.view.VideoSpeedPopup;
import com.martin.core.jzvd.JZDataSource;
import com.martin.core.jzvd.Jzvd;
import com.martin.core.jzvd.JzvdStd;
import com.martin.core.utils.ToastUtils;

import java.util.List;


public class VideoPlayerActivity extends FragmentActivity implements AGVideo.JzVideoListener, VideoSpeedPopup.SpeedChangeListener
        {
    String urlContent;
    String mtitle;
    AGVideo mAgVideo;
    private JZDataSource mJzDataSource;
    VideoPlayerActivity self;
    ImageView imageView;
    SectorProgressView downloadProgressView;
    LinearLayout saveProgressLayout;
    TextView saveProgressTv;
    ImageView saveCancelBtn;
    String curDownloadPath;
    //倍数弹窗
    private VideoSpeedPopup videoSpeedPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer_activity);
        self = this;
        initUI();
    }

    private void initUI() {
        List<String> uriList = this.getIntent().getStringArrayListExtra("videoUrlList");
        mtitle = this.getIntent().getStringExtra("title");
        mAgVideo = findViewById(R.id.videoplayer);
        imageView = findViewById(R.id.imgView);
        downloadProgressView = findViewById(R.id.progress_download);
        saveProgressLayout = findViewById(R.id.layout_save_progress);
        saveProgressTv = findViewById(R.id.tv_save_progress);
        saveCancelBtn = findViewById(R.id.btn_save_cancel);
//        mJCVideoPlayer.setExpandBtnClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onMoreClick(message);
//            }
//        });
        mAgVideo.setOnLongClickListener(v -> {
            ToastUtils.showToastOnce("长按视频todo 。。。。。。");
            return true;
        });

        if (uriList != null && uriList.size() > 0 && !TextUtils.isEmpty(uriList.get(0))) {
            urlContent = uriList.get(0);
            mAgVideo.setJzVideoListener(this);
            mJzDataSource = new JZDataSource(urlContent, "");
            mAgVideo.setUp(mJzDataSource, JzvdStd.SCREEN_NORMAL, MediaExo.class);
            mAgVideo.startVideo();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        Jzvd.goOnPlayOnResume();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.goOnPlayOnPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void finish() {
        super.finish();
    }


    private void downloadIMProgress(long currentSize, long totalSize) {
        if (isFinishing()) {
            return;
        }
        if (currentSize <= 0) {
            downloadProgressView.setProgress(0);
        } else {
            downloadProgressView.setProgress((int) (currentSize * 100 / totalSize));
        }
    }

    /**
     * 关闭倍速播放弹窗和选集弹窗
     */
    private void dismissSpeedPopAndEpisodePop() {
        if (videoSpeedPopup != null) {
            videoSpeedPopup.dismiss();
        }
    }

    @Override
    public void nextClick() {

    }

    @Override
    public void backClick() {
        if (mAgVideo.screen == mAgVideo.SCREEN_FULLSCREEN) {
            dismissSpeedPopAndEpisodePop();
            AGVideo.backPress();
        } else {
            finish();
        }
    }

    @Override
    public void throwingScreenClick() {

    }

    @Override
    public void selectPartsClick() {

    }

    @Override
    public void speedClick() {
        if (videoSpeedPopup == null) {
            videoSpeedPopup = new VideoSpeedPopup(this);
            videoSpeedPopup.setSpeedChangeListener(this);
        }
        videoSpeedPopup.showAtLocation(getWindow().getDecorView(), Gravity.RIGHT, 0, 0);
    }

    @Override
    public void speedChange(float speed) {
        changeSpeed(speed);
    }

    /**
     * 改变播放倍速
     *
     * @param speed
     */
    private void changeSpeed(float speed) {
        Object[] object = {speed};
        mAgVideo.mediaInterface.setSpeed(speed);
//        mJzDataSource.objects[0] = object;
        Toast.makeText(this, "正在以" + speed + "X倍速播放", Toast.LENGTH_SHORT).show();
        mAgVideo.speedChange(speed);
    }


    /**
     * 竖屏并退出全屏
     */
    private void changeScrenNormal() {
        if (mAgVideo != null && mAgVideo.screen == Jzvd.SCREEN_FULLSCREEN) {
            mAgVideo.autoQuitFullscreen();
        }
    }

    /**
     * 横屏
     */
    private void changeScreenFullLandscape(float x) {
        //从竖屏状态进入横屏
        if (mAgVideo != null && mAgVideo.screen != Jzvd.SCREEN_FULLSCREEN) {
            if ((System.currentTimeMillis() - Jzvd.lastAutoFullscreenTime) > 2000) {
                mAgVideo.autoFullscreen(x);
                Jzvd.lastAutoFullscreenTime = System.currentTimeMillis();
            }
        }
    }

}
