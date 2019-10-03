package com.example.thestrongbox.StartApp;

import android.app.Application;
import android.content.Intent;

import com.example.thestrongbox.LoginReg.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class Start_Activity extends Application {

        private FirebaseAuth mAuth;
        private FirebaseUser currentOnlineUser;

        @Override
        public void onCreate() {
            super.onCreate();

            mAuth = FirebaseAuth.getInstance();
            currentOnlineUser = mAuth.getCurrentUser();

            if (currentOnlineUser == null){
                Intent myIntent = new Intent(Start_Activity.this, LoginActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            }


        }
}
