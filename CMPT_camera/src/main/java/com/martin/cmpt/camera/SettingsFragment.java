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
    private static int cameraIndex;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

    }

    private void loadSupportedPreviewSize() {
        cameraSizeListToListPreference(parameters.getSupportedPreviewSizes(), CameraUtils.KEY_PREF_PREV_SIZE + cameraIndex);
    }

    private void loadSupportedPictureSize() {
        cameraSizeListToListPreference(parameters.getSupportedPictureSizes(), CameraUtils.KEY_PREF_PIC_SIZE + cameraIndex);
    }

    private void loadSupportedVideoeSize() {
        cameraSizeListToListPreference(parameters.getSupportedVideoSizes(), CameraUtils.KEY_PREF_VIDEO_SIZE + cameraIndex);
    }

    private void loadSupportedFlashMode() {
        stringListToListPreference(parameters.getSupportedFlashModes(), CameraUtils.KEY_PREF_FLASH_MODE + cameraIndex);
    }

    private void loadSupportedFocusMode() {
        stringListToListPreference(parameters.getSupportedFocusModes(), CameraUtils.KEY_PREF_FOCUS_MODE + cameraIndex);
    }

    private void loadSupportedWhiteBalance() {
        stringListToListPreference(parameters.getSupportedWhiteBalance(), CameraUtils.KEY_PREF_WHITE_BALANCE + cameraIndex);
    }

    private void loadSupportedSceneMode() {
        stringListToListPreference(parameters.getSupportedSceneModes(), CameraUtils.KEY_PREF_SCENE_MODE + cameraIndex);
    }

    private void loadSupportedExposeCompensation() {
        int minExposComp = parameters.getMinExposureCompensation();
        int maxExposComp = parameters.getMaxExposureCompensation();
        List<String> exposComp = new ArrayList<>();
        for (int value = minExposComp; value <= maxExposComp; value++) {
            exposComp.add(Integer.toString(value));
        }
        stringListToListPreference(exposComp, CameraUtils.KEY_PREF_EXPOS_COMP + cameraIndex);
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
        if (null != listPref) {
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(CameraUtils.KEY_PREF_PREV_SIZE + cameraIndex)) {
            CameraUtils.setPreviewSize(sharedPreferences.getString(key, ""));
        } else if (key.equals(CameraUtils.KEY_PREF_PIC_SIZE + cameraIndex)) {
            CameraUtils.setPictureSize(sharedPreferences.getString(key, ""));
        }else if (key.equals(CameraUtils.KEY_PREF_FOCUS_MODE + cameraIndex)) {
            CameraUtils.setFocusMode(sharedPreferences.getString(key, ""));
        }else if (key.equals(CameraUtils.KEY_PREF_FLASH_MODE + cameraIndex)) {
            CameraUtils.setFlashMode(sharedPreferences.getString(key, ""));
        }else if (key.equals(CameraUtils.KEY_PREF_WHITE_BALANCE + cameraIndex)) {
            CameraUtils.setWhiteBalance(sharedPreferences.getString(key, ""));
        }else if (key.equals(CameraUtils.KEY_PREF_SCENE_MODE + cameraIndex)) {
            CameraUtils.setSceneMode(sharedPreferences.getString(key, ""));
        }else if (key.equals(CameraUtils.KEY_PREF_EXPOS_COMP + cameraIndex)) {
            CameraUtils.setExposComp(sharedPreferences.getString(key, ""));
        }else if (key.equals(CameraUtils.KEY_PREF_JPEG_QUALITY + cameraIndex)) {
            CameraUtils.setJpegQuality(sharedPreferences.getString(key, ""));
        }else if (key.equals(CameraUtils.KEY_PREF_GPS_DATA + cameraIndex)) {
            CameraUtils.setGpsData(sharedPreferences.getBoolean(key, false));
        }
        camera.stopPreview();
        CameraUtils.setParameters();
        camera.startPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void refresh() {
        camera = CameraUtils.getCameraInstance(getActivity());
        cameraIndex = CameraUtils.getCameraIndex();
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
}
