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
import com.example.thestrongbox.Account.AddAccountActivity;
import com.example.thestrongbox.Account.UpdateAccountActivity;
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

        displayAllAccounts();
    }

    public void deleteEntry(String entryId) {
       UserDataInDatabaseReference.child(entryId).getRef().removeValue();
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

                        AddAccountToList(postSnapshot.getKey(), Semail, Spass, Snote, Surl);

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
        });
    }

    @SuppressLint({"ResourceAsColor", "WrongConstant"})
    private void AddAccountToList(final String dataKey, String Semail, String Spass, String Snote, String Surl) {

        LinearLayout new_window = new LinearLayout(this);
        new_window.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250));
        new_window.setBackgroundColor(R.color.white);
        new_window.setBackgroundResource(R.drawable.layout_bg);
        new_window.setOrientation(0);

        LinearLayout left_side = new LinearLayout(this);
        left_side.setLayoutParams(new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT));
        left_side.setOrientation(1);
        LinearLayout right_side = new LinearLayout(this);
        right_side.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT));
        right_side.setOrientation(1);

        new_window.addView(left_side);
        new_window.addView(right_side);

        TextView email_tv = new TextView(this);
        TextView pass_tv = new TextView(this);
        TextView note_tv = new TextView(this);
        TextView url_tv = new TextView(this);

        email_tv.setText(Semail);
        email_tv.setPadding(15,10,5,5);
        pass_tv.setText(Spass);
        pass_tv.setPadding(15,0,5,5);
        note_tv.setText(Snote);
        note_tv.setPadding(15,0,5,5);
        url_tv.setText(Surl);
        url_tv.setPadding(15,0,5,10);

        left_side.addView(email_tv);
        left_side.addView(pass_tv);
        left_side.addView(note_tv);
        left_side.addView(url_tv);

        ImageButton removeBtn = new ImageButton(this);
        removeBtn.setImageResource(R.drawable.ic_delete_forever_black_24dp);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        removeBtn.setLayoutParams(lp);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry(dataKey);
                Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
        right_side.addView(removeBtn);

        ImageButton editBtn = new ImageButton(this);
        editBtn.setImageResource(R.drawable.ic_edit_black_24dp);
        LinearLayout.LayoutParams lpE = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editBtn.setLayoutParams(lpE);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpdateAccountActivity.class);
                intent.putExtra("entryId", dataKey);
                startActivity(intent);
            }
        });
        right_side.addView(editBtn);

        RootLayout.addView(new_window);
    }
}
