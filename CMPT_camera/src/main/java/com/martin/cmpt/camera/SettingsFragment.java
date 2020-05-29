package com.martin.cmpt.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class SettingsFragment extends PreferenceFragment {
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

    private Camera camera;
    private Camera.Parameters parameters;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

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
}
