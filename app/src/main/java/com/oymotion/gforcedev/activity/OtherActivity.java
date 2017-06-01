package com.oymotion.gforcedev.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.Window;

import com.oymotion.gforcedev.R;
import com.oymotion.gforcedev.ui.fragment.DeviceFragment;
import com.oymotion.gforcedev.ui.fragment.FragmentInstanceManager;
import com.oymotion.gforcedev.ui.fragment.IntroduceFragment;
import com.oymotion.gforcedev.ui.fragment.MemberFragment;
import com.oymotion.gforcedev.ui.fragment.MessageFragment;
import com.oymotion.gforcedev.ui.fragment.SettingFragment;
import com.oymotion.gforcedev.utils.ToastUtil;

/**
 * OtherActivity
 * @author MouMou
 */
public class OtherActivity extends FragmentActivity {

    private FragmentManager mFragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_other);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String value = bundle.getString("menu");
        mFragmentManager = getSupportFragmentManager();

        switchToChild(value);
    }

    private void switchToChild(String value) {
        switch (value) {
            case "member":
                switchFragment(FragmentInstanceManager.getInstance().getFragment(MemberFragment.class));
                break;
            case "message":
                switchFragment(FragmentInstanceManager.getInstance().getFragment(MessageFragment.class));
                break;
            case "device":
                switchFragment(FragmentInstanceManager.getInstance().getFragment(DeviceFragment.class));
                break;
            case "setting":
                switchFragment(FragmentInstanceManager.getInstance().getFragment(SettingFragment.class));
                break;
            case "introduce":
                switchFragment(FragmentInstanceManager.getInstance().getFragment(IntroduceFragment.class));
                break;
        }
    }

    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        int backStackEntryCount = mFragmentManager.getBackStackEntryCount();
        while (backStackEntryCount > 0) {
            mFragmentManager.popBackStack();
            backStackEntryCount--;
        }
        transaction.replace(R.id.fl_content, fragment);
        transaction.commit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
