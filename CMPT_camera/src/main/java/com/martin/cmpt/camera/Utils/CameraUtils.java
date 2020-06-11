package com.martin.cmpt.camera.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
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
import android.widget.TextView;

import com.martin.cmpt.camera.CameraPreview;
import com.martin.core.utils.ScreenUtils;
import com.martin.core.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DingJinZhu on 2020/6/3.
 * Description:
 */
public class CameraUtils {
    private static final String TAG = CameraUtils.class.getSimpleName();
    private static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String KEY_PREF_PREV_SIZE = "preview_size_";
    public static final String KEY_PREF_PIC_SIZE = "picture_size_";
    public static final String KEY_PREF_VIDEO_SIZE = "video_size_";
    public static final String KEY_PREF_FLASH_MODE = "flash_mode_";
    public static final String KEY_PREF_FOCUS_MODE = "focus_mode_";
    public static final String KEY_PREF_WHITE_BALANCE = "white_balance_";
    public static final String KEY_PREF_SCENE_MODE = "scene_mode_";
    public static final String KEY_PREF_GPS_DATA = "gps_data_";
    public static final String KEY_PREF_EXPOS_COMP = "exposure_compensation_";
    public static final String KEY_PREF_JPEG_QUALITY = "jpeg_quality_";
    public static final String KEY_PREF_CAMERA_INDEX = "camera_index_";
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
                listener = new OrientationEventListener(context) {
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

    public static boolean isBackCamera() {
        return cameraIndex == backCameraIndex;
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

    public static int getCameraIndex() {
        return cameraIndex;
    }

    public static void setDefault(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valPreviewSize = sharedPrefs.getString(KEY_PREF_PREV_SIZE + cameraIndex, null);
        if (valPreviewSize == null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point screenResolution = new Point();
            display.getSize(screenResolution);//得到屏幕的尺寸，单位是像素

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(KEY_PREF_PREV_SIZE + cameraIndex, getDefaultPreviewSize(screenResolution));
            editor.putString(KEY_PREF_PIC_SIZE + cameraIndex, getDefaultPictureSize(screenResolution));
            editor.putString(KEY_PREF_VIDEO_SIZE + cameraIndex, getDefaultVideoSize());
            editor.putString(KEY_PREF_FOCUS_MODE + cameraIndex, getDefaultFocusMode());
            editor.apply();
        }
        init(sharedPrefs, cameraIndex);
    }

    private static void init(SharedPreferences sharedPref, int cameraIndex) {
        setPreviewSize(sharedPref.getString(KEY_PREF_PREV_SIZE + cameraIndex, ""));
        setPictureSize(sharedPref.getString(KEY_PREF_PIC_SIZE + cameraIndex, ""));
        setFlashMode(sharedPref.getString(KEY_PREF_FLASH_MODE + cameraIndex, ""));
        setFocusMode(sharedPref.getString(KEY_PREF_FOCUS_MODE + cameraIndex, ""));
        setWhiteBalance(sharedPref.getString(KEY_PREF_WHITE_BALANCE + cameraIndex, ""));
        setSceneMode(sharedPref.getString(KEY_PREF_SCENE_MODE + cameraIndex, ""));
        setExposComp(sharedPref.getString(KEY_PREF_EXPOS_COMP + cameraIndex, ""));
        setJpegQuality(sharedPref.getString(KEY_PREF_JPEG_QUALITY + cameraIndex, ""));
        setGpsData(sharedPref.getBoolean(KEY_PREF_GPS_DATA + cameraIndex, false));
        camera.setParameters(parameters);
    }

    public static void setCameraSound(final boolean isSound, final Context context) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, isSound);
                    }
                }
        ).start();
    }

    /**
     * find best previewSize value,on the basis of camera supported previewSize and screen size
     *
     * @param parameters
     * @param screenResolution
     * @return
     */
    public static Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {

        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no preview size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        if (Log.isLoggable(TAG, Log.INFO)) {//检查是否可以输出日志
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                previewSizesString.append(supportedPreviewSize.width).append('x')
                        .append(supportedPreviewSize.height).append(' ');
            }
            Log.i(TAG, "Supported preview sizes: " + previewSizesString);
        }

        double screenAspectRatio;
        if (screenResolution.x > screenResolution.y) {
            screenAspectRatio = screenResolution.x / (double) screenResolution.y;//屏幕尺寸比例
        } else {
            screenAspectRatio = screenResolution.y / (double) screenResolution.x;//屏幕尺寸比例
        }

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {//delete if less than minimum size
                it.remove();
                continue;
            }

            //camera preview width > height
            boolean isCandidatePortrait = realWidth < realHeight;//width less than height
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;//ratio for camera
            double distortion = Math.abs(aspectRatio - screenAspectRatio);//returan absolute value
            if (distortion > MAX_ASPECT_DISTORTION) {//delete if distoraion greater than 0.15
                it.remove();
                continue;
            }
            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {//serceen size equal to camera supportedPreviewSize
                Point exactPoint = new Point(realWidth, realHeight);
                Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }
        }

        if (!supportedPreviewSizes.isEmpty()) {//default return first supportedPreviewSize,mean largest
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            Log.i(TAG, "Using largest suitable preview size: " + largestSize);
            return largestSize;
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
        return defaultSize;
    }

    /**
     * find best pictureSize value,on the basis of camera supported pictureSize and screen size
     *
     * @param parameters
     * @param screenResolution
     * @return
     */
    public static Point findBestPictureSizeValue(Camera.Parameters parameters, Point screenResolution) {

        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPictureSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPictureSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no preview size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        if (Log.isLoggable(TAG, Log.INFO)) {//检查是否可以输出日志
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                previewSizesString.append(supportedPreviewSize.width).append('x')
                        .append(supportedPreviewSize.height).append(' ');
            }
            Log.i(TAG, "Supported picture sizes: " + previewSizesString);
        }

        double screenAspectRatio;
        if (screenResolution.x > screenResolution.y) {
            screenAspectRatio = screenResolution.x / (double) screenResolution.y;//屏幕尺寸比例
        } else {
            screenAspectRatio = screenResolution.y / (double) screenResolution.x;//屏幕尺寸比例
        }

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {//delete if less than minimum size
                it.remove();
                continue;
            }

            //camera preview width > height
            boolean isCandidatePortrait = realWidth < realHeight;//width less than height
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;//ratio for camera
            double distortion = Math.abs(aspectRatio - screenAspectRatio);//returan absolute value
            if (distortion > MAX_ASPECT_DISTORTION) {//delete if distoraion greater than 0.15
                it.remove();
                continue;
            }

            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {//serceen size equal to camera supportedPreviewSize
                Point exactPoint = new Point(realWidth, realHeight);
                Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }
        }

        if (!supportedPreviewSizes.isEmpty()) {//default return first supportedPreviewSize,mean largest
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            Log.i(TAG, "Using largest suitable preview size: " + largestSize);
            return largestSize;
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPictureSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
        return defaultSize;
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

                    mediaPreview.setScaleType(ImageView.ScaleType.FIT_XY);
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

    private static String getDefaultPreviewSize(Point screenResolution) {
        Point previewSizeOnScreen = findBestPreviewSizeValue(parameters, screenResolution);
        return previewSizeOnScreen.x + "x" + previewSizeOnScreen.y;
    }

    private static String getDefaultPictureSize(Point screenResolution) {
        Point pictureSizeOnScreen  = findBestPictureSizeValue(parameters, screenResolution);
        return pictureSizeOnScreen .x + "x" + pictureSizeOnScreen .y;
    }

    private static String getDefaultVideoSize() {
        Camera.Size VideoSize = parameters.getPreferredPreviewSizeForVideo();
        return VideoSize.width + "x" + VideoSize.height;
    }

    private static String getDefaultFocusMode() {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.contains("continuous-picture")) {
            return "continuous-picture";
        } else if (supportedFocusModes.size() > 0) {
            return supportedFocusModes.get(0);
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
        if (!TextUtils.isEmpty(value)) {
            parameters.setFlashMode(value);
        }
    }

    public static void setWhiteBalance(String value) {
        if (!TextUtils.isEmpty(value)) {
            parameters.setWhiteBalance(value);
        }
    }

    public static void setSceneMode(String value) {
        if (!TextUtils.isEmpty(value)) {
            parameters.setSceneMode(value);
        }
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
