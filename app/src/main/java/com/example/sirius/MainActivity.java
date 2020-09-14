package com.example.sirius;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView myLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);



        myLogo = findViewById(R.id.imageViewmyLogo);

        myLogo.setAlpha(0f);



        CountDownTimer cdt = new CountDownTimer(4500,1500) {
            int flag=1;
            @Override
            public void onTick(long millisUntilFinished) {

                if(flag==1) {
                    myLogo.animate().alpha(1f).setDuration(1500);
                    flag=0;
                }
                else {
                    myLogo.animate().alpha(0.2f).setDuration(1500);
                    flag=1;
                }
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this,loginActivity.class);
                startActivity(intent);
            }
        }.start();

    }
}