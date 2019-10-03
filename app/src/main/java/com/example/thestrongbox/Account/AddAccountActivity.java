package com.example.thestrongbox.Account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.thestrongbox.Home.MainActivity;
import com.example.thestrongbox.Model.AESCrypt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.thestrongbox.R;

import java.util.HashMap;

public class AddAccountActivity extends AppCompatActivity {


    private DatabaseReference rootReference;
    private FirebaseAuth Auth;
    private FirebaseUser User;
    private String UserId;
    private Button addButton;
    private EditText inputEmail, inputPassword, inputNote, inputUrl;

    private Toolbar aToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        aToolbar = findViewById(R.id.add_account_toolbar);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setTitle("Add Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        rootReference = FirebaseDatabase.getInstance().getReference();
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        if (User != null) {UserId = User.getUid();}

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputNote = findViewById(R.id.inputNote);
        inputUrl = findViewById(R.id.inputUrl);
        addButton = findViewById(R.id.AddButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAccount();
                Intent mainIntent = new Intent(AddAccountActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

    }

    public void uploadAccount() {
        String email = inputEmail.getText().toString();
        String password_enc = inputPassword.getText().toString();
        String note = inputNote.getText().toString();
        String url = inputUrl.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(note) || TextUtils.isEmpty(url) || TextUtils.isEmpty(password_enc)) {
                //TODO: AFEK if text empty
        } else {

            DatabaseReference user_data_key = rootReference.child("users").child(UserId).child("data").push();
            String data_push_id = user_data_key.getKey();

            HashMap<String, Object> strongBoxEntry_text_body = new HashMap<>();
            strongBoxEntry_text_body.put("email", email);

            String password = null;
            try {
                password = AESCrypt.encrypt(password_enc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            strongBoxEntry_text_body.put("password", password);
            strongBoxEntry_text_body.put("note", note);
            strongBoxEntry_text_body.put("url", url);

            HashMap<String, Object> dataBodyDetails = new HashMap<>();
            dataBodyDetails.put( "users/" + UserId + "/data/" + data_push_id , strongBoxEntry_text_body);
            rootReference.updateChildren(dataBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null){
                        Log.e("Sending message", databaseError.getMessage());
                    }
                    inputNote.setText(""); //TODO: if writting fail
                }
            });
        }
    }
}
