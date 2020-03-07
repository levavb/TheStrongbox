package com.example.thestrongbox.ForgotPassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thestrongbox.R;
import com.example.thestrongbox.WelcomeSlide.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPassActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText newPass, newPassConfirm, userEmail;
    private Button ChangePassButton;
    private Context myContext = ResetPassActivity.this;
    private String actionCode;

    private DatabaseReference userDatabaseReferenceData;
    private FirebaseAuth auth;

    byte[] newMasterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_pass);

        mToolbar = findViewById(R.id.cp_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        newPass        = findViewById(R.id.newPass);
        newPassConfirm = findViewById(R.id.newPassConfirm);
        userEmail      = findViewById(R.id.inputEmail);

        ChangePassButton = findViewById(R.id.ChangePassButton);
        actionCode       = getIntent().getStringExtra("actionCode");
        // ATTENTION: This was auto-generated to handle app links.

    }

    public void onStart() {
        super.onStart();

        ChangePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickReset();

            }

        });

    }

    private void onClickReset(){

        String email            = userEmail.getText().toString();
        String new_pass         = newPass.getText().toString();
        String new_pass_confirm = newPassConfirm.getText().toString();

        if (TextUtils.isEmpty(new_pass)) {
            Toast.makeText(myContext, "Please fill this password field", Toast.LENGTH_SHORT).show();
        } else if (new_pass.length() < 6) {
            Toast.makeText(myContext, "Create a password at least 6 characters long.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(new_pass_confirm)) {
            Toast.makeText(myContext, "Please retype in password field", Toast.LENGTH_SHORT).show();
        } else if (!new_pass.equals(new_pass_confirm)) {
            Toast.makeText(myContext, "Your password don't match with your confirm password", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(email)){
            Toast.makeText(myContext, "Email is required",Toast.LENGTH_SHORT).show();
        } else {
            new_pass = "";
            new_pass_confirm = "";
            auth.confirmPasswordReset(actionCode, newPass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        auth.signInWithEmailAndPassword(userEmail.getText().toString(), newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String UID = auth.getCurrentUser().getUid();

                                DatabaseReference userDatabaseReference;
                                userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("data");
                                userDatabaseReference.removeValue();


                                Intent WelcomeIntent =  new Intent(ResetPassActivity.this, WelcomeActivity.class);
                                WelcomeIntent.putExtra("USER_PASS", newPass.getText().toString());
                                WelcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                newPass.setText("");
                                newPassConfirm.setText("");
                                startActivity(WelcomeIntent);
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }

}
