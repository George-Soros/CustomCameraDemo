package com.cameratest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cameratest.ui.CustomOneActivity;

/**
 * Created by 17081292 on 2017/9/18.
 */

public class FlashActivity extends AppCompatActivity{

    private static final int PERMISSION_CAMERA_TAG = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //view
        TextView view = new TextView(this);
        view.setText("点击拍照");
        view.setTextSize(30);
        view.setGravity(Gravity.CENTER);
        setContentView(view);

        //监听
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(FlashActivity.this, Camera2TestActivity.class));
//                startActivity(new Intent(FlashActivity.this, Camera1TestActivity.class));
                startActivity(new Intent(FlashActivity.this, CustomOneActivity.class));


            }
        });

        //权限
        checkSelfPermission();

    }

    void checkSelfPermission() {
        //去申请权限
        if (ContextCompat.checkSelfPermission(this.getApplicationContext()
                , Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {//PERMISSION_GRANTED允许
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_TAG);
            return;
        }
    }

    /**
     * 权限申请的回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CAMERA_TAG){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission accepte", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
