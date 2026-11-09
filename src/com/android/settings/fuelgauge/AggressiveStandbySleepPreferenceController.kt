/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.android.settings.fuelgauge

import android.content.Context
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController

class AggressiveStandbySleepPreferenceController(context: Context) :
    TogglePreferenceController(context, "aggressive_standby_sleep") {

    override fun getSliceHighlightMenuRes(): Int = R.string.menu_key_battery

    private var idleManagerPreference: Preference? = null

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        idleManagerPreference = screen.findPreference("idle_manager")
        updateIdleManagerPreference()
    }

    override fun isChecked(): Boolean =
        Settings.Secure.getInt(
            mContext.contentResolver,
            Settings.Secure.AGGRESSIVE_STANDBY_SLEEP,
            0
        ) != 0

    override fun setChecked(isChecked: Boolean): Boolean {
        val result = Settings.Secure.putInt(
            mContext.contentResolver,
            Settings.Secure.AGGRESSIVE_STANDBY_SLEEP,
            if (isChecked) 1 else 0
        )
        updateIdleManagerPreference()
        return result
    }

    private fun updateIdleManagerPreference() {
        idleManagerPreference?.isEnabled = !isChecked()
    }

    override fun getAvailabilityStatus(): Int = AVAILABLE
}
