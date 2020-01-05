package com.example.thestrongbox.Home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.AdapterView.OnItemClickListener;

import com.example.thestrongbox.About.AboutAppActivity;
import com.example.thestrongbox.Account.AddAccountActivity;
import com.example.thestrongbox.Account.UpdateAccountActivity;
import com.example.thestrongbox.LoginReg.LoginActivity;
import com.example.thestrongbox.Model.AESCrypt;
import com.example.thestrongbox.Model.Account;
import com.example.thestrongbox.Model.AccountAdapter;
import com.example.thestrongbox.Model.CryptoHash;
import com.example.thestrongbox.Model.MyBaseActivity;
import com.example.thestrongbox.ProfileSetting.SettingsActivity;
import com.example.thestrongbox.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends MyBaseActivity {

    private Toolbar mToolbar;
    private LinearLayout RootLayout;
    //Firebase
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;
    private DatabaseReference UserDataInDatabaseReference;


    ListView lv;
    ArrayList<Account> AccountList;
    AccountAdapter AccountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        lv = findViewById(R.id.listView);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {
                Log.d("LEVAV", "pass = ");
            }
        });
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Account account = AccountAdapter.getItem(position);
//                String Spass_enc = UserDataInDatabaseReference.child(account.getDataKey()).child("password").toString();
//                String Spass = "";
//                try {
//                    Log.d("LEVAV", "pass = " + Spass_enc);
//                    Log.d("LEVAV", "MasterKey = " + MasterKey);
//                    Log.d("LEVAV", "Spass = " + AESCrypt.decrypt(Spass_enc, MasterKey));
//                    Spass = AESCrypt.decrypt(Spass_enc, MasterKey);
//                } catch (Exception e) {
//                    Log.d("LEVAV", "printStackTrace");
//                    e.printStackTrace();
//                }
//
//                TextView tvPass = view.findViewById(R.id.tvPass);
//                tvPass.setText(Spass);
//            };
//        });

        currentUser = mAuth.getCurrentUser();
        RootLayout = (LinearLayout) findViewById(R.id.main_layout);
        Log.d("LEVAV","main activity on create");
        if (getIntent().getExtras() != null) {
            Log.d("LEVAV","intent is not null");
            try {
                String pass = getIntent().getStringExtra("USER_PASS");
                MasterKey = CryptoHash.getSha256(pass);
                pass = "";
                getIntent().putExtra("USER_PASS", "");
                getIntent().removeExtra("USER_PASS");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        if (currentUser != null){
            String user_uID = mAuth.getCurrentUser().getUid();

            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_uID);
            UserDataInDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_uID).child("data");

            displayAllAccounts();
        }

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
                    logOutUser();
                }
            });
            builder.setView(view);
            builder.show();
        }
        return true;
    }

    public void logOutUser() {
        MasterKey = CryptoHash.emptySHA();
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
        AccountList = new ArrayList<Account>();
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    addWellcomeBubble(dataSnapshot.child("user_name").getValue().toString());
                    AccountList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.child("data").getChildren()) {

                        String SuserName = postSnapshot.child("email").getValue().toString();
                        String Spass_enc = postSnapshot.child("password").getValue().toString();
                        String Snote = postSnapshot.child("note").getValue().toString();
                        String Surl = postSnapshot.child("url").getValue().toString();
                        String Sdate = postSnapshot.child("date").getValue().toString();

                        String Spass = "";
                        AccountList.add(new Account(postSnapshot.getKey(), SuserName, Snote, Spass, Surl, Sdate));
                    }
                    AccountAdapter = new AccountAdapter(MainActivity.this,0,0, AccountList, UserDataInDatabaseReference);
                    lv.setAdapter(AccountAdapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("OnCancelled", "on");
                }
        });

    }

    @SuppressLint({"ResourceAsColor", "WrongConstant"})
    private void addWellcomeBubble(String UserName) {

        TextView welcome_tv = findViewById(R.id.WellcomeBubble);
        TextView part_day_tv = findViewById(R.id.DayPart);

        welcome_tv.setText("Wellcome " + UserName);
        part_day_tv.setText(getBlessingDayText());

    }

    private String getBlessingDayText() {

        int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String blessingText = "";
        if ( cur_hour >= 6 && cur_hour < 12 ) {
            blessingText = "Good Morning";
        } else if ( cur_hour >= 12 && cur_hour < 18) {
            blessingText = "Good Afternoon";
        } else if ( cur_hour >= 18 && cur_hour < 22) {
            blessingText = "Good Evening";
        } else if ( (cur_hour >= 22 && cur_hour <= 24) | (cur_hour >= 0 && cur_hour < 6)){
            blessingText = "Good Night";
        }

        return blessingText;
    }
}

