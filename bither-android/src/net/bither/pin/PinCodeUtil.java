/*
 *
 *  * Copyright 2014 http://Bither.net
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package net.bither.pin;

import android.app.Activity;
import android.content.Intent;

import net.bither.bitherj.AbstractApp;
import net.bither.preference.AppSharedPreference;

/**
 * Created by songchenwen on 14-11-10.
 */
public class PinCodeUtil {
    private static final int CausePinCodeBackgroundTime = 60 * 1000;
    public static boolean isPreBackground = true;
    private static long backgroundEnterTime = 0;

    public static void resumeCheck(Activity activity) {
        if (isPreBackground) {
            if (AppSharedPreference.getInstance().hasPinCode() && System.currentTimeMillis() -
                    backgroundEnterTime >= CausePinCodeBackgroundTime) {
                activity.startActivity(new Intent(activity, PinCodeActivity.class));
            }
        }
        checkBackground();
    }

    public static boolean checkBackground() {
        boolean beforeBack = isPreBackground;
        isPreBackground = !AbstractApp.bitherjApp.isApplicationRunInForeground();
        if (isPreBackground && !beforeBack) {
            backgroundEnterTime = System.currentTimeMillis();
        }
        return isPreBackground;
    }

    public static boolean checkBackgroundWithoutLockDelay() {
        boolean beforeBack = isPreBackground;
        isPreBackground = !AbstractApp.bitherjApp.isApplicationRunInForeground();
        if (isPreBackground && !beforeBack) {
            backgroundEnterTime = System.currentTimeMillis() - CausePinCodeBackgroundTime * 2;
        }
        return isPreBackground;
    }
}
