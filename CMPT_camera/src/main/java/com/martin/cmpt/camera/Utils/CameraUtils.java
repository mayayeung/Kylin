package com.martin.cmpt.camera.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageView;

import com.martin.cmpt.camera.CameraPreview;
import com.martin.core.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by DingJinZhu on 2020/6/3.
 * Description:
 */
public class CameraUtils {
    private static final String TAG = CameraUtils.class.getSimpleName();
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String KEY_PREF_PREV_SIZE = "preview_size";
    public static final String KEY_PREF_PIC_SIZE = "picture_size";
    public static final String KEY_PREF_VIDEO_SIZE = "video_size";
    public static final String KEY_PREF_FLASH_MODE = "flash_mode";
    public static final String KEY_PREF_FOCUS_MODE = "focus_mode";
    public static final String KEY_PREF_WHITE_BALANCE = "white_balance";
    public static final String KEY_PREF_SCENE_MODE = "scene_mode";
    public static final String KEY_PREF_GPS_DATA = "gps_data";
    public static final String KEY_PREF_EXPOS_COMP = "exposure_compensation";
    public static final String KEY_PREF_JPEG_QUALITY = "jpeg_quality";
    public static final String KEY_PREF_CAMERA_INDEX = "camera_index";
    private static Uri outputMediaFileUri;
    private static String outputMediaFileType;
    private static Camera camera;
    private static Camera.Parameters parameters;
    private static int frontCameraIndex;
    private static int backCameraIndex;
    private static int cameraIndex;
    private static OrientationEventListener listener;

