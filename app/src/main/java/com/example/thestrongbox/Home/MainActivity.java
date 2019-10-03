package com.example.thestrongbox.Home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thestrongbox.About.AboutAppActivity;
import com.example.thestrongbox.AddAccount.AddAccountActivity;
import com.example.thestrongbox.LoginReg.LoginActivity;
import com.example.thestrongbox.ProfileSetting.SettingsActivity;
import com.example.thestrongbox.R;
import com.example.thestrongbox.Search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private LinearLayout RootLayout;
    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;
    private DatabaseReference UserDataInDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String user_id = mAuth.getCurrentUser().getUid();
        UserDataInDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("data");

        if (currentUser != null){
            String user_uID = mAuth.getCurrentUser().getUid();

            userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user_uID);
        }
        RootLayout = (LinearLayout) findViewById(R.id.main_layout);

//        displayAllAccounts();
        AddAccountToList("Semail", "Spass", "Snote", "Surl");
    }

    // tool bar action menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_search){
            Intent intent =  new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.profile_settings){
            Intent intent =  new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.about_app){
            Intent intent =  new Intent(MainActivity.this, AboutAppActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.main_logout){
            // Custom Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.logout_dailog, null);

            ImageButton imageButton = view.findViewById(R.id.logoutImg);
            imageButton.setImageResource(R.drawable.logout);
            builder.setCancelable(true);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("YES, Log out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (currentUser != null){
                        userDatabaseReference.child("active_now").setValue(ServerValue.TIMESTAMP);
                    }
                    mAuth.signOut();
                    logOutUser();
                }
            });
            builder.setView(view);
            builder.show();
        }
        return true;
    }


    public void logOutUser() {
        mAuth.signOut();
        Intent loginIntent =  new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    public void MoveToPageAddAccount(View view) {

        Intent intent = new Intent(MainActivity.this, AddAccountActivity.class);
        startActivity(intent);
    }

    private void displayAllAccounts() {

        UserDataInDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String Semail = postSnapshot.child("email").getValue().toString();
                    String Spass = postSnapshot.child("password").getValue().toString();
                    String Snote = postSnapshot.child("note").getValue().toString();
                    String Surl = postSnapshot.child("url").getValue().toString();

                    AddAccountToList(Semail, Spass, Snote, Surl);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @SuppressLint({"ResourceAsColor", "WrongConstant"})
    private void AddAccountToList(String Semail, String Spass, String Snote, String Surl) {

        LinearLayout new_window = new LinearLayout(this);
        new_window.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200));
        new_window.setBackgroundColor(R.color.white);
        new_window.setBackgroundResource(R.drawable.layout_bg);
        new_window.setOrientation(1);
        TextView email_tv = new TextView(this);
        TextView pass_tv = new TextView(this);
        TextView note_tv = new TextView(this);
        TextView url_tv = new TextView(this);

        email_tv.setText(Semail);
        pass_tv.setText(Spass);
        note_tv.setText(Snote);
        url_tv.setText(Surl);

        new_window.addView(email_tv);
        new_window.addView(pass_tv);
        new_window.addView(note_tv);
        new_window.addView(url_tv);

        RootLayout.addView(new_window);
    }
}
