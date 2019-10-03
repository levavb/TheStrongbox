package com.example.thestrongbox.LoginReg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thestrongbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private Context myContext = RegisterActivity.this;


    private EditText
            registerUserFullName,
            registerUserEmail,
            registerUserMobileNo,
            registerUserPassword,
            confirmRegisterUserPassword;

    private Button registerUserButton;
    private ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference storeDefaultDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "on Create : started");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        registerUserFullName = (EditText)findViewById(R.id.registerFullName);
        registerUserEmail = (EditText)findViewById(R.id.registerEmail);
        registerUserMobileNo = (EditText)findViewById(R.id.registerMobileNo);
        registerUserPassword = (EditText)findViewById(R.id.registerPassword);
        confirmRegisterUserPassword = (EditText)findViewById(R.id.confirm_registerPassword);

        //Working with Create A/C Button Or, Register a/c
        registerUserButton = (Button) findViewById(R.id.resisterButton);
        registerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = registerUserFullName.getText().toString();
                final String email = registerUserEmail.getText().toString();
                final String mobile = registerUserMobileNo.getText().toString();
                String password = registerUserPassword.getText().toString();
                String confirmPassword = confirmRegisterUserPassword.getText().toString();

                // pass input parameter through this Method
                registerAccount(name, email, mobile, password, confirmPassword);
            }
        });
        progressDialog = new ProgressDialog(myContext);
    }// ending onCreate



    private void registerAccount(final String name, final String email, final String mobile, String password, String confirmPassword) {

        //Validation for empty fields
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(myContext, "Your name is required.", Toast.LENGTH_SHORT).show();
        } else if (name.length() < 3 || name.length() > 40){
            Toast.makeText(myContext, "Your name should be 3 to 40 numbers of characters.", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(email)){
            Toast.makeText(myContext, "Your email is required.", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(myContext, "Your email is not valid.", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(mobile)){
            Toast.makeText(myContext, "Your mobile number is required.", Toast.LENGTH_SHORT).show();
        } else if (mobile.length() < 10){
            Toast.makeText(myContext, "Mobile number should be min 10 characters.", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(myContext, "Please fill this password field", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6){
            Toast.makeText(myContext, "Create a password at least 6 characters long.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(myContext, "Please retype in password field", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)){
            Toast.makeText(myContext, "Your password don't match with your confirm password", Toast.LENGTH_SHORT).show();

        } else {
            //NOw ready to create a user a/c
            Toast.makeText(myContext, "now you ready to create acoount", Toast.LENGTH_SHORT).show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(myContext, "1", Toast.LENGTH_SHORT).show();
                            if (task.isSuccessful()){
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                Toast.makeText(myContext, "2", Toast.LENGTH_SHORT).show();
                                // get and link storage
                                String current_userID =  mAuth.getCurrentUser().getUid();
                                storeDefaultDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(current_userID);
                                storeDefaultDatabaseReference.child("user_name").setValue(name);
                                storeDefaultDatabaseReference.child("verified").setValue("false");
                                storeDefaultDatabaseReference.child("search_name").setValue(name.toLowerCase());
                                storeDefaultDatabaseReference.child("user_mobile").setValue(mobile);
                                storeDefaultDatabaseReference.child("user_email").setValue(email);
                                storeDefaultDatabaseReference.child("device_token").setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    // SENDING VERIFICATION EMAIL TO THE REGISTERED USER'S EMAIL
                                                    user = mAuth.getCurrentUser();
                                                    if (user != null){
                                                        user.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){

                                                                            registerSuccessPopUp();

                                                                            // LAUNCH activity after certain time period
                                                                            new Timer().schedule(new TimerTask(){
                                                                                public void run() {
                                                                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                                                                        public void run() {
                                                                                            mAuth.signOut();

                                                                                            Intent mainIntent =  new Intent(myContext, LoginActivity.class);
                                                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            startActivity(mainIntent);
                                                                                            finish();

                                                                                            Toast.makeText(myContext, "Please check your email & verify.", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }, 8000);


                                                                        } else {
                                                                            mAuth.signOut();
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                }
                                            }
                                        });

                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(myContext, "Error occurred : " + message, Toast.LENGTH_SHORT).show();
                            }

                            progressDialog.dismiss();

                        }
                    });


            //config progressbar
            progressDialog.setTitle("Creating new account");
            progressDialog.setMessage("Please wait a moment....");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

    }

    private void registerSuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.register_success_popup, null);

        builder.setCancelable(false);
        builder.setView(view);
        builder.show();
    }




}
