package com.martin.cmpt.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.martin.cmpt.camera.Utils.CameraUtils;
import com.martin.cmpt.camera.widget.AutoCenterHorizontalScrollView;
import com.martin.cmpt.camera.widget.HorizontalAdapter;
import com.martin.core.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class CameraActivity extends FragmentActivity implements View.OnClickListener {
    public final static int PERMISSION_REQUESTCODE_CAMERA = 0x99;
    private static int lastPermissionsRequestCode;
    private String[] permissions_camera = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private FrameLayout frameLayout;
    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        initView();
        initHorizontal();
        //check camere permissions
        checkPermissions(PERMISSION_REQUESTCODE_CAMERA, permissions_camera);
    }

    private void initView() {
        frameLayout = findViewById(R.id.camera_preview);
        Button takePhoto = findViewById(R.id.camera_takephoto);
        Button switchCamera = findViewById(R.id.camera_switch);
        takePhoto.setOnClickListener(this);
        switchCamera.setOnClickListener(this);
    }

    private void initHorizontal() {
        AutoCenterHorizontalScrollView autoCenterHorizontalScrollView;
        autoCenterHorizontalScrollView = findViewById(R.id.camera_mode_select);
        //测试用的随机字符串集合
        final List<String> names = new ArrayList<>();
        names.add("普通模式");
        names.add("拍题模式");
        //adapter去处理itemView
        HorizontalAdapter hadapter = new HorizontalAdapter(this, names);
        autoCenterHorizontalScrollView.setAdapter(hadapter);
        autoCenterHorizontalScrollView.setOnSelectChangeListener(new AutoCenterHorizontalScrollView.OnSelectChangeListener() {
            @Override
            public void onSelectChange(int position) {
                ToastUtils.showToastOnce(names.get(position));
            }
        });
        autoCenterHorizontalScrollView.setCurrentIndex(0);
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

    private boolean shouldCheckPermission() {
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

    private void onRequestPermissionsSuccess(boolean request, int requestCode) {
        if (requestCode == PERMISSION_REQUESTCODE_CAMERA) {
            initCamera();
        }
    }

    private void onRequestPermissionsFailure(int requestCode, int[] grantResults) {
        if (requestCode == PERMISSION_REQUESTCODE_CAMERA) {
            ToastUtils.showToastOnce("请去系统设置中手动开启camera及读写sd卡权限！");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == cameraPreview) {
            initCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview = null;
    }

    private void initCamera() {
        cameraPreview = new CameraPreview(this);
        frameLayout.addView(cameraPreview);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camera_takephoto) {
//            CameraUtils.takePicture(mediaPreview);
        } else if (v.getId() == R.id.camera_switch) {
            CameraUtils.switchCamera(this, cameraPreview);
        }
    }
}
