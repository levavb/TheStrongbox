package com.example.thestrongbox.Home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.thestrongbox.About.AboutAppActivity;
import com.example.thestrongbox.Account.AddAccountActivity;
import com.example.thestrongbox.LoginReg.LoginActivity;
import com.example.thestrongbox.Model.Account;
import com.example.thestrongbox.Model.AccountAdapter;
import com.example.thestrongbox.Model.CryptoHash;
import com.example.thestrongbox.Model.MyBaseActivity;
import com.example.thestrongbox.ProfileSetting.SettingsActivity;
import com.example.thestrongbox.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends MyBaseActivity implements SearchView.OnQueryTextListener {

    private Toolbar mToolbar;
    private LinearLayout RootLayout;
    //Firebase
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;
    private DatabaseReference UserDataInDatabaseReference;


    RecyclerView Rv;
    ArrayList<Account> AccountList;
    AccountAdapter AccountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Rv = findViewById(R.id.recycler_view);
        Rv.setLayoutManager(new LinearLayoutManager(this));
        Rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        currentUser = mAuth.getCurrentUser();
        RootLayout = (LinearLayout) findViewById(R.id.main_layout);
        if (getIntent().getExtras() != null) {
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

            AccountList = new ArrayList<Account>();
            userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    addWellcomeBubble(dataSnapshot.child("user_name").getValue().toString());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("OnCancelled", "on");
                }
            });

            AccountAdapter = new AccountAdapter(this, AccountList, UserDataInDatabaseReference);
            Rv.setAdapter(AccountAdapter);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(Rv);
            setDatabaseListener();
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

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.profile_settings){
            Intent intent =  new Intent(MainActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.about_app){
            Intent intent =  new Intent(MainActivity.this, AboutAppActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        Intent AddAccountIntent = new Intent(MainActivity.this, AddAccountActivity.class);
        AddAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(AddAccountIntent);
    }

    private void setDatabaseListener() {
        UserDataInDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String SuserName = dataSnapshot.child("email").getValue().toString();
                String Snote = dataSnapshot.child("note").getValue().toString();
                String Surl = dataSnapshot.child("url").getValue().toString();
                String Sdate = dataSnapshot.child("date").getValue().toString();
                AccountList.add(new Account(dataSnapshot.getKey(), SuserName, Snote, Surl, Sdate));
                AccountAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Account account = AccountList.get(getItemIndexByDataKey(dataSnapshot.getKey()));
                account.setUserName(dataSnapshot.child("email").getValue().toString());
                account.setNote(dataSnapshot.child("note").getValue().toString());
                account.setUrl(dataSnapshot.child("url").getValue().toString());
                account.setDate(dataSnapshot.child("date").getValue().toString());
                AccountAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                AccountList.remove(getItemIndexByDataKey(dataSnapshot.getKey()));
                AccountAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndexByDataKey(String dataKey) {
        for (int i=0; i < AccountList.size() ; i++) {

            if (AccountList.get(i).getDataKey().equals(dataKey)) {
                return i;
            }
        }
        return -1;
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

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.delete_item_dailog, null);

            builder.setCancelable(true);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    AccountAdapter.notifyDataSetChanged();
                }
            });

            final int itemId = viewHolder.getAdapterPosition();
            builder.setPositiveButton("YES, Log out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteEntry(AccountList.get(itemId).getDataKey());
                }
            });
            builder.setView(view);
            builder.show();
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput = newText.toLowerCase();
        List<Account> newList = new ArrayList<>();

        for (Account entry : AccountList) {

            if (entry.getUrl().toLowerCase().contains(userInput)) {
                newList.add(entry);
            }

        }
        AccountAdapter.updateList(newList);
        return true;
    }
}

