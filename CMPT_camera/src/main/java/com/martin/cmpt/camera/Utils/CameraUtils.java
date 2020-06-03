package com.martin.cmpt.camera.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

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
    private static Uri outputMediaFileUri;
    private static String outputMediaFileType;
    private static Camera camera;
    private static Camera.Parameters parameters;

    public static Camera getCameraInstance() {
        if (null == camera) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                ToastUtils.showToastOnce("相机打开失败！");
            }
        }
        return camera;
    }

    public static void release() {
        camera = null;
    }

    public static Camera.Parameters getParameters() {
        return null != camera ? camera.getParameters() : null;
    }

    public static void setDefault(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        CameraUtils.camera = getCameraInstance();
        CameraUtils.parameters = camera.getParameters();

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
        parameters.setExposureCompensation(Integer.parseInt(value));
    }

    public static void setJpegQuality(String value) {
        parameters.setJpegQuality(Integer.parseInt(value));
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
