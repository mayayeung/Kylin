package com.martin.cmpt.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.martin.cmpt.camera.Utils.CameraUtils;
import com.martin.cmpt.camera.callback.NextStepListener;
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
    private final static int CAMERA_MODE_COMMON = 0; //普通模式
    private final static int CAMERA_MODE_EXERCISE = 1; //拍题模式
    private final static int CAMERA_STEP_PREVIEW = 0; //预览界面
    private final static int CAMERA_STEP_CLIP = 1; //裁剪界面
    private final static int CAMERA_STEP_CONFIRM = 2; //确认界面
    public final static int PERMISSION_REQUESTCODE_CAMERA = 0x99;
    private static int lastPermissionsRequestCode;
    private AutoCenterHorizontalScrollView autoCenterHorizontalScrollView;
    private String[] permissions_camera = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private FrameLayout frameLayout;
    private ImageView photoView;
    private ImageView left;
    private ImageView right;
    private ImageView next;
    private ImageView takePhoto;
    private CameraPreview cameraPreview;
    private int cameraMode;
    private int step;
    private boolean clipContent = true;//裁剪内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        initView();
        initHorizontal();
        refreshView(cameraMode, CAMERA_STEP_PREVIEW);
        //check camere permissions
        checkPermissions(PERMISSION_REQUESTCODE_CAMERA, permissions_camera);
    }

    private void initView() {
        frameLayout = findViewById(R.id.camera_preview);
        photoView = findViewById(R.id.camera_photo);
        takePhoto = findViewById(R.id.camera_takephoto);
        left = findViewById(R.id.camera_left);
        right = findViewById(R.id.camera_right);
        next = findViewById(R.id.camera_next);
        ImageView close = findViewById(R.id.camera_close);
        ImageView flash = findViewById(R.id.camera_flash);
        takePhoto.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        next.setOnClickListener(this);
        close.setOnClickListener(this);
        flash.setOnClickListener(this);
        step = CAMERA_STEP_PREVIEW;
    }

    private void initHorizontal() {
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
                cameraMode = position;
                refreshView(cameraMode, step);
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
            if (step == CAMERA_STEP_PREVIEW) {
                CameraUtils.takePicture(photoView, new NextStepListener() {
                    @Override
                    public void onNext() {
                        photoView.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.GONE);
                    }
                });
            }
            step = cameraMode == CAMERA_MODE_COMMON ? CAMERA_STEP_CONFIRM : CAMERA_STEP_CLIP;
            refreshView(cameraMode, step);
        } else if (v.getId() == R.id.camera_left) {
            if (step == CAMERA_STEP_CLIP) {//
                step = CAMERA_STEP_PREVIEW;
                refreshView(cameraMode, step);
            } else if (step == CAMERA_STEP_CONFIRM) {
                if (cameraMode == CAMERA_MODE_COMMON) {
                    step = CAMERA_STEP_PREVIEW;
                    refreshView(cameraMode, step);
                } else {
                    step = CAMERA_STEP_CLIP;
                    refreshView(cameraMode, step);
                    backToMark();
                }
            }
        } else if (v.getId() == R.id.camera_right) {
            if (step == CAMERA_STEP_PREVIEW) {
                CameraUtils.switchCamera(this, cameraPreview);
            }
            if (step == CAMERA_STEP_CLIP) {
                clipContent = !clipContent;
                switchClipRange(clipContent);
            }
            if (step == CAMERA_STEP_CONFIRM) {
                ToastUtils.showToastOnce("开始标注!" + CameraUtils.getOutputMediaFileUri());
            }
        } else if (v.getId() == R.id.camera_next) {
            if (step == CAMERA_STEP_CLIP) {
                step = CAMERA_STEP_CONFIRM;
                refreshView(cameraMode, step);
                doClipRange(clipContent);
            }
        } else if (v.getId() == R.id.camera_close) {
            finish();
        } else if (v.getId() == R.id.camera_flash) {
            ToastUtils.showToastOnce("闪光灯啊");
        }
    }

    private void doClipRange(boolean clipContent) {

    }

    /**
     * 拍题模式，裁剪范围
     * @param markContent
     */
    private void switchClipRange(boolean markContent) {
        right.setImageResource(clipContent ? R.drawable.camera_icon_mark_zoom_out : R.drawable.camera_icon_mark_zoom_in);
        ToastUtils.showToastOnce("裁剪：" + (markContent ? "内容" : "边框"));
    }

    /**
     * 拍题模式确认，返回到拍题模式的标记
     */
    private void backToMark() {
        ToastUtils.showToastOnce("返回标记页面");
    }

    private void refreshView(int cameraMode, int step) {
        if (step != CAMERA_STEP_PREVIEW) {
            autoCenterHorizontalScrollView.setVisibility(View.GONE);
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            if (cameraMode == CAMERA_MODE_COMMON && step == CAMERA_STEP_CONFIRM) {//普通模式，确认界面
                left.setImageResource(R.drawable.camera_icon_back_to_preview);
                right.setImageResource(R.drawable.camera_icon_mark_begin);
                next.setImageResource(R.drawable.camera_icon_mark_confirm);
                takePhoto.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }
            if (cameraMode == CAMERA_MODE_EXERCISE && step == CAMERA_STEP_CONFIRM) {//拍题模式，确认界面
                left.setImageResource(R.drawable.camera_icon_back_to_mark);
                right.setImageResource(R.drawable.camera_icon_mark_begin);
                next.setImageResource(R.drawable.camera_icon_mark_confirm);
                takePhoto.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);

            }
            if (cameraMode == CAMERA_MODE_EXERCISE && step == CAMERA_STEP_CLIP) {//拍题模式，裁剪界面
                left.setImageResource(R.drawable.camera_icon_back_to_preview);
                right.setImageResource(clipContent ? R.drawable.camera_icon_mark_zoom_out : R.drawable.camera_icon_mark_zoom_in);
                next.setImageResource(R.drawable.camera_icon_mark_next);
                takePhoto.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        } else { //预览模式
            autoCenterHorizontalScrollView.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            left.setVisibility(View.GONE);
            takePhoto.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
            right.setVisibility(cameraMode == CAMERA_MODE_COMMON ? View.VISIBLE : View.GONE);
            right.setImageResource(R.drawable.camera_icon_switch_camera);
        }

    }
}
