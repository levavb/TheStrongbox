package com.example.thestrongbox.ProfileSetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thestrongbox.ForgotPassword.ChangePasswordActivity;
import com.example.thestrongbox.Home.MainActivity;
import com.example.thestrongbox.Model.MyBaseActivity;
import com.example.thestrongbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Timer;
import java.util.TimerTask;

public class SettingsActivity extends MyBaseActivity {

    private EditText display_name, display_email, user_phone;
    private TextView updatedMsg;
    private Button saveInfoBtn;
    private Button changePassBtn;

    private DatabaseReference getUserDatabaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        getUserDatabaseReference.keepSynced(true); // for offline

        updatedMsg = findViewById(R.id.updatedMsg);
        display_name = findViewById(R.id.user_display_name );
        display_email = findViewById(R.id.userEmail);
        user_phone = findViewById(R.id.phone);
        saveInfoBtn = findViewById(R.id.saveInfoBtn);
        changePassBtn = findViewById(R.id.ChangePassword);

        Toolbar toolbar = findViewById(R.id.profile_settings_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Retrieve data from database
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve data from db
                String name = dataSnapshot.child("user_name").getValue().toString();
                String email = dataSnapshot.child("user_email").getValue().toString();
                String phone = dataSnapshot.child("user_mobile").getValue().toString();

                display_name.setText(name);
                display_name.setSelection(display_name.getText().length());

                user_phone.setText(phone);
                user_phone.setSelection(user_phone.getText().length());

                display_email.setText(email);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /** Edit information */
        saveInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = display_name.getText().toString();
                String uPhone = user_phone.getText().toString();

                saveInformation(uName, uPhone);
            }
        });

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        // hide soft keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    } // Ending onCrate



    private void saveInformation(String uName, String uPhone) {
        if (TextUtils.isEmpty(uName)){
            Toast.makeText(SettingsActivity.this, "Oops! your name can't be empty",Toast.LENGTH_SHORT).show();
        } else if (uName.length()<3 || uName.length()>12){
            Toast.makeText(SettingsActivity.this, "Your name should be 3 to 12 numbers of characters",Toast.LENGTH_SHORT).show();
        } else {
            getUserDatabaseReference.child("user_name").setValue(uName);
            getUserDatabaseReference.child("user_mobile").setValue(uPhone)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updatedMsg.setVisibility(View.VISIBLE);

                            new Timer().schedule(new TimerTask(){
                                public void run() {
                                    SettingsActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            updatedMsg.setVisibility(View.GONE);
                                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }, 1500);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }


}
