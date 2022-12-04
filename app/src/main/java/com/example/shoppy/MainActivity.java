package com.example.shoppy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int SPLASH_SCREEN = 5000;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                startActivity( new Intent(getApplicationContext(), Login.class));

                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
                finish();
            }

            //private void startActivity(Intent intent) {
            //}
        }, SPLASH_SCREEN);
    }
}