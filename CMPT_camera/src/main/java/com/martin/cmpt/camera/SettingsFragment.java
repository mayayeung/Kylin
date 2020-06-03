package com.martin.cmpt.camera;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.martin.cmpt.camera.Utils.CameraUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static Camera camera;
    private static Camera.Parameters parameters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        camera = CameraUtils.getCameraInstance();
        parameters = CameraUtils.getParameters();
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

    private void loadSupportedPreviewSize() {
        cameraSizeListToListPreference(parameters.getSupportedPreviewSizes(), CameraUtils.KEY_PREF_PREV_SIZE);
    }

    private void loadSupportedPictureSize() {
        cameraSizeListToListPreference(parameters.getSupportedPictureSizes(), CameraUtils.KEY_PREF_PIC_SIZE);
    }

    private void loadSupportedVideoeSize() {
        cameraSizeListToListPreference(parameters.getSupportedVideoSizes(), CameraUtils.KEY_PREF_VIDEO_SIZE);
    }

    private void loadSupportedFlashMode() {
        stringListToListPreference(parameters.getSupportedFlashModes(), CameraUtils.KEY_PREF_FLASH_MODE);
    }

    private void loadSupportedFocusMode() {
        stringListToListPreference(parameters.getSupportedFocusModes(), CameraUtils.KEY_PREF_FOCUS_MODE);
    }

    private void loadSupportedWhiteBalance() {
        stringListToListPreference(parameters.getSupportedWhiteBalance(), CameraUtils.KEY_PREF_WHITE_BALANCE);
    }

    private void loadSupportedSceneMode() {
        stringListToListPreference(parameters.getSupportedSceneModes(), CameraUtils.KEY_PREF_SCENE_MODE);
    }

    private void loadSupportedExposeCompensation() {
        int minExposComp = parameters.getMinExposureCompensation();
        int maxExposComp = parameters.getMaxExposureCompensation();
        List<String> exposComp = new ArrayList<>();
        for (int value = minExposComp; value <= maxExposComp; value++) {
            exposComp.add(Integer.toString(value));
        }
        stringListToListPreference(exposComp, CameraUtils.KEY_PREF_EXPOS_COMP);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case CameraUtils.KEY_PREF_PREV_SIZE:
                CameraUtils.setPreviewSize(sharedPreferences.getString(key, ""));
                break;
            case CameraUtils.KEY_PREF_PIC_SIZE:
                CameraUtils.setPictureSize(sharedPreferences.getString(key, ""));
                break;
            case CameraUtils.KEY_PREF_FOCUS_MODE:
                CameraUtils.setFocusMode(sharedPreferences.getString(key, ""));
                break;
            case CameraUtils.KEY_PREF_FLASH_MODE:
                CameraUtils.setFlashMode(sharedPreferences.getString(key, ""));
                break;
            case CameraUtils.KEY_PREF_WHITE_BALANCE:
                CameraUtils.setWhiteBalance(sharedPreferences.getString(key, ""));
                break;
            case CameraUtils.KEY_PREF_SCENE_MODE:
                CameraUtils.setSceneMode(sharedPreferences.getString(key, ""));
                break;
            case CameraUtils.KEY_PREF_EXPOS_COMP:
                CameraUtils.setExposComp(sharedPreferences.getString(key, ""));
                break;
            case CameraUtils.KEY_PREF_JPEG_QUALITY:
                CameraUtils.setJpegQuality(sharedPreferences.getString(key, ""));
                break;
            case CameraUtils.KEY_PREF_GPS_DATA:
                CameraUtils.setGpsData(sharedPreferences.getBoolean(key, false));
                break;
        }
        camera.stopPreview();
        CameraUtils.setParameters();
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
