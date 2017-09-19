package com.cameratest.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.cameratest.R;
import com.cameratest.view.Camera1SurfaceView;
import com.cameratest.view.CustomRectCameraView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 17081292 on 2017/9/19.
 */

public class CustomOneActivity extends AppCompatActivity {


    @Bind(R.id.surfaceview)
    Camera1SurfaceView mSurfaceView;//surfaceView承载camera的预览

    @Bind(R.id.CustomRectCameraView)
    CustomRectCameraView mRectView;//自定义的拍照框view

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera_custom_one);
        ButterKnife.bind(this);
        initView();
    }

    void initView(){
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
