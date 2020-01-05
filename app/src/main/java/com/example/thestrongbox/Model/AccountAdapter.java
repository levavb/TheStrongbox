package com.example.thestrongbox.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.thestrongbox.Account.UpdateAccountActivity;
import com.example.thestrongbox.Home.MainActivity;
import com.example.thestrongbox.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {
    Context context;
    List<Account> objects;
    private DatabaseReference UserDataInDatabaseReference;

    public AccountAdapter(Context context, int resource, int textViewResourceId, List<Account> objects, DatabaseReference UserDataInDatabaseReference) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.UserDataInDatabaseReference = UserDataInDatabaseReference;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.list_entry_layout,parent,false);

        TextView tvEmail = (TextView)view.findViewById(R.id.tvEmail);
        TextView tvPass  = (TextView)view.findViewById(R.id.tvPass);
        TextView tvNote  = (TextView)view.findViewById(R.id.tvNote);
        TextView tvUrl   = (TextView)view.findViewById(R.id.tvUrl);
        TextView tvDate = (TextView)view.findViewById(R.id.tvDate);

        Account temp = objects.get(position);

        tvEmail.setText(Html.fromHtml("<strong><em>" +"Email/User Name: " + "</em></strong>" + temp.getUserName()));
        tvPass.setText(Html.fromHtml("<strong><em>" +"Password: " + "</em></strong>" + temp.getPassword()));
        tvNote.setText(Html.fromHtml("<strong><em>" +"Note: " + "</em></strong>" + temp.getNote()));
        tvUrl.setText(Html.fromHtml("<strong><em>" + "Url: " + "</em></strong>"+ "<a href="+ "" + ">" + temp.getUrl()+ "</a>"));
        tvDate.setText(temp.getDate());
        final String Surl = temp.getUrl();
        tvUrl.setFocusable(false);
        tvUrl.setFocusableInTouchMode(false);
        tvUrl.setClickable(false);
//        tvUrl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(Surl)) {
//                    Intent intent_url = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + Surl));
//                    context.startActivity(intent_url);
//                }
//            }
//
//        });
        final String dataKey = temp.getDataKey();
        ImageButton editBtn = view.findViewById(R.id.EditBtn);
        editBtn.setFocusable(false);
        editBtn.setFocusableInTouchMode(false);
        editBtn.setClickable(false);
//        editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, UpdateAccountActivity.class);
//                intent.putExtra("entryId", dataKey);
//                context.startActivity(intent);
//            }
//        });
        ImageButton removeBtn = view.findViewById(R.id.RemoveBtn);
        removeBtn.setFocusable(false);
        removeBtn.setClickable(false);
        removeBtn.setFocusableInTouchMode(false);
//        removeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UserDataInDatabaseReference.child(dataKey).getRef().removeValue();
//            }
//        });

        return view;
    }

}
