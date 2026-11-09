/*
 * Copyright (C) 2026 Project Infinity X
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.display

import android.content.Context
import android.provider.Settings
import android.view.CrossWindowBlurListeners.CROSS_WINDOW_BLUR_SUPPORTED
import com.android.settings.R
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.datastore.KeyValueStoreDelegate
import com.android.settingslib.datastore.SettingsGlobalStore
import com.android.settingslib.metadata.PreferenceAvailabilityProvider
import com.android.settingslib.metadata.preferencesapi.preconditions.PreconditionStability
import com.android.settingslib.metadata.ReadWritePermit
import com.android.settingslib.metadata.SensitivityLevel
import com.android.settingslib.metadata.SwitchPreference

class EnableBlursPreference :
    SwitchPreference(
        key = KEY,
        purpose = R.string.disable_window_blurs_purpose,
        title = R.string.window_blurs,
    ),
    PreferenceAvailabilityProvider {

    override fun storage(context: Context): KeyValueStore =
        EnableBlursStore(SettingsGlobalStore.get(context))

    override fun getReadPermissions(context: Context) = SettingsGlobalStore.getReadPermissions()

    override fun getWritePermissions(context: Context) = SettingsGlobalStore.getWritePermissions()

    override fun getReadPermit(context: Context, callingPid: Int, callingUid: Int) =
        ReadWritePermit.ALLOW

    override fun getWritePermit(
        context: Context,
        value: Boolean?,
        callingPid: Int,
        callingUid: Int,
    ) = ReadWritePermit.ALLOW

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY

    override val availabilityDescription =
        "The device must support cross window blur."

    override fun getAvailabilityStability() = PreconditionStability.STABLE_UNTIL_APK_UPDATE

    override fun isAvailable(context: Context) = CROSS_WINDOW_BLUR_SUPPORTED

    @Suppress("UNCHECKED_CAST")
    private class EnableBlursStore(
        private val globalStore: KeyValueStore,
    ) : KeyValueStoreDelegate {

        override val keyValueStoreDelegate
            get() = globalStore

        override fun <T : Any> getDefaultValue(key: String, valueType: Class<T>): T? =
            false as T

        override fun <T : Any> getValue(key: String, valueType: Class<T>): T? {
            val disableBlurs = globalStore.getInt(key) ?: 1
            return (disableBlurs == 0) as T
        }

        override fun <T : Any> setValue(key: String, valueType: Class<T>, value: T?) {
            if (key == KEY) {
                val enable = value as? Boolean ?: false
                globalStore.setInt(key, if (enable) 0 else 1)
            }
        }
    }

    companion object {
        const val KEY = Settings.Global.DISABLE_WINDOW_BLURS
    }
}
