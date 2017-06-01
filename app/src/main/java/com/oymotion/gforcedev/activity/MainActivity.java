package com.oymotion.gforcedev.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nineoldandroids.view.ViewHelper;
import com.oymotion.gforcedev.DeviceScanForceActivity;
import com.oymotion.gforcedev.utils.Cheeses;
import com.oymotion.gforcedev.DeviceScanActivity;
import com.oymotion.gforcedev.R;
import com.oymotion.gforcedev.adapters.MemuAdapter;
import com.oymotion.gforcedev.ui.DragLayout;
import com.oymotion.gforcedev.ui.MyLinearLayout;
import com.oymotion.gforcedev.utils.ToastUtil;


/**
 * MainActivity
 * @author MouMou
 */

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private DragLayout dl;
    private ImageView iv_gfoece;
    private LinearLayout ll_main;
    private Button bt_scan_device;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        iv_gfoece = (ImageView) findViewById(R.id.iv_gforce);
        bt_scan_device = (Button) findViewById(R.id.bt_scan_device);
        dl = (DragLayout) findViewById(R.id.dl);
        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        ll_main.setOnClickListener(null);

        final View iv_header = findViewById(R.id.iv_header);
        final ListView lv_left = (ListView) findViewById(R.id.lv_left);

        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.main_animationset);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.main_animationset);
                anim.setAnimationListener(this);
                iv_gfoece.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_gfoece.startAnimation(animationSet);

        iv_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dl.open();

            }
        });
        bt_scan_device.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DeviceScanForceActivity.class));
            }
        });

        lv_left.setAdapter(new MemuAdapter(Cheeses.sCheeseStrings,Cheeses.sCheeseIcons));
        lv_left.setOnItemClickListener(this);

        dl.setOnDragChangeListener(new DragLayout.OnDragChangeListener() {
            //add animation
            @Override
            public void onOpen() {
            }

            @Override
            public void onDraging(float percent) {
                // 1.0 -> 0.0
                ViewHelper.setAlpha(iv_header, 1 - percent);
            }

            //while the menuContent is closed ,you can do sth in this method
            @Override
            public void onClose() {

            }
        });

        MyLinearLayout ll_main = (MyLinearLayout) findViewById(R.id.ll_main);
        ll_main.setDragLayout(dl);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0://main content
                dl.close();
                break;
            case 1://member center
                startActivity("member");
                break;
            case 2://messeage center
                startActivity("message");
                break;
            case 3://decvice control
                startActivity("device");
                break;
            case 4://setting center
                startActivity("setting");
                break;
            case 5://introduce
                startActivity("introduce");
                break;
        }
    }

    private void startActivity(String str) {
        dl.close();
        Intent intent = new Intent(this,OtherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("menu",str);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static void create(Context context, int index) {
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("index",index);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showCenterToast("Press again,then quit gForceApp");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
