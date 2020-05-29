package com.martin.cmpt.camera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.martin.core.utils.ToastUtils;

/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class MainActivity extends Activity {
    public final static int PERMISSION_REQUESTCODE_CAMERA = 0x99;
    private static int lastPermissionsRequestCode;
    private String[] permissions_camera = {Manifest.permission.CAMERA};
    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);
        frameLayout = findViewById(R.id.camera_preview);
        Button settings = findViewById(R.id.camera_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsFragment settingsFragment = new SettingsFragment();
                settingsFragment.setCamera(CameraPreview.getCameraInstance());
                getFragmentManager().beginTransaction()
                        .replace(R.id.camera_preview, settingsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        //check camere permissions
        checkPermissions(PERMISSION_REQUESTCODE_CAMERA, permissions_camera);

    }

    private void openCamera() {
        CameraPreview cameraView = new CameraPreview(this);
        frameLayout.addView(cameraView);
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
            openCamera();
        }
    }

    private void onRequestPermissionsFailure(int requestCode, int[] grantResults) {
        if (requestCode == PERMISSION_REQUESTCODE_CAMERA) {
            ToastUtils.showToastOnce("请去系统设置中手动开启camera及读写sd卡权限！");
        }
    }


}
