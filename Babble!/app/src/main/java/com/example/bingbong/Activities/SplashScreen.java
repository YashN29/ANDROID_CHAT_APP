package com.example.bingbong.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.bingbong.R;

public class SplashScreen extends AppCompatActivity {

    public static int spalashtimeout = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this,Authenticate.class);
                startActivity(i);
                finish();
            }
        },spalashtimeout);
    }
}