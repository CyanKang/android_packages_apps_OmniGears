/*
 *  Copyright (C) 2013 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.omnirom.omnigears.interfacesettings;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.WindowManagerGlobal;

import android.net.ConnectivityManager;

import com.android.internal.util.slim.DeviceUtils;
import com.android.internal.util.nameless.NamelessUtils;
import org.omnirom.omnigears.preference.SystemCheckBoxPreference;

public class MenusSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
    private static final String TAG = "MenusSettings";
    private ContentResolver resolver;

    private Context mContext;
    private static final String POWER_MENU_SCREENRECORD = "screenrecord_in_power_menu";
    private static final String POWER_MENU_MOBILE_DATA = "mobile_data_in_power_menu";
    private static final String POWER_MENU_PROFILES = "power_menu_profiles";
    private static final String POWER_MENU_ONTHEGO_ENABLED = "power_menu_onthego_enabled";

    private SystemCheckBoxPreference mScreenrecordPowerMenu;
    private SystemCheckBoxPreference mMobileDataPowerMenu;
    private SystemCheckBoxPreference mOnTheGoPowerMenu;
    private ListPreference mProfilesPowerMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.menus_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        resolver = getActivity().getContentResolver();
        mContext = getActivity().getApplicationContext();

        mMobileDataPowerMenu = (SystemCheckBoxPreference) prefSet.findPreference(POWER_MENU_MOBILE_DATA);
        if (!DeviceUtils.deviceSupportsMobileData(mContext)) {
            prefSet.removePreference(mMobileDataPowerMenu);
        }

        mProfilesPowerMenu = (ListPreference) prefSet.findPreference(POWER_MENU_PROFILES);
        mProfilesPowerMenu.setOnPreferenceChangeListener(this);
        int mProfileShow = Settings.System.getInt(resolver,
                Settings.System.PROFILES_IN_POWER_MENU, 1);
        mProfilesPowerMenu.setValue(String.valueOf(mProfileShow));
        mProfilesPowerMenu.setSummary(mProfilesPowerMenu.getEntries()[mProfileShow]);

        // Only enable if System Profiles are also enabled
        boolean enabled = Settings.System.getInt(resolver,
                Settings.System.SYSTEM_PROFILES_ENABLED, 1) == 1;
        mProfilesPowerMenu.setEnabled(enabled);

        mScreenrecordPowerMenu = (SystemCheckBoxPreference) prefSet.findPreference(POWER_MENU_SCREENRECORD);
        if (!mContext.getResources().getBoolean(com.android.internal.R.bool.config_enableScreenrecordChord)) {
            prefSet.removePreference(mScreenrecordPowerMenu);
        }

        mOnTheGoPowerMenu = (SystemCheckBoxPreference) prefSet.findPreference(POWER_MENU_ONTHEGO_ENABLED);
        if (!NamelessUtils.hasCamera(getActivity())) {
            prefSet.removePreference(mOnTheGoPowerMenu);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mProfilesPowerMenu) {
            int mProfileShow = Integer.valueOf((String) objValue);
            int index = mProfilesPowerMenu.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PROFILES_IN_POWER_MENU, mProfileShow);
            mProfilesPowerMenu.setSummary(mProfilesPowerMenu.getEntries()[index]);
        } else {
            return false;
        }
        return true;
    }
}
