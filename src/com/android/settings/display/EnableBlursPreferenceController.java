/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.settings.display;

import static android.view.CrossWindowBlurListeners.CROSS_WINDOW_BLUR_SUPPORTED;

import android.content.Context;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

public class EnableBlursPreferenceController extends TogglePreferenceController {

    private static final String KEY_ENABLE_BLURS = "enable_blurs_on_windows";

    public EnableBlursPreferenceController(Context context) {
        super(context, KEY_ENABLE_BLURS);
    }

    public EnableBlursPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return CROSS_WINDOW_BLUR_SUPPORTED ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isChecked() {
        return Settings.Global.getInt(mContext.getContentResolver(),
                Settings.Global.DISABLE_WINDOW_BLURS, 1) == 0;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return Settings.Global.putInt(mContext.getContentResolver(),
                Settings.Global.DISABLE_WINDOW_BLURS, isChecked ? 0 : 1);
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }
}
