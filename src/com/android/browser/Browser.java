/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.android.browser;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.os.Process;

import org.chromium.content.browser.ResourceExtractor;
import org.chromium.base.PathUtils;

import org.codeaurora.swe.Engine;

import com.android.browser.BrowserConfig;

public class Browser extends Application {

    private final static String LOGTAG = "browser";

    // Set to true to enable verbose logging.
    final static boolean LOGV_ENABLED = false;

    // Set to true to enable extra debug logging.
    final static boolean LOGD_ENABLED = true;

    private static final String[] MP_MANDATORY_PAKS = new String[] {
        "webviewchromium.pak",
        "icudtl.dat"
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initializeApplicationParameters();
        if (LOGV_ENABLED)
            Log.v(LOGTAG, "Browser.onCreate: this=" + this);

        // SWE: Avoid initializing databases for sandboxed processes.
        // Must have INITIALIZE_DATABASE permission in AndroidManifest.xml only for browser process
        final String INITIALIZE_DATABASE = BrowserConfig.AUTHORITY +
                                            ".permission.INITIALIZE_DATABASE";
        final Context context = getApplicationContext();
        boolean isActivityContext = (context.checkPermission(INITIALIZE_DATABASE,
              Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED);
        if (isActivityContext) {
            // Initialize the SWE engine.
            Engine.initialize(context);
            BrowserSettings.initialize(context);
            Preloader.initialize(context);
       }

    }

    public static void initializeApplicationParameters() {
        ResourceExtractor.setMandatoryPaksToExtract(MP_MANDATORY_PAKS);
        PathUtils.setPrivateDataDirectorySuffix("android_browser");
    }
}

