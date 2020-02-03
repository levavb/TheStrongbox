package com.example.thestrongbox.Model;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.thestrongbox.Account.ViewAccountActivity;
import com.example.thestrongbox.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    Context context;
    ArrayList<Account> objects;
    private DatabaseReference UserDataInDatabaseReference;
    ArrayList<String> urls;

    public AccountAdapter(Context context, ArrayList<Account> listdata, DatabaseReference UserDataInDatabaseReference) {
        this.objects = listdata;
        this.context = context;
        this.UserDataInDatabaseReference = UserDataInDatabaseReference;

        urls = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.url_array)));
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_entry_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Account itemData = objects.get(position);

        int index = urls.indexOf(itemData.getUrl());
        if (index != -1) {
            String url = itemData.getUrl();
            String urlAfterOnePoint = url.substring(url.indexOf(".") + 1);
            String imageName = urlAfterOnePoint.substring(0, urlAfterOnePoint.indexOf("."));
            String uri = "@drawable/" + imageName + "_image";
            int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
            if (imageResource != 0) {
                Drawable res = context.getResources().getDrawable(imageResource, context.getTheme());
                holder.myImage.setImageDrawable(res);
            }
        }

        holder.tvEmail.setText(itemData.getUserName());
        holder.tvUrl.setText(Html.fromHtml("<a href=" + "" + ">" + itemData.getUrl() + "</a>"));
        holder.tvDate.setText(itemData.getDate());
        final String Surl = itemData.getUrl();
        holder.tvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Surl)) {
                    Intent intent_url = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + Surl));
                    context.startActivity(intent_url);
                }
            }

        });
        final String dataKey = itemData.getDataKey();
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewAccountActivity.class);
                intent.putExtra("entryId", dataKey);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public Account getItem(int position) {
        return objects.get(position);
    }

    public void updateList(List<Account> newList) {
        objects = new ArrayList<>();
        objects.addAll(newList);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;

        TextView tvEmail;
        TextView tvUrl;
        TextView tvDate;
        ImageView myImage;


        public ViewHolder(View itemView) {
            super(itemView);

            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvUrl = (TextView) itemView.findViewById(R.id.tvUrl);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            myImage = (ImageView) itemView.findViewById(R.id.account_image);
            itemLayout = itemView.findViewById(R.id.customLayout);
        }
    }
}



