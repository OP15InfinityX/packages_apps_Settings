/*
 * Copyright (C) 2026 The Infinity-X Project
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

package com.android.settings.localepicker;

import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.VisibleForTesting;

import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocaleStore;

import java.util.Locale;

/** Adapts Android's locale picker to Pixel SetupWizard's system-locale contract. */
public class SetupWizardLocalePickerActivity extends LocalePickerWithRegionActivity {
    @Override
    public void onLocaleSelected(LocaleStore.LocaleInfo localeInfo) {
        // The ordinary Settings picker only returns localeInfo to its caller.
        // Pixel SetupWizard instead reads the updated system locale on return.
        updateSystemLocale(localeInfo.getLocale());

        // WelcomeActivity checks this before consulting SIM/SKU locale suggestions.
        Settings.Global.putInt(getContentResolver(), "is_locale_set", 1);

        setResult(RESULT_OK, new Intent().putExtra(LocaleListEditor.INTENT_LOCALE_KEY, localeInfo));
        finish();
    }

    @VisibleForTesting
    void updateSystemLocale(Locale locale) {
        LocalePicker.updateLocale(locale);
    }
}
