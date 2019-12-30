package com.example.thestrongbox.Model;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.thestrongbox.LoginReg.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;


public class MyBaseActivity extends AppCompatActivity {

    public byte[] MasterKey;
    public FirebaseAuth mAuth;
    public static final long SWITCH_ACTIVITY = 2;
    public static final long DISCONNECT_TIMEOUT = 300000; // 5 min = 5 * 60 * 1000 ms
    public static int activityCount;
    public static boolean CallBackFromBackground;

    private Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    });

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            MasterKey = CryptoHash.emptySHA();
            mAuth.signOut();
            if ( activityCount == 0) {
                CallBackFromBackground = true;
            } else {
                moveToLoginPage();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MasterKey = new byte[16];
        mAuth = FirebaseAuth.getInstance();
    }

    public void moveToLoginPage() {
        Intent loginIntent =  new Intent(MyBaseActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CallBackFromBackground) {
            CallBackFromBackground = false;
            moveToLoginPage();
        }
        resetDisconnectTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
        activityCount++;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (activityCount == SWITCH_ACTIVITY) {
            stopDisconnectTimer();
        }
        activityCount--;
    }

    public void onDestroy() {
        super.onDestroy();
    }

}
