package com.example.thestrongbox.Account;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.thestrongbox.R;

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
        UpdateDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);




        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputNote = findViewById(R.id.inputNote);
        inputUrl = findViewById(R.id.inputUrl);
        editButton = findViewById(R.id.AddButton);


        inputEmail.setText();
        inputPassword.setText();
        inputNote.setText();
        inputUrl.setText();
        editButton.setText("Edit");
//
//
//        status_from_input = findViewById(R.id.input_status);
//        progressDialog = new ProgressDialog(this);
//
//        mToolbar = findViewById(R.id.update_status_appbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Update Status");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        // back on previous activity
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick : navigating back to 'SettingsActivity.class' ");
//                finish();
//            }
//        });
//
//        /**
//         * retrieve previous profile status from SettingsActivity
//         */
//        String previousStatus = getIntent().getExtras().get("ex_status").toString();
//        status_from_input.setText(previousStatus);
//        status_from_input.setSelection(status_from_input.getText().length());
    } //ending onCreate

//    // tool bar Status update done- menu button
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.update_status_done_menu, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//        if (item.getItemId() == R.id.status_update_done){
//            String new_status = status_from_input.getText().toString();
//            changeProfileStatus(new_status);
//        }
//        return true;
//    }

    private void changeProfileStatus(String new_status) {
        if (TextUtils.isEmpty(new_status)){
//            SweetToast.warning(getApplicationContext(), "Please write something about status");
        } else {
            progressDialog.setMessage("Updating status...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            UpdateDatabaseReference.child("user_status").setValue(new_status)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                finish();
                            } else {
//                                SweetToast.warning(getApplicationContext(), "Error occurred: failed to update.");
                            }
                        }
                    });
        }
     }

}
