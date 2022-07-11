package com.bosictsolution.quickwaiter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH=5000;
    TextView tvAppName,tvCompanyName;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvAppName=(TextView)findViewById(R.id.tvAppName);
        tvCompanyName=(TextView)findViewById(R.id.tvCompanyName);

        this.getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                wifiOn();
                permission();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void wifiOn(){
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifi.isWifiEnabled()){
            Toast.makeText(this,"Please, Turn On Wifi!",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                goStarting();
            }
        } else {
            goStarting();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Require Storage Permission, Please allow this permission in App SystemSetting.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(SplashActivity.this, StartActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this,"Require Storage Permission, Please allow this permission in App SystemSetting.",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void goStarting(){
        Intent mainIntent=new Intent(SplashActivity.this,StartActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }
}
