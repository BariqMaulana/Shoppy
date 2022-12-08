package com.example.shoppy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    Animation topAnim;
    Animation bottomAnim;
    ImageView image;
    TextView slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.imageView);
        slogan = findViewById(R.id.textView);

        image.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);

        int SPLASH_SCREEN = 5000;

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                startActivity( new Intent(getApplicationContext(), Login.class));

                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
                finish();
            }

        }, SPLASH_SCREEN);
    }
}