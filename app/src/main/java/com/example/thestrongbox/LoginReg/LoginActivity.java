package com.example.thestrongbox.LoginReg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thestrongbox.ForgotPassword.ForgotPassActivity;
import com.example.thestrongbox.ForgotPassword.ResetPassActivity;
import com.example.thestrongbox.R;
import com.example.thestrongbox.WelcomeSlide.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private String actionCode;


    private EditText userEmail, userPassword;
    private Button loginButton;
    private TextView linkSingUp, linkForgotPassword, copyrightTV;


    private ProgressDialog progressDialog;

    //Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference userDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");


        userEmail = findViewById(R.id.inputEmail);
        userPassword = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.loginButton);
        linkSingUp = findViewById(R.id.linkSingUp);
        linkForgotPassword = findViewById(R.id.linkForgotPassword);
        progressDialog = new ProgressDialog(this);

        //Copyright text
        copyrightTV = findViewById(R.id.copyrightTV);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        copyrightTV.setText("AL © " + year);

        //redirect to FORGOT PASS activity
        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go to FORGOT Activity");
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);

            }
        });

        //redirect to register activity
        linkSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go to Register Activity");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });


        /**
         * Login Button with Firebase
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();

            }
        });

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null) {

            // Get the action to complete.
            String mode = appLinkData.getQueryParameter("mode");
            // Get the one-time code from the query parameter.
            actionCode = appLinkData.getQueryParameter("oobCode");
            // (Optional) Get the continue URL from the query parameter if available.
            String continueUrl = appLinkData.getQueryParameter("continueUrl");
            // (Optional) Get the language code if available.
            String lang = appLinkData.getQueryParameter("lang");
            String apikey = appLinkData.getQueryParameter("apiKey");

            // Handle the user management action.
            switch (mode) {
                case "resetPassword":
                    // Display reset password handler and UI.
                    handleResetPassword();
                    break;
                case "recoverEmail":
                    // Display email recovery handler and UI.
//                    handleRecoverEmail(auth, actionCode, lang);
                    break;
                case "verifyEmail":
                    // Display email verification handler and UI.
//                    handleVerifyEmail(auth, actionCode, continueUrl, lang);
                    break;
                default:
                    // Error: invalid mode.
            }


        }
    }

    private void handleResetPassword() {
        // Localize the UI to the selected language as determined by the lang
        // parameter.

        // Verify the password reset code is valid.
        mAuth.verifyPasswordResetCode(actionCode).addOnCompleteListener(this, new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
                    intent.putExtra("actionCode", actionCode);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            }
        });
        //TODO: Add fail
    }

    private void loginUserAccount() {
        //just validation
        String email    = userEmail.getText().toString();
        String password = userPassword.getText().toString();


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email is required",Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Your email is not valid.",Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password is required",Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6){
            Toast.makeText(this, "May be your password had minimum 6 numbers of character.",Toast.LENGTH_SHORT).show();
        } else {
            //progress bar
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            // after validation checking, log in user a/c
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                checkVerifiedEmail();

                            } else {
                                Toast.makeText(LoginActivity.this, "Your email and password may be incorrect. Please check & try again.",Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });

        }
        password = "";
    }

    /** checking email verified or NOT */
    private void checkVerifiedEmail() {
        user = mAuth.getCurrentUser();
        boolean isVerified = false;
        if (user != null) {
            isVerified = user.isEmailVerified();
        }
        if (isVerified){
            String UID = mAuth.getCurrentUser().getUid();
            userDatabaseReference.child(UID).child("verified").setValue("true");

            Intent WelcomeIntent =  new Intent(LoginActivity.this, WelcomeActivity.class);
            WelcomeIntent.putExtra("USER_PASS", userPassword.getText().toString());
            WelcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            userPassword.setText("");
            startActivity(WelcomeIntent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Email is not verified. Please verify first",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }



}