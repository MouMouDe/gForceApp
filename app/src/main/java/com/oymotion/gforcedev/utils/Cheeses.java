/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.oymotion.gforcedev.utils;


import com.oymotion.gforcedev.R;
import com.oymotion.gforcedev.global.OymotionApplication;

public class Cheeses {

    public static final String[] sCheeseStrings = {
            OymotionApplication.context.getResources().getString(R.string.menu_main),
            OymotionApplication.context.getResources().getString(R.string.menu_member),
            OymotionApplication.context.getResources().getString(R.string.menu_message),
            OymotionApplication.context.getResources().getString(R.string.menu_device),
            OymotionApplication.context.getResources().getString(R.string.menu_setting),
            OymotionApplication.context.getResources().getString(R.string.menu_introduce)
    };

    public static final Integer[] sCheeseIcons = {
            R.drawable.main,
            R.drawable.person,
            R.drawable.message,
            R.drawable.device,
            R.drawable.setting,
            R.drawable.about
    };
}
