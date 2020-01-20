package com.example.thestrongbox.WelcomeSlide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pass = getIntent().getStringExtra("USER_PASS");
        Log.d("AFEK", "pass: " + pass );
        getIntent().putExtra("USER_PASS", "");
        Intent IntroIntent =  new Intent(WelcomeActivity.this, IntroActivity.class);
        IntroIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        IntroIntent.putExtra("USER_PASS", pass);
        pass = "";
        startActivity(IntroIntent);
        finish();

    }

}
