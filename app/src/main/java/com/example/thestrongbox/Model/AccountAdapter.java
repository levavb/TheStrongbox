package com.example.thestrongbox.Model;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thestrongbox.R;

import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {
    Context context;
    List<Account> objects;


    public AccountAdapter(Context context, int resource, int textViewResourceId, List<Account> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
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

        return view;
    }

}
