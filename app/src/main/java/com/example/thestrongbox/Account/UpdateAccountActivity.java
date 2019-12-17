package com.example.thestrongbox.Account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thestrongbox.Class.AutoSuggestAdapter;
import com.example.thestrongbox.Home.MainActivity;
import com.example.thestrongbox.Model.AESCrypt;
import com.example.thestrongbox.Model.CryptoHash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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


public class UpdateAccountActivity extends AppCompatActivity {

    private static final String TAG = "UpdateAccountActivity";

    private DatabaseReference UpdateDatabaseReference;
    private FirebaseAuth Auth;

    private String UserId;
    private Button editButton;
    private EditText inputEmail, inputPassword, inputNote;
    AutoCompleteTextView inputUrl;
    private Toolbar aToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        aToolbar = findViewById(R.id.add_account_toolbar);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setTitle("Update Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Auth = FirebaseAuth.getInstance();
        String user_id = Auth.getCurrentUser().getUid();
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

        UpdateDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve data from db
                String note = dataSnapshot.child("note").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String password = dataSnapshot.child("password").getValue().toString();
                String url = dataSnapshot.child("url").getValue().toString();

                inputEmail.setText(email);
                try {
                    byte[] user_sha = getIntent().getByteArrayExtra("USER_SHA");
                    inputPassword.setText(AESCrypt.decrypt(password, user_sha));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                inputNote.setText(note);
                inputUrl.setText(url);
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
            Log.d("LEVAV","NUM PASS: " + password.length());
            Toast.makeText(UpdateAccountActivity.this, "Your Password should be 1 to 30 numbers of characters.",Toast.LENGTH_SHORT).show();
        } else if (url.length() > 35){
            Toast.makeText(UpdateAccountActivity.this, "Your Url should be 1 to 35 numbers of characters.",Toast.LENGTH_SHORT).show();
        } else {

            String password_enc = null;
            try {
                byte[] user_sha = getIntent().getByteArrayExtra("USER_SHA");
                password_enc = AESCrypt.encrypt(password, user_sha);
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
            getIntent().putExtra("USER_SHA","");
            getIntent().removeExtra("USER_SHA");
            Intent mainIntent = new Intent(UpdateAccountActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }
    }

}