    public static Camera getCameraInstance(Context context) {
        if (null == camera) {
            try {
                getCameraInfo(context);
                camera = Camera.open(cameraIndex);
                parameters = camera.getParameters();
                listener=new OrientationEventListener(context) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        if (orientation == ORIENTATION_UNKNOWN) return;
                        Camera.CameraInfo info = new Camera.CameraInfo();
                        android.hardware.Camera.getCameraInfo(cameraIndex, info);
                        orientation = (orientation + 45) / 90 * 90;
                        int rotation = 0;
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            rotation = (info.orientation - orientation + 360) % 360;
                        } else {  // back-facing camera
                            rotation = (info.orientation + orientation) % 360;
                        }
                        if (null != camera) {
                            Camera.Parameters parameters = camera.getParameters();
                            parameters.setRotation(rotation);
                            camera.setParameters(parameters);
                        }
                    }
                };
                listener.enable();
            } catch (Exception e) {
                ToastUtils.showToastOnce("相机打开失败！");
            }
        }
        return camera;
    }

    private static void getCameraInfo(Context context) {
        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        int count = Camera.getNumberOfCameras();
        for (int i = 0; i < count; i++) {
            Camera.getCameraInfo(i, camInfo);
            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontCameraIndex = i;
            }
            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backCameraIndex = i;
            }
        }
        cameraIndex = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_PREF_CAMERA_INDEX, -1);
        if (-1 == cameraIndex) {
            cameraIndex = backCameraIndex;
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_PREF_CAMERA_INDEX, cameraIndex).commit();
        }
    }

    public static void switchCamera(Context context, CameraPreview preview) {
        cameraIndex = (cameraIndex == frontCameraIndex ? backCameraIndex : frontCameraIndex);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_PREF_CAMERA_INDEX, cameraIndex).commit();

        if (null != camera) {
//            preview.getHolder().removeCallback(preview);
//            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        getCameraInstance(context);
        setDefault(context);
        try {
            camera.setPreviewDisplay(preview.getHolder());
            camera.startPreview();
            setRotation(context);
            camera.autoFocus(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setRotation(Context context) {
        int rotation = getDisplayOrientation(context);
        camera.setDisplayOrientation(rotation);
        camera.setParameters(parameters);
    }

    private static int getDisplayOrientation(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraIndex, camInfo);

        int result;
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (camInfo.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            result = (camInfo.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static void release() {
        listener.disable();
        listener = null;
        camera = null;
    }

    public static Camera.Parameters getParameters() {
        return null != camera ? camera.getParameters() : null;
    }

    public static void setDefault(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valPreviewSize = sharedPrefs.getString(KEY_PREF_PREV_SIZE, null);
        if (valPreviewSize == null) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(KEY_PREF_PREV_SIZE, getDefaultPreviewSize());
            editor.putString(KEY_PREF_PIC_SIZE, getDefaultPictureSize());
            editor.putString(KEY_PREF_VIDEO_SIZE, getDefaultVideoSize());
            editor.putString(KEY_PREF_FOCUS_MODE, getDefaultFocusMode());
            editor.apply();
        }
        init(sharedPrefs);
    }

    private static void init(SharedPreferences sharedPref) {
        setPreviewSize(sharedPref.getString(KEY_PREF_PREV_SIZE, ""));
        setPictureSize(sharedPref.getString(KEY_PREF_PIC_SIZE, ""));
        setFlashMode(sharedPref.getString(KEY_PREF_FLASH_MODE, ""));
        setFocusMode(sharedPref.getString(KEY_PREF_FOCUS_MODE, ""));
        setWhiteBalance(sharedPref.getString(KEY_PREF_WHITE_BALANCE, ""));
        setSceneMode(sharedPref.getString(KEY_PREF_SCENE_MODE, ""));
        setExposComp(sharedPref.getString(KEY_PREF_EXPOS_COMP, ""));
        setJpegQuality(sharedPref.getString(KEY_PREF_JPEG_QUALITY, ""));
        setGpsData(sharedPref.getBoolean(KEY_PREF_GPS_DATA, false));
        camera.setParameters(parameters);
        camera.autoFocus(null);
    }


    public static void takePicture(final ImageView mediaPreview) {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    ToastUtils.showToastOnce("Error creating media file, check storage permissions");
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    mediaPreview.setImageURI(outputMediaFileUri);
                    camera.startPreview();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
            }
        });
    }


    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "camera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                ToastUtils.showToastOnce("failed to create media file directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            outputMediaFileType = "image/*";
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            outputMediaFileType = "video/*";
        } else {
            return null;
        }
        outputMediaFileUri = Uri.fromFile(mediaFile);
        return mediaFile;
    }

    public static Uri getOutputMediaFileUri() {
        return outputMediaFileUri;
    }

    public static String getOutputMediaFileType() {
        return outputMediaFileType;
    }

    private static String getDefaultPreviewSize() {
        Camera.Size previewSize = parameters.getPreviewSize();
        return previewSize.width + "x" + previewSize.height;
    }

    private static String getDefaultPictureSize() {
        Camera.Size pictureSize = parameters.getPictureSize();
        return pictureSize.width + "x" + pictureSize.height;
    }

    private static String getDefaultVideoSize() {
        Camera.Size VideoSize = parameters.getPreferredPreviewSizeForVideo();
        return VideoSize.width + "x" + VideoSize.height;
    }

    private static String getDefaultFocusMode() {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.contains("continuous-picture")) {
            return "continuous-picture";
        }
        return "continuous-video";
    }

    public static void setPreviewSize(String value) {
        String[] split = value.split("x");
        parameters.setPreviewSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public static void setPictureSize(String value) {
        String[] split = value.split("x");
        parameters.setPictureSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public static void setFocusMode(String value) {
        parameters.setFocusMode(value);
    }

    public static void setFlashMode(String value) {
        parameters.setFlashMode(value);
    }

    public static void setWhiteBalance(String value) {
        parameters.setWhiteBalance(value);
    }

    public static void setSceneMode(String value) {
        parameters.setSceneMode(value);
    }

    public static void setExposComp(String value) {
        if (!TextUtils.isEmpty(value)) {
            parameters.setExposureCompensation(Integer.parseInt(value));
        }
    }

    public static void setJpegQuality(String value) {
        if (!TextUtils.isEmpty(value)) {
            parameters.setJpegQuality(Integer.parseInt(value));
        }
    }

    public static void setGpsData(Boolean value) {
        if (value.equals(false)) {
            parameters.removeGpsData();
        }
    }

    public static void setParameters() {
        if (null != camera) {
            camera.setParameters(parameters);
        }
    }
}
