package com.android.settings.fuelgauge;

import android.content.Context;
import android.provider.Settings;

import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;

public class IdleManagerPreferenceController extends BasePreferenceController {

    public IdleManagerPreferenceController(Context context) {
        super(context, "idle_manager");
    }

    @Override
    public void updateState(Preference preference) {
        preference.setEnabled(Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.AGGRESSIVE_STANDBY_SLEEP, 0) == 0);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }
}
