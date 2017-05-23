package com.oymotion.gforcedev.global;

import android.app.Application;

/***
 * OymotionApplication
 * @author MouMou
 *
 */

public class OymotionApplication extends Application {
    public static OymotionApplication context;


    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
    }
}
