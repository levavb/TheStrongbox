package com.example.thestrongbox.Account;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thestrongbox.Class.AutoSuggestAdapter;
import com.example.thestrongbox.Home.MainActivity;
import com.example.thestrongbox.Model.AESCrypt;
import com.example.thestrongbox.Model.MyBaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.thestrongbox.R;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UpdateAccountActivity extends MyBaseActivity {

    private static final String TAG = "UpdateAccountActivity";

    private DatabaseReference UpdateDatabaseReference;

    private String UserId;
    private Button editButton;
    private EditText inputEmail, inputPassword, inputNote;
    AutoCompleteTextView inputUrl;
    private Toolbar aToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_template);

        aToolbar = findViewById(R.id.account_toolbar);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setTitle("Update Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String user_id = mAuth.getCurrentUser().getUid();
        String entry_id = getIntent().getStringExtra("entryId");
        UpdateDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("data").child(entry_id);


        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputNote = findViewById(R.id.inputNote);
        inputUrl = findViewById(R.id.inputUrl);
        editButton = findViewById(R.id.AddButton);

        // Get the string array
        List<String> urls = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.url_array)));
        // Create the adapter and set it to the AutoCompleteTextView
        AutoSuggestAdapter adapter = new AutoSuggestAdapter(this, android.R.layout.simple_list_item_1, urls);
        inputUrl.setAdapter(adapter);
        inputUrl.setThreshold(1);

        setListenerToCopyImage();

        UpdateDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve data from db
                String note = dataSnapshot.child("note").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String password = dataSnapshot.child("password").getValue().toString();
                String url = dataSnapshot.child("url").getValue().toString();

                inputEmail.setText(email);
                try {
                    inputPassword.setText(AESCrypt.decrypt(password, MasterKey));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                inputNote.setText(note);
                inputUrl.setText(url);
                setImageAccountByUrl(url);
                editButton.setText("Edit");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /** Edit information */
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = inputNote.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String url = inputUrl.getText().toString();

                saveInformation(email, note, password, url);
            }
        });


    }

    private void saveInformation(String email, String note, String password, String url) {

        if (TextUtils.isEmpty(email)){
            Toast.makeText(UpdateAccountActivity.this, "Oops! your Email can't be empty",Toast.LENGTH_SHORT).show();
        } else if (email.length() > 25) {
            Toast.makeText(UpdateAccountActivity.this, "Your Email/User Name should be 1 to 25 numbers of characters.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(UpdateAccountActivity.this, "Oops! your Password can't be empty",Toast.LENGTH_SHORT).show();
        } else if (password.length() > 30){
            Toast.makeText(UpdateAccountActivity.this, "Your Password should be 1 to 30 numbers of characters.",Toast.LENGTH_SHORT).show();
        } else if (url.length() > 35){
            Toast.makeText(UpdateAccountActivity.this, "Your Url should be 1 to 35 numbers of characters.",Toast.LENGTH_SHORT).show();
        } else {

            String password_enc = null;
            try {
                password_enc = AESCrypt.encrypt(password, MasterKey);
            } catch (Exception e) {
                e.printStackTrace();
            }

            UpdateDatabaseReference.child("date").setValue(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
            UpdateDatabaseReference.child("email").setValue(email);
            UpdateDatabaseReference.child("url").setValue(url);
            UpdateDatabaseReference.child("note").setValue(note);
            UpdateDatabaseReference.child("password").setValue(password_enc)
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
            Intent mainIntent = new Intent(UpdateAccountActivity.this, MainActivity.class);
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

    void setImageAccountByUrl(String url) {
        ImageView imageView = findViewById(R.id.ImageAccount);
        ArrayList<String> urls = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.url_array)));
        int index = urls.indexOf(url);
        if (index != -1) {
            String urlAfterOnePoint = url.substring(url.indexOf(".") + 1);
            String imageName = urlAfterOnePoint.substring(0, urlAfterOnePoint.indexOf("."));
            String uri = "@drawable/" + imageName + "_image";
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            if (imageResource != 0) {
                Drawable res = getResources().getDrawable(imageResource, getTheme());
                imageView.setImageDrawable(res);
            }
        }
    }
}
