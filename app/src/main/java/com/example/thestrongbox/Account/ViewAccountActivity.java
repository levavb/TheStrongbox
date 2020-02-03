package com.example.thestrongbox.Account;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.thestrongbox.Model.AESCrypt;
import com.example.thestrongbox.Model.MyBaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.thestrongbox.R;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;


public class ViewAccountActivity extends MyBaseActivity {

    private static final String TAG = "UpdateAccountActivity";

    private DatabaseReference UpdateDatabaseReference;
    private String entry_id;
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
        getSupportActionBar().setTitle("Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String user_id = mAuth.getCurrentUser().getUid();
        entry_id = getIntent().getStringExtra("entryId");
        UpdateDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("data").child(entry_id);


        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputUrl = findViewById(R.id.inputUrl);
        inputNote = findViewById(R.id.inputNote);
        editButton = findViewById(R.id.AddButton);

        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
        inputUrl.setInputType(InputType.TYPE_NULL);
        inputUrl.setFocusable(false);
        inputNote.setEnabled(false);

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
                inputUrl.setText(Html.fromHtml("<a href=" + "" + ">" + url + "</a>"));
                final String Surl = url;
                inputUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(Surl)) {
                            Intent intent_url = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + Surl));
                            startActivity(intent_url);
                        }
                    }

                });
                editButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // tool bar action menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_edit){
            Intent intent =  new Intent(ViewAccountActivity.this, UpdateAccountActivity.class);
            intent.putExtra("entryId", entry_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return true;
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

