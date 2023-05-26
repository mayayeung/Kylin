package com.martin.cmpt.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.core.utils.CheckUtils;
import com.martin.core.utils.TimeUtil;
import com.martin.core.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by DingJinZhu on 2023/5/25.
 * Description:
 */
public class VideoRecordActivity extends FragmentActivity implements View.OnClickListener, SurfaceHolder.Callback {
    private static String TAG = "MTK";
    int cameraCount = 0;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    private View btnSwithCamera, btnSelVideo;
    private ImageView btnCancel, btnFlash, btnRecord;
    private SurfaceView surfaceview;
    private Camera camera;
    private Chronometer chronometer;
    private TextView tvLimit;
    private long maxLength;//单位毫秒，此处取值300000即5分钟
    private SurfaceHolder surfaceHolder;
    public final static int PERMISSION_REQUESTCODE_CAMERA = 0x99;
    private static int lastPermissionsRequestCode;
    final String[] permissions_camera = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private MediaRecorder recorder;
    private boolean isRecording = false;
    private int screenHeight;//屏幕的高度
    private int screenWidth;//屏幕的宽度
    private int videoWidth = 0;// 视频分辨率宽度
    private int videoHeight = 0;// 视频分辨率高度
    public  Camera.Size pictureSize;//照片分辨率
    private Camera.Size previewSize;//预览分辨率
    private String outputFilePath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_video_record);
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        if(0 == maxLength){
            maxLength = 7200*1000;
        }
        initUI();
    }

    private void initUI() {
        cameraCount = Camera.getNumberOfCameras();
        btnSwithCamera = findViewById(R.id.btn_swith_camera);
        btnSwithCamera.setOnClickListener(this);
        if (cameraCount < 2) {
            btnSwithCamera.setVisibility(View.GONE);
        }
        btnSelVideo = findViewById(R.id.btn_sel_video);
        btnSelVideo.setOnClickListener(this);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnFlash = findViewById(R.id.btn_flash);
        btnFlash.setOnClickListener(this);
        btnRecord = findViewById(R.id.btn_record);
        btnRecord.setOnClickListener(this);
        surfaceview = findViewById(R.id.surfaceview);
        surfaceview.getHolder().addCallback(this);
        surfaceview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        surfaceview.getHolder().setKeepScreenOn(true);
        chronometer = findViewById(R.id.chronometer);
        tvLimit = findViewById(R.id.tv_limit);
        tvLimit.setText("点击开始录像，最长" + TimeUtil.getVideoLimitStr(maxLength/1000));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (!TextUtils.isEmpty(outputFilePath)) {
                    File outFile = new File(outputFilePath);
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                }
                finish();
                break;
            case R.id.btn_flash:
                try {
                    camera.lock();
                    Camera.Parameters parameters = camera.getParameters();
                    // 判断闪光灯当前状态來修改
                    if (Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        btnFlash.setImageResource(R.drawable.camera_icon_flash_on);
                    } else if (Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        btnFlash.setImageResource(R.drawable.camera_icon_flash_off);
                    } else if(Camera.Parameters.FLASH_MODE_ON.equals(parameters.getFlashMode())) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        btnFlash.setImageResource(R.drawable.camera_icon_flash_off);
                    }
                    camera.unlock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_swith_camera:
                if (isRecording) {
                    //正在录像，不允许切换
                    Toast.makeText(VideoRecordActivity.this, "视频录制中，请先停止录制。", Toast.LENGTH_LONG).show();
                } else {
                    switchCamera();
                }
                break;
            case R.id.btn_sel_video:
                //选择视频
                break;
            case R.id.btn_record:
                Toast.makeText(VideoRecordActivity.this, "视频录制。。。", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        checkPermissions(PERMISSION_REQUESTCODE_CAMERA, permissions_camera);
        surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseMediaRecorder();
        releaseCamera();
    }

    private void checkPermissions(final int requestCode, String[] permissions) {
        if (shouldCheckPermission()) {
            lastPermissionsRequestCode = requestCode;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, requestCode);
                    return;
                }
            }
            onRequestPermissionsSuccess(false, requestCode);
        } else {
            onRequestPermissionsSuccess(false, requestCode);
        }
    }

    private void onRequestPermissionsSuccess(boolean request, int requestCode) {
        if (requestCode == PERMISSION_REQUESTCODE_CAMERA) {
            initCameraRecorder(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
    }

    private void onRequestPermissionsFailure(int requestCode, int[] grantResults) {
        if (requestCode == PERMISSION_REQUESTCODE_CAMERA) {
            ToastUtils.showToastOnce("请去系统设置中手动开启camera及读写sd卡权限！");
        }
    }


    public boolean shouldCheckPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == lastPermissionsRequestCode) {
            for (int i = 0, len = grantResults.length; i < len; i++) {
                int result = grantResults[i];
                if (result != PackageManager.PERMISSION_GRANTED) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            onRequestPermissionsFailure(requestCode, null);
                            return;
                        }
                    }
                    onRequestPermissionsFailure(requestCode, grantResults);
                    return;
                }
            }
            onRequestPermissionsSuccess(true, requestCode);
        }
    }

    private void releaseMediaRecorder() {
        Log.d(TAG,"releaseMediaRecorder");
        if (recorder != null) {
            try {
                recorder.setPreviewDisplay(null);
            } catch (Exception e) {
                Log.e(TAG,"recorder.setPreviewDisplay:", e);
                e.printStackTrace();
            }

            try {
                if (isRecording) {
                    recorder.stop();
                }
            } catch (Exception e) {
                Log.e(TAG,"recorder.stop:", e);
                e.printStackTrace();
            }

            try {
                recorder.release();
                recorder = null;
            } catch (Exception e) {
                Log.e(TAG,"recorder.release():", e);
                e.printStackTrace();
            }
            if (camera != null) {
                camera.lock();
            }

        }
    }

    private void releaseCamera() {
        Log.d(TAG, "releaseCamera:");
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.lock();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCameraRecorder(int cameraNo) {
        Log.i(TAG, "begin init camera");
        Log.i(TAG, "height = " + screenHeight + "; width = " + screenWidth);
        try {
            isRecording = false;
            camera = Camera.open(cameraNo);//打开摄像头
            recorder = new MediaRecorder();
            recorder.reset();

            //camera = Camera.open();
            if (null == camera || null == recorder) {
                return;
            }
            camera.setDisplayOrientation(90);
            Camera.Parameters p = camera.getParameters();
            if (p.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            else if (p.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
//            p.setPreviewSize(640, 480);

            float percent = calcPreviewPercent();
            Log.i(TAG, "percent = " + percent);
            List<Camera.Size> supportedPreviewSizes = p.getSupportedPreviewSizes();
            //因为有些手机videoSizes不提供  比如华为mate7  那样的话就只能拿照相机预览数据了 当然预览数据尺寸不知道能不能当视频录制尺寸
            List<Camera.Size> videoSizes = p.getSupportedVideoSizes();
            previewSize = getPreviewMaxSize(supportedPreviewSizes, percent);

            if (videoSizes != null) {
                for (Camera.Size _s : videoSizes) {
                    float videoS = (float) _s.width / _s.height;
                    Log.i(TAG, "getVideoSize : w =" + _s.width + "; h = " + _s.height + "; ratio = " + videoS);
                    if (Math.abs(videoS - percent) < 0.1 && _s.width <= previewSize.width && _s.height <= previewSize.height) {
                        videoWidth = _s.width;
                        videoHeight = _s.height;
                        Log.i(TAG, "getVideoSize : find videoWidth = " + videoWidth + "; videoHeight = " + videoHeight);
                        continue;
                    }
                }
            }
            if (videoWidth == 0 && videoHeight == 0) {
                //如果没有拿到值  那就是当前的预览数据了
                videoWidth = previewSize.width;
                videoHeight = previewSize.height;
                Log.i(TAG, "videoWidth = 0 & videoHeight = 0, use preview size :" + videoWidth + " X " + videoHeight);
            }

            // 获取摄像头支持的各种分辨率
            List<Camera.Size> supportedPictureSizes = p.getSupportedPictureSizes();
            pictureSize = findSizeFromList(supportedPictureSizes, previewSize);
            if (pictureSize == null) {
                pictureSize = getPictureMaxSize(supportedPictureSizes, previewSize);
            }
            p.setPictureSize(pictureSize.width, pictureSize.height);
            // 设置预浏尺寸，注意要在摄像头支持的范围内选择
            p.setPreviewSize(previewSize.width, previewSize.height);
            p.setJpegQuality(70);
            //surfaceview.getHolder().setFixedSize(previewSize.width, previewSize.height);
            camera.setParameters(p);
            try {
                camera.setPreviewDisplay(surfaceview.getHolder());
            } catch (IOException e1) {
                Log.e(TAG, "camera.setPreviewDisplay:", e1);
                e1.printStackTrace();
            }
            camera.startPreview();
            camera.unlock();
            recorder.setCamera(camera);
            recorder.setPreviewDisplay(surfaceview.getHolder().getSurface());
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//百度云转码只支持H.263，使用H.264百度云转码不会把视频摆正
            // recorder.setMaxDuration(15000);
            recorder.setVideoSize(videoWidth, videoHeight);// 设置分辨率
            recorder.setVideoEncodingBitRate(2 * 1024 * 1024);// 设置帧频率，然后就清晰了
            int oritention = 90;
            if (cameraNo == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                oritention = 270;
            }
            recorder.setOrientationHint(oritention);

            File appDir = new File(Environment.getExternalStorageDirectory(), "cmpt_camera");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }

            outputFilePath = appDir + "/" + TimeUtil.getFormatTime(System.currentTimeMillis()) + ".mp4";
            recorder.setOutputFile(outputFilePath);
            if(maxLength > 0) {
                recorder.setMaxDuration((int) (maxLength-1000));//录制时间不能超过maxLength，故设置提前1秒
            }
            recorder.prepare();
        } catch (Exception e) {
            Toast.makeText(this, "开启摄像头失败,请检查是否打开相关权限", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "camera:", e);
            finish();
        }
    }

    private float calcPreviewPercent() {
        if (CheckUtils.checkIsPad(this)) {//平板
            Log.d(TAG, "is pad: true");
            return (float) screenWidth / screenHeight;
        }else {//手机
            Log.d(TAG, "is pad: false");
            return (float) screenHeight / screenWidth;
        }
    }

    // 获取预览的最大分辨率
    private Camera.Size getPreviewMaxSize(List<Camera.Size> l, float j) {
        int idx_best = 0;
        int best_width = 0;
        float best_diff = 100.0f;
        for (int i = 0; i < l.size(); i++) {
            int w = l.get(i).width;
            int h = l.get(i).height;
            Log.i(TAG, "getPreviewMaxSize : w =" + w + "; h = " + h);
            if (w * h < screenHeight * screenWidth)
                continue;
            float previewPercent = (float) w / h;
            Log.i(TAG, "getPreviewMaxSize : previewPercent =" + previewPercent);
            float diff = Math.abs(previewPercent - j);
            if (diff < best_diff) {
                idx_best = i;
                best_diff = diff;
                best_width = w;
            } else if (diff == best_diff && w > best_width) {
                idx_best = i;
                best_diff = diff;
                best_width = w;
            }
        }
        Log.i(TAG, "getPreviewMaxSize : best idx =" + idx_best);
        return l.get(idx_best);
    }

    private Camera.Size findSizeFromList(List<Camera.Size> supportedPictureSizes, Camera.Size size) {
        Camera.Size s = null;
        Log.i(TAG, "previewSize : w =" + size.width + "; h = " + size.height);
        if (supportedPictureSizes != null && !supportedPictureSizes.isEmpty()) {
            for (Camera.Size su : supportedPictureSizes) {
                Log.i(TAG, "findSizeFromList : w =" + su.width + "; h = " + su.height);
                if (size.width == su.width && size.height == su.height) {
                    s = su;
                    Log.i(TAG, "find size form list, w = " + su.width + "; h = " + su.height);
                    break;
                }
            }
        }
        return s;
    }

    // 根据摄像头的获取与屏幕分辨率最为接近的一个分辨率
    private Camera.Size getPictureMaxSize(List<Camera.Size> l, Camera.Size size) {
        Camera.Size s = null;
        for (int i = 0; i < l.size(); i++) {
            Log.i(TAG, "getPictureMaxSize : w =" + l.get(i).width + "; h = " + l.get(i).height);
            if (l.get(i).width >= size.width && l.get(i).height >= size.height
                    && l.get(i).height != l.get(i).width) {
                if (s == null) {
                    s = l.get(i);
                } else {
                    if (s.height * s.width > l.get(i).width * l.get(i).height) {
                        s = l.get(i);
                    }
                }
            }
        }
        return s;
    }

    private void switchCamera() {
        //切换前后摄像头
        try {
            //cameraCount = Camera.getNumberOfCameras();
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                if (cameraPosition == 1) {
                    //现在是后置，变更为前置
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                        releaseCamera();
                        releaseMediaRecorder();
                        initCameraRecorder(i);
                        cameraPosition = 0;
                        break;
                    }
                } else {
                    //现在是前置， 变更为后置
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                        releaseCamera();
                        releaseMediaRecorder();
                        initCameraRecorder(i);
                        cameraPosition = 1;
                        break;
                    }
                }

            }

        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

    }


}
