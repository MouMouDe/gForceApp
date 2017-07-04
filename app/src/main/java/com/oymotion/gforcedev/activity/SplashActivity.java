package com.oymotion.gforcedev.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.oymotion.gforcedev.DeviceScanHtmlActivity;
import com.oymotion.gforcedev.R;
import com.oymotion.gforcedev.ui.CustomVideoView;


/**
 * SplashActivity
 * @author MouMou
 */
public class SplashActivity extends Activity {
    private CustomVideoView videoview;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        videoview = (CustomVideoView) findViewById(R.id.cv_splash);
        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.out));
        videoview.start();
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                    startActivity(new Intent(SplashActivity.this,DeviceScanHtmlActivity.class));
                    finish();
            }
        });

    }
}
