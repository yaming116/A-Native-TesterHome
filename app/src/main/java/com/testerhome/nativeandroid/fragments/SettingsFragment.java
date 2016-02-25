package com.testerhome.nativeandroid.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.widgets.ThemeUtils;

/**
 * Created by vclub on 15/10/23.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String KEY_PREF_THEME = "pref_theme_key";
    public static final String KEY_PREF_COMMENT_WITH_SNACK = "pref_comment_with_snack_key";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingsFragment.KEY_PREF_THEME, "0").equals("0")) {
            view.setBackgroundColor(getResources().getColor(R.color.white));
        } else {

            view.setBackgroundColor(getResources().getColor(R.color.bg_main_dark));
        }


        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        findPreference(KEY_PREF_THEME).setSummary(getKeyPrefThemeString(KEY_PREF_THEME));

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(KEY_PREF_THEME)) {
            Preference themePref = findPreference(key);
            // Set summary to be the user-description for the selected value

            themePref.setSummary(getKeyPrefThemeString(key));
            if (getKeyPrefThemeString(key).equals("Light")){
                getActivity().setTheme(R.style.theme_light);
            } else {
                getActivity().setTheme(R.style.theme_dark);
            }
            ThemeUtils.recreateActivity(getActivity());
        }
    }

    private String getKeyPrefThemeString(String key) {
        try {
            Integer index = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(key, "0"));
            return getResources().getStringArray(R.array.pref_theme_list_titles)[index];
        } catch (Exception ex) {
            return "Light";
        }
    }

}
