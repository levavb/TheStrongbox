package com.example.thestrongbox.Account;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.thestrongbox.Class.AutoSuggestAdapter;
import com.example.thestrongbox.Home.MainActivity;
import com.example.thestrongbox.Model.AESCrypt;
import com.example.thestrongbox.Model.MyBaseActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.thestrongbox.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddAccountActivity extends MyBaseActivity {


    private DatabaseReference rootReference;
    private FirebaseUser User;
    private String UserId;
    private Button addButton;
    private EditText inputEmail, inputPassword, inputNote;
    AutoCompleteTextView inputUrl;

    private Toolbar aToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_template);
        aToolbar = findViewById(R.id.account_toolbar);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setTitle("Add Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        rootReference = FirebaseDatabase.getInstance().getReference();
        User = mAuth.getCurrentUser();
        if (User != null) {UserId = User.getUid();}

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputNote = findViewById(R.id.inputNote);
        inputUrl = findViewById(R.id.inputUrl);
        addButton = findViewById(R.id.AddButton);

        // Get the string array
        List<String> urls = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.url_array)));
        // Create the adapter and set it to the AutoCompleteTextView
        AutoSuggestAdapter adapter = new AutoSuggestAdapter(this, android.R.layout.simple_list_item_1, urls);
        inputUrl.setAdapter(adapter);
        inputUrl.setThreshold(1);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAccount();
            }
        });
        setListenerToCopyImage();
    }

    public void uploadAccount() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String note = inputNote.getText().toString();
        String url = inputUrl.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(AddAccountActivity.this, "Oops! your Email can't be empty",Toast.LENGTH_SHORT).show();
        } else if (email.length() > 25) {
            Toast.makeText(AddAccountActivity.this, "Your Email/User Name should be 1 to 25 numbers of characters.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
                Toast.makeText(AddAccountActivity.this, "Oops! your Password can't be empty",Toast.LENGTH_SHORT).show();
        } else if (password.length() > 30){
            Toast.makeText(AddAccountActivity.this, "Your Password should be 1 to 30 numbers of characters.",Toast.LENGTH_SHORT).show();
        } else if (url.length() > 35){
            Toast.makeText(AddAccountActivity.this, "Your Url should be 1 to 35 numbers of characters.",Toast.LENGTH_SHORT).show();
        } else {

            DatabaseReference user_data_key = rootReference.child("users").child(UserId).child("data").push();
            String data_push_id = user_data_key.getKey();

            HashMap<String, Object> strongBoxEntry_text_body = new HashMap<>();


            String password_enc = null;
            try {
                password_enc = AESCrypt.encrypt(password, MasterKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            strongBoxEntry_text_body.put("email", email);
            strongBoxEntry_text_body.put("password", password_enc);
            strongBoxEntry_text_body.put("note", note);
            strongBoxEntry_text_body.put("url", url);
            strongBoxEntry_text_body.put("date", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

            HashMap<String, Object> dataBodyDetails = new HashMap<>();
            dataBodyDetails.put( "users/" + UserId + "/data/" + data_push_id , strongBoxEntry_text_body);
            rootReference.updateChildren(dataBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null){
                        Log.e("Sending message", databaseError.getMessage());
                    }
                }
            });
            Intent mainIntent = new Intent(AddAccountActivity.this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
        }
    }

    void setListenerToCopyImage() {

        ImageButton emailCopyBtn = findViewById(R.id.email_copy_btn);
        emailCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", inputEmail.getText().toString());
                clipboard.setPrimaryClip(clip);
            }

        });

        ImageButton passCopyBtn = findViewById(R.id.pass_copy_btn);
        passCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", inputPassword.getText().toString());
                clipboard.setPrimaryClip(clip);
            }

        });

        ImageButton urlCopyBtn = findViewById(R.id.url_copy_btn);
        urlCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", inputUrl.getText().toString());
                clipboard.setPrimaryClip(clip);
            }

        });

        ImageButton noteCopyBtn = findViewById(R.id.note_copy_btn);
        noteCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", inputNote.getText().toString());
                clipboard.setPrimaryClip(clip);
            }

        });

    }
}
