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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.view.MenuItem;

import com.android.internal.app.LocaleStore;
import com.android.settings.R;

import com.google.android.setupcompat.util.WizardManagerHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
public class SetupWizardLocalePickerActivityTest {
    private SetupWizardLocalePickerActivity mActivity;
    private LocaleStore.LocaleInfo mLocaleInfo;

    @Before
    public void setUp() {
        mActivity = spy(Robolectric.buildActivity(SetupWizardLocalePickerActivity.class).get());
        mLocaleInfo = mock(LocaleStore.LocaleInfo.class);
        when(mLocaleInfo.getLocale()).thenReturn(Locale.GERMANY);
        // Test the callback without calling the real ActivityManager locale-update binder.
        doNothing().when(mActivity).updateSystemLocale(Locale.GERMANY);
        Settings.Global.putInt(mActivity.getContentResolver(), "is_locale_set", 0);
    }

    @Test
    public void onLocaleSelected_appliesSelectedSystemLocale() {
        mActivity.onLocaleSelected(mLocaleInfo);

        verify(mActivity).updateSystemLocale(Locale.GERMANY);
    }

    @Test
    public void onLocaleSelected_marksLocaleAsExplicitlyChosen() {
        mActivity.onLocaleSelected(mLocaleInfo);

        assertEquals(1, Settings.Global.getInt(mActivity.getContentResolver(), "is_locale_set", 0));
    }

    @Test
    public void onLocaleSelected_returnsSuccessAndFinishes() {
        mActivity.onLocaleSelected(mLocaleInfo);

        assertEquals(Activity.RESULT_OK, Shadows.shadowOf(mActivity).getResultCode());
        assertNotNull(
                Shadows.shadowOf(mActivity)
                        .getResultIntent()
                        .getSerializableExtra(LocaleListEditor.INTENT_LOCALE_KEY));
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void onCreate_firstRunSetupWithoutActionBar_doesNotCrash() {
        final Intent intent = new Intent()
                .putExtra(WizardManagerHelper.EXTRA_IS_FIRST_RUN, true)
                .putExtra(WizardManagerHelper.EXTRA_IS_SETUP_FLOW, true);
        assertSetupLayoutWorksWithoutActionBar(intent);
    }

    @Test
    public void onCreate_setupFlowWithoutActionBar_doesNotCrash() {
        assertSetupLayoutWorksWithoutActionBar(new Intent()
                .putExtra(WizardManagerHelper.EXTRA_IS_SETUP_FLOW, true));
    }

    private void assertSetupLayoutWorksWithoutActionBar(Intent intent) {
        try (ActivityController<SetupWizardLocalePickerActivity> controller =
                Robolectric.buildActivity(SetupWizardLocalePickerActivity.class, intent)) {
            final SetupWizardLocalePickerActivity activity =
                    controller.create().start().resume().visible().get();
            activity.getFragmentManager().executePendingTransactions();

            assertNull(activity.getActionBar());
            assertFalse(activity.isFinishing());
            assertNotNull(activity.findViewById(R.id.content_frame));
            assertNotNull(activity.getFragmentManager().findFragmentByTag("LocalePickerWithRegion"));

            // Search callbacks must also work without a collapsing app bar.
            final MenuItem item = mock(MenuItem.class);
            assertTrue(activity.onMenuItemActionExpand(item));
            assertTrue(activity.onMenuItemActionCollapse(item));
        }
    }
}
