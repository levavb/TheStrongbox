package com.example.thestrongbox.Home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

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

        currentUser = mAuth.getCurrentUser();
        RootLayout = (LinearLayout) findViewById(R.id.main_layout);
        try {
            String pass = getIntent().getStringExtra("USER_PASS");
            MasterKey = CryptoHash.getSha256(pass);
            pass = "";
            getIntent().removeExtra("USER_PASS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
//        startActivityForResult(intent,1);
        startActivity(intent);
        finish();
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


                        String Spass = null;
                        try {
                            Spass = AESCrypt.decrypt(Spass_enc, MasterKey);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        AccountList.add(new Account( SuserName, Snote, Spass, Surl, Sdate));
                    }
                    AccountAdapter = new AccountAdapter(MainActivity.this,0,0,AccountList);
                    lv.setAdapter(AccountAdapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("OnCancelled", "on");
                }
        });
    }

    @SuppressLint({"ResourceAsColor", "WrongConstant"})
    private void AddAccountToList(final String dataKey, String Semail, String Spass, String Snote, final String Surl, String Sdate) {

        LinearLayout new_window = new LinearLayout(this);
        LinearLayout.LayoutParams wLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
        wLayoutParams.setMargins(0,15,0,0);
        new_window.setLayoutParams(wLayoutParams);

        new_window.setBackgroundColor(R.color.white);
        new_window.setBackgroundResource(R.drawable.layout_bg);
        new_window.setOrientation(0);

        LinearLayout left_side = new LinearLayout(this);
        left_side.setLayoutParams(new LinearLayout.LayoutParams(850, LinearLayout.LayoutParams.MATCH_PARENT));
        left_side.setOrientation(1);
        LinearLayout right_side = new LinearLayout(this);
        LinearLayout.LayoutParams rLayoutParams = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT);
        rLayoutParams.setMargins(0,10,0,0);
        right_side.setLayoutParams(rLayoutParams);
        right_side.setOrientation(1);

        new_window.addView(left_side);
        new_window.addView(right_side);

        TextView email_tv = new TextView(this);
        TextView pass_tv = new TextView(this);
        TextView note_tv = new TextView(this);
        TextView url_tv = new TextView(this);
        TextView date_tv = new TextView(this);

        email_tv.setText(Html.fromHtml("<strong><em>" +"Email/User Name: " + "</em></strong>" + Semail));
        email_tv.setPadding(15,10,5,5);
        email_tv.setTextIsSelectable(true);
        pass_tv.setText(Html.fromHtml("<strong><em>" +"Password: " + "</em></strong>" + Spass));
        pass_tv.setPadding(15,0,5,5);
        pass_tv.setTextIsSelectable(true);
        note_tv.setText(Html.fromHtml("<strong><em>" +"Note: " + "</em></strong>" + Snote));
        note_tv.setPadding(15,0,5,5);
        note_tv.setTextIsSelectable(true);
        url_tv.setText(Html.fromHtml("<strong><em>" + "Url: " + "</em></strong>"+ "<a href="+ "" + ">" + Surl + "</a>"));
        url_tv.setPadding(15,0,5,10);
        url_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Surl)) {
                    Intent intent_url = new Intent(Intent.ACTION_VIEW,Uri.parse("http://" + Surl));
                    startActivity(intent_url);
                }
            }
        });


        left_side.addView(email_tv);
        left_side.addView(pass_tv);
        left_side.addView(url_tv);
        left_side.addView(note_tv);

        ImageButton removeBtn = new ImageButton(this);
        removeBtn.setImageResource(R.drawable.remove_icon);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(95,0,0,0);
        removeBtn.setLayoutParams(lp);
        removeBtn.setBackgroundColor(Color.WHITE);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry(dataKey);
            }
        });

        ImageButton editBtn = new ImageButton(this);
        editBtn.setImageResource(R.drawable.edit_icon);
        LinearLayout.LayoutParams lpE = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpE.setMargins(95,0,0,0);
        editBtn.setLayoutParams(lpE);
        editBtn.setBackgroundColor(Color.WHITE);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpdateAccountActivity.class);
                intent.putExtra("entryId", dataKey);
                startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0) //come from edit mode
//        {
//            if (resultCode == RESULT_OK) {
//                String model = data.getExtras().getString("model");
//                String passengers = data.getExtras().getString("passengers");
//                String color = data.getExtras().getString("color");
//                Bitmap bitmap = Helper.byteArrayToBitmap(data.getExtras().getByteArray("bitmap"));
//                String speed = data.getExtras().getString("speed");
//
//                lastSelected.setModel(model);
//                lastSelected.setPassengers(Integer.valueOf(passengers));
//                lastSelected.setColor(color);
//                lastSelected.setBitmap(bitmap);
//                lastSelected.setSpeed(Integer.valueOf(speed));
//                carAdapter.notifyDataSetChanged();
//                Toast.makeText(this, "data saved", Toast.LENGTH_LONG).show();
//            } else {
//                if (resultCode == RESULT_CANCELED)
//                    Toast.makeText(this, "user cancel", Toast.LENGTH_LONG).show();
//
//            }
//        }
        Toast.makeText(this,"result activity",Toast.LENGTH_LONG).show();

//            if(resultCode==RESULT_OK)
//            {
//                String SuserName = data.getExtras().getString("email");
//                String Spass_enc = data.getExtras().getString("password");
//                String Snote = data.getExtras().getString("note");
//                String Surl = data.getExtras().getString("url");
//                String Sdate = data.getExtras().getString("date");
//
//
//                String Spass = null;
//                try {
//                    Spass = AESCrypt.decrypt(Spass_enc, MasterKey);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                AccountAdapter.add(new Account( SuserName, Snote, Spass, Surl, Sdate));
//
//                AccountAdapter.notifyDataSetChanged();
//                Toast.makeText(this,"data saved",Toast.LENGTH_LONG).show();
//            }
//            else if(resultCode==RESULT_CANCELED)
//            {
//                Toast.makeText(this,"user cancel",Toast.LENGTH_LONG).show();
//            }

    }


}

