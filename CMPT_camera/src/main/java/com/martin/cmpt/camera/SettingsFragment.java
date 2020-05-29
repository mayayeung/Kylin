package com.martin.cmpt.camera;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.martin.core.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
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
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri outputMediaFileUri;
    private String outputMediaFileType;

    private Camera camera;
    private Camera.Parameters parameters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        setDefault(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        init(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        if (null != parameters) {
            loadSupportedPreviewSize();
            loadSupportedPictureSize();
            loadSupportedVideoeSize();
            loadSupportedFlashMode();
            loadSupportedFocusMode();
            loadSupportedWhiteBalance();
            loadSupportedSceneMode();
            loadSupportedExposeCompensation();
        }

    }

    public void setCamera(Camera camera) {
        if (null != camera) {
            this.camera = camera;
            this.parameters = camera.getParameters();
        }
    }

    public void setDefault(SharedPreferences sharedPrefs) {
        String valPreviewSize = sharedPrefs.getString(KEY_PREF_PREV_SIZE, null);
        if (valPreviewSize == null) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(KEY_PREF_PREV_SIZE, getDefaultPreviewSize());
            editor.putString(KEY_PREF_PIC_SIZE, getDefaultPictureSize());
            editor.putString(KEY_PREF_VIDEO_SIZE, getDefaultVideoSize());
            editor.putString(KEY_PREF_FOCUS_MODE, getDefaultFocusMode());
            editor.apply();
        }
    }

    public void takePicture() {
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
                    camera.startPreview();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
            }
        });
    }

    private File getOutputMediaFile(int type) {
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

    public Uri getOutputMediaFileUri() {
        return outputMediaFileUri;
    }

    public String getOutputMediaFileType() {
        return outputMediaFileType;
    }

    private void loadSupportedPreviewSize() {
        cameraSizeListToListPreference(parameters.getSupportedPreviewSizes(), KEY_PREF_PREV_SIZE);
    }

    private void loadSupportedPictureSize() {
        cameraSizeListToListPreference(parameters.getSupportedPictureSizes(), KEY_PREF_PIC_SIZE);
    }

    private void loadSupportedVideoeSize() {
        cameraSizeListToListPreference(parameters.getSupportedVideoSizes(), KEY_PREF_VIDEO_SIZE);
    }

    private void loadSupportedFlashMode() {
        stringListToListPreference(parameters.getSupportedFlashModes(), KEY_PREF_FLASH_MODE);
    }

    private void loadSupportedFocusMode() {
        stringListToListPreference(parameters.getSupportedFocusModes(), KEY_PREF_FOCUS_MODE);
    }

    private void loadSupportedWhiteBalance() {
        stringListToListPreference(parameters.getSupportedWhiteBalance(), KEY_PREF_WHITE_BALANCE);
    }

    private void loadSupportedSceneMode() {
        stringListToListPreference(parameters.getSupportedSceneModes(), KEY_PREF_SCENE_MODE);
    }

    private void loadSupportedExposeCompensation() {
        int minExposComp = parameters.getMinExposureCompensation();
        int maxExposComp = parameters.getMaxExposureCompensation();
        List<String> exposComp = new ArrayList<>();
        for (int value = minExposComp; value <= maxExposComp; value++) {
            exposComp.add(Integer.toString(value));
        }
        stringListToListPreference(exposComp, KEY_PREF_EXPOS_COMP);
    }

    private void cameraSizeListToListPreference(List<Camera.Size> list, String key) {
        List<String> stringList = new ArrayList<>();
        for (Camera.Size size : list) {
            String stringSize = size.width + "x" + size.height;
            stringList.add(stringSize);
        }
        stringListToListPreference(stringList, key);
    }

    private void stringListToListPreference(List<String> list, String key) {
        ListPreference listPref = (ListPreference) getPreferenceScreen().findPreference(key);
        if (null == list) {
            CharSequence[] sequences = {"无"};
            listPref.setEntries(sequences);
            listPref.setEntryValues(sequences);
        } else {
            CharSequence[] charSeq = list.toArray(new CharSequence[list.size()]);
            listPref.setEntries(charSeq);
            listPref.setEntryValues(charSeq);
        }
    }

    private String getDefaultPreviewSize() {
        Camera.Size previewSize = parameters.getPreviewSize();
        return previewSize.width + "x" + previewSize.height;
    }

    private String getDefaultPictureSize() {
        Camera.Size pictureSize = parameters.getPictureSize();
        return pictureSize.width + "x" + pictureSize.height;
    }

    private String getDefaultVideoSize() {
        Camera.Size VideoSize = parameters.getPreferredPreviewSizeForVideo();
        return VideoSize.width + "x" + VideoSize.height;
    }

    private String getDefaultFocusMode() {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.contains("continuous-picture")) {
            return "continuous-picture";
        }
        return "continuous-video";
    }

    public void init(SharedPreferences sharedPref) {
        setPreviewSize(sharedPref.getString(KEY_PREF_PREV_SIZE, ""));
        setPictureSize(sharedPref.getString(KEY_PREF_PIC_SIZE, ""));
        setFlashMode(sharedPref.getString(KEY_PREF_FLASH_MODE, ""));
        setFocusMode(sharedPref.getString(KEY_PREF_FOCUS_MODE, ""));
        setWhiteBalance(sharedPref.getString(KEY_PREF_WHITE_BALANCE, ""));
        setSceneMode(sharedPref.getString(KEY_PREF_SCENE_MODE, ""));
        setExposComp(sharedPref.getString(KEY_PREF_EXPOS_COMP, ""));
        setJpegQuality(sharedPref.getString(KEY_PREF_JPEG_QUALITY, ""));
        setGpsData(sharedPref.getBoolean(KEY_PREF_GPS_DATA, false));
        camera.stopPreview();
        camera.setParameters(parameters);
        camera.startPreview();
    }

    private void setPreviewSize(String value) {
        String[] split = value.split("x");
        parameters.setPreviewSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private void setPictureSize(String value) {
        String[] split = value.split("x");
        parameters.setPictureSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private void setFocusMode(String value) {
        parameters.setFocusMode(value);
    }

    private void setFlashMode(String value) {
        parameters.setFlashMode(value);
    }

    private void setWhiteBalance(String value) {
        parameters.setWhiteBalance(value);
    }

    private void setSceneMode(String value) {
        parameters.setSceneMode(value);
    }

    private void setExposComp(String value) {
        parameters.setExposureCompensation(Integer.parseInt(value));
    }

    private void setJpegQuality(String value) {
        parameters.setJpegQuality(Integer.parseInt(value));
    }

    private void setGpsData(Boolean value) {
        if (value.equals(false)) {
            parameters.removeGpsData();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_PREF_PREV_SIZE:
                setPreviewSize(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_PIC_SIZE:
                setPictureSize(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_FOCUS_MODE:
                setFocusMode(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_FLASH_MODE:
                setFlashMode(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_WHITE_BALANCE:
                setWhiteBalance(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_SCENE_MODE:
                setSceneMode(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_EXPOS_COMP:
                setExposComp(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_JPEG_QUALITY:
                setJpegQuality(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_GPS_DATA:
                setGpsData(sharedPreferences.getBoolean(key, false));
                break;
        }
        camera.stopPreview();
        camera.setParameters(parameters);
        camera.startPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
