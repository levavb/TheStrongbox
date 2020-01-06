package com.example.thestrongbox.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thestrongbox.Account.UpdateAccountActivity;
import com.example.thestrongbox.Home.MainActivity;
import com.example.thestrongbox.R;
import com.google.firebase.database.DatabaseReference;



import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;



public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    Context context;
    ArrayList<Account> objects;
    private DatabaseReference UserDataInDatabaseReference;

    public AccountAdapter(Context context, ArrayList<Account> listdata, DatabaseReference UserDataInDatabaseReference) {
        this.objects = listdata;
        this.context = context;
        this.UserDataInDatabaseReference = UserDataInDatabaseReference;
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_entry_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Account itemData = objects.get(position);


        holder.tvEmail.setText(Html.fromHtml("<strong><em>" + "Email/User Name: " + "</em></strong>" + itemData.getUserName()));
        holder.tvPass.setText(Html.fromHtml("<strong><em>" + "Password: " + "</em></strong>" + "******"));
        holder.tvNote.setText(Html.fromHtml("<strong><em>" + "Note: " + "</em></strong>" + itemData.getNote()));
        holder.tvUrl.setText(Html.fromHtml("<strong><em>" + "Url: " + "</em></strong>" + "<a href=" + "" + ">" + itemData.getUrl() + "</a>"));
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
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateAccountActivity.class);
                intent.putExtra("entryId", dataKey);
                context.startActivity(intent);
            }
        });
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataInDatabaseReference.child(dataKey).getRef().removeValue();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageButton editBtn;
        ImageButton removeBtn;

        TextView tvEmail;
        TextView tvPass;
        TextView tvNote;
        TextView tvUrl;
        TextView tvDate;



        public ViewHolder(View itemView) {
            super(itemView);

            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvPass = (TextView) itemView.findViewById(R.id.tvPass);
            tvNote = (TextView) itemView.findViewById(R.id.tvNote);
            tvUrl = (TextView) itemView.findViewById(R.id.tvUrl);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);

            editBtn = itemView.findViewById(R.id.EditBtn);
            removeBtn = itemView.findViewById(R.id.RemoveBtn);
            itemLayout = itemView.findViewById(R.id.customLayout);
        }
    }
}



