package com.martin.cmpt.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.martin.cmpt.camera.Utils.CameraUtils;
import com.martin.core.utils.ToastUtils;


/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class MainActivity extends Activity implements View.OnClickListener {
    public final static int PERMISSION_REQUESTCODE_CAMERA = 0x99;
    private static int lastPermissionsRequestCode;
    private String[] permissions_camera = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private FrameLayout frameLayout;
    private SettingsFragment settingsFragment;
    private CameraPreview cameraPreview;
    private ImageView mediaPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);
        frameLayout = findViewById(R.id.camera_preview);
        mediaPreview = findViewById(R.id.media_preview);
        Button settings = findViewById(R.id.camera_settings);
        Button takePhoto = findViewById(R.id.camera_takephoto);
        Button switchCamera = findViewById(R.id.camera_switch);
        takePhoto.setOnClickListener(this);
        settings.setOnClickListener(this);
        switchCamera.setOnClickListener(this);
        mediaPreview.setOnClickListener(this);

        settingsFragment = new SettingsFragment();
        //check camere permissions
        checkPermissions(PERMISSION_REQUESTCODE_CAMERA, permissions_camera);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camera_settings) {
            getFragmentManager().beginTransaction().replace(R.id.camera_preview, settingsFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (v.getId() == R.id.camera_takephoto) {
            CameraUtils.takePicture(mediaPreview);
        } else if (v.getId() == R.id.camera_switch) {
            CameraUtils.switchCamera(this, cameraPreview);
        } else if (v.getId() == R.id.media_preview) {
            Intent intent = new Intent(this, ShowPhotoActivity.class);
            intent.setDataAndType(CameraUtils.getOutputMediaFileUri(), CameraUtils.getOutputMediaFileType());
            startActivity(intent);
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


}
