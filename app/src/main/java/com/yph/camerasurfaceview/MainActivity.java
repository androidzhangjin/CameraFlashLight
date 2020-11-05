package com.yph.camerasurfaceview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private CameraSurfaceView cameraSurfaceView;
    private boolean openLight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxPermissionHelper.requesPermission(MainActivity.this, new RxPermissionHelper.PermissionGetListener() {
                    @Override
                    public void onSuccess() {
                        Log.i("xx", "onSuccess: ");
                        setContentView(R.layout.activity_main);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                        getSupportActionBar().hide();
                        start();
                    }

                    @Override
                    public void onFailed() {
                        Log.i("xx", "onFailed: ");
                    }
                }, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );


    }

    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
        }
        return retVal;
    }

    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;

    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                cameraSurfaceView.switchLight(openLight);
            }
        }
    }

    private void start() {
        cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        cameraSurfaceView.init(MainActivity.this);
        findViewById(R.id.capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSurfaceView.capture();
            }
        });
        ((ToggleButton) findViewById(R.id.record)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cameraSurfaceView.startRecord();
                    //设置录制时长为10秒视频
//                    cameraSurfaceView.startRecord(10000, new MediaRecorder.OnInfoListener() {
//                        @Override
//                        public void onInfo(MediaRecorder mr, int what, int extra) {
//                            cameraSurfaceView.stopRecord();
//                            buttonView.setChecked(false);
//                        }
//                    });
                } else
                    cameraSurfaceView.stopRecord();
            }
        });

        ((ToggleButton) findViewById(R.id.light)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openLight = isChecked;
                if (!checkSystemWritePermission()) {
                    requestWriteSettings();
                } else {
                    cameraSurfaceView.switchLight(isChecked);
                }
            }
        });
        ((ToggleButton) findViewById(R.id.runBack)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cameraSurfaceView.setRunBack(isChecked);
            }
        });
        ((ToggleButton) findViewById(R.id.switchCamera)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cameraSurfaceView.setDefaultCamera(!isChecked);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSurfaceView.closeCamera();
    }


}
