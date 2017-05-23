package com.oymotion.gforcedev.ui.fragment;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/*
 * control all of the fragments,Fragment can only be obtained through this class
 * FragmentInstanceManager
 *
 * Created by MouMou
 */
public class FragmentInstanceManager {

    private FragmentInstanceManager() {
    }

    private static FragmentInstanceManager sInstance = new FragmentInstanceManager();

    public static FragmentInstanceManager getInstance() {

        return sInstance;
    }

    private Map<String, Fragment> mFragments = new HashMap<String, Fragment>();

    // Provides methods for external access to Fragment
    public Fragment getFragment(Class<? extends Fragment> clazz) {

        String key = clazz.getSimpleName();
        Fragment fragment = mFragments.get(key);

        if (fragment == null) {

            synchronized (FragmentInstanceManager.class) {
                try {
                    if (fragment == null) {
                        fragment = clazz.newInstance();
                        mFragments.put(key, fragment);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        return fragment;
    }

}
