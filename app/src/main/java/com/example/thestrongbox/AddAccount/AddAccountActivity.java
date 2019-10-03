package com.example.thestrongbox.AddAccount;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.thestrongbox.R;

public class AddAccountActivity extends AppCompatActivity {

    private Toolbar aToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        aToolbar = findViewById(R.id.add_account_toolbar);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setTitle("Add Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
