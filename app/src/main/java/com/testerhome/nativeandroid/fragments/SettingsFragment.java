package com.testerhome.nativeandroid.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.testerhome.nativeandroid.R;

/**
 * Created by vclub on 15/10/23.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String KEY_PREF_THEME = "pref_theme_key";
    public static final String KEY_PREF_COMMENT_WITH_SNACK = "pref_comment_with_snack_key";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themePref = sharedPref.getString(KEY_PREF_THEME, "");
        findPreference(KEY_PREF_THEME).setSummary(getKeyPrefThemeString(themePref));

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
        }
    }

    private String getKeyPrefThemeString(String value){
        try {
            return getResources().getStringArray(R.array.pref_theme_list_titles)[Integer.parseInt(value)];
        }catch (Exception ex){
            return value;
        }
    }

}
