package com.example.thestrongbox.Account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thestrongbox.Home.MainActivity;
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



public class UpdateAccountActivity extends AppCompatActivity {

    private static final String TAG = "UpdateAccountActivity";

    private DatabaseReference UpdateDatabaseReference;
    private FirebaseAuth Auth;

    private String UserId;
    private Button editButton;
    private EditText inputEmail, inputPassword, inputNote, inputUrl;
    private Toolbar aToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        aToolbar = findViewById(R.id.add_account_toolbar);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setTitle("Update Account");

        Auth = FirebaseAuth.getInstance();
        String user_id = Auth.getCurrentUser().getUid();
        String entry_id = getIntent().getStringExtra("entryId");
        UpdateDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("data").child(entry_id);


        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputNote = findViewById(R.id.inputNote);
        inputUrl = findViewById(R.id.inputUrl);
        editButton = findViewById(R.id.AddButton);


        UpdateDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve data from db
                String note = dataSnapshot.child("note").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String password = dataSnapshot.child("password").getValue().toString();
                String url = dataSnapshot.child("url").getValue().toString();

                inputEmail.setText(email);
                inputPassword.setText(password);
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
                Intent mainIntent = new Intent(UpdateAccountActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });


    }

    private void saveInformation(String email, String note, String password, String url) {
        Toast.makeText(UpdateAccountActivity.this, "hahahah2",Toast.LENGTH_SHORT).show(); //TODO: Change text
        if (TextUtils.isEmpty(email)){
            Toast.makeText(UpdateAccountActivity.this, "Oops! your name can't be empty",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(note)){
            Toast.makeText(UpdateAccountActivity.this, "Your name should be 3 to 40 numbers of characters",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(UpdateAccountActivity.this, "Your mobile number is required.",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(url)){
            Toast.makeText(UpdateAccountActivity.this, "Sorry! your mobile number is too short",Toast.LENGTH_SHORT).show();
        } else {
            UpdateDatabaseReference.child("email").setValue(email);
            UpdateDatabaseReference.child("url").setValue(url);
            UpdateDatabaseReference.child("note").setValue(note);
            UpdateDatabaseReference.child("password").setValue(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //TODO: Edit completed
                            Log.d("UpdateDB:", "Completed");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO: Edit Failed
                    Log.d("UpdateDB:", "Failed");
                }
            });
        }
    }

}