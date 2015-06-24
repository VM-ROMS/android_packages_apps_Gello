/*
 * Copyright (c) 2015, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * * Neither the name of The Linux Foundation nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.android.browser.mdm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;

import com.android.browser.BrowserSettings;
import com.android.browser.PreferenceKeys;

public class AutoFillRestriction extends Restriction implements PreferenceKeys {

    private final static String TAG = "AutoFillRestriction";

    public static final String AUTO_FILL_RESTRICTION = "AutoFillEnabled";

    private static AutoFillRestriction sInstance;
    private MdmCheckBoxPreference mPref = null;

    private AutoFillRestriction() {
        super(TAG);
    }

    public static AutoFillRestriction getInstance() {
        synchronized (AutoFillRestriction.class) {
            if (sInstance == null) {
                sInstance = new AutoFillRestriction();
            }
        }
        return sInstance;
    }

    @Override
    protected void doCustomInit() {
    }


    public void registerPreference (Preference pref) {
        mPref = (MdmCheckBoxPreference) pref;
        updatePref();
    }

    private void updatePref() {
        if (null != mPref) {
            if (isEnabled()) {
                mPref.setChecked(false);
                mPref.disablePref();
            }
            else {
                mPref.setChecked(true);
                mPref.enablePref();
            }
            mPref.setMdmRestrictionState(isEnabled());
        }
    }

    /*
     *   Note reversed logic:
     *       [x] 'Restrict' true  = AutoFillEnabled : false   => disable Auto fill in swe
     *       [ ] 'Restrict' false = AutoFillEnabled : true    => enable Auto fill in swe
     */
    @Override
    public void enforce(Bundle restrictions) {
        SharedPreferences.Editor editor = BrowserSettings.getInstance().getPreferences().edit();

        boolean bEnable = false;
        if (restrictions.containsKey(AUTO_FILL_RESTRICTION)) {
            bEnable = ! restrictions.getBoolean(AUTO_FILL_RESTRICTION);
        }
        Log.i(TAG, "Enforce [" + bEnable + "]");
        enable(bEnable);

        editor.putBoolean(PREF_AUTOFILL_ENABLED, !isEnabled());
        editor.apply();

        updatePref();
    }
}
