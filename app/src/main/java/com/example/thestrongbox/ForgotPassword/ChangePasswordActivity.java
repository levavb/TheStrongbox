package com.example.thestrongbox.ForgotPassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thestrongbox.Model.AESCrypt;
import com.example.thestrongbox.Model.CryptoHash;
import com.example.thestrongbox.Model.MyBaseActivity;
import com.example.thestrongbox.ProfileSetting.SettingsActivity;
import com.example.thestrongbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;


public class ChangePasswordActivity extends MyBaseActivity {

    private Toolbar mToolbar;
    private EditText newPass;
    private EditText newPassConfirm;
    private Button ChangePassButton;
    private Context myContext = ChangePasswordActivity.this;

    private DatabaseReference userDatabaseReferenceData;
    private FirebaseAuth auth;

    byte[] newMasterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_pass);

        mToolbar = findViewById(R.id.cp_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        auth = FirebaseAuth.getInstance();
        String user_uID = auth.getCurrentUser().getUid();
        userDatabaseReferenceData = FirebaseDatabase.getInstance().getReference().child("users").child(user_uID).child("data");


        newMasterKey = new byte[16];


        newPass        = findViewById(R.id.newPass);
        newPassConfirm = findViewById(R.id.newPassConfirm);

        ChangePassButton = findViewById(R.id.ChangePassButton);

    }

    public void onStart() {
        super.onStart();

        ChangePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String new_pass = newPass.getText().toString();
                String new_pass_confirm = newPassConfirm.getText().toString();

                if (TextUtils.isEmpty(new_pass)) {
                    Toast.makeText(myContext, "Please fill this password field", Toast.LENGTH_SHORT).show();
                } else if (new_pass.length() < 6) {
                    Toast.makeText(myContext, "Create a password at least 6 characters long.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(new_pass_confirm)) {
                    Toast.makeText(myContext, "Please retype in password field", Toast.LENGTH_SHORT).show();
                } else if (!new_pass.equals(new_pass_confirm)) {
                    Toast.makeText(myContext, "Your password don't match with your confirm password", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseUser user = auth.getCurrentUser();

                    user.updatePassword(new_pass)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("AFEK", "User password updated.");
                                        UpdateBDwithNewPassword();
                                    }
                                }
                            });
                }
                new_pass = "";
                new_pass_confirm = "";
                Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        });

    }

    public void UpdateBDwithNewPassword () {
        try {
            newMasterKey = CryptoHash.getSha256(newPass.getText().toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        newPass.setText("");
        newPassConfirm.setText("");
        userDatabaseReferenceData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String Spass_enc = postSnapshot.child("password").getValue().toString();

                    String Spass = null;
                    String new_pass_enc = null;
                    try {
                        Spass = AESCrypt.decrypt(Spass_enc, MasterKey);
                        new_pass_enc = AESCrypt.encrypt(Spass, newMasterKey);
                    } catch (Exception e) {
                        //TODO: think what to do in this case
                        e.printStackTrace();
                    }
                    userDatabaseReferenceData.child(postSnapshot.getKey()).child("password").setValue(new_pass_enc)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("UpdateDB:", "Completed");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("UpdateDB:", "Failed");
                        }
                    });

                }
                MasterKey = newMasterKey;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("OnCancelled", "on");
            }
        });


    }

}
