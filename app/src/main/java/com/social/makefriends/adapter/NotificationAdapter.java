package com.social.makefriends.adapter;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.social.makefriends.R;
import com.social.makefriends.model.NotificationModel;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder>{
    private List<NotificationModel> arrayList;

    public NotificationAdapter(List<NotificationModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list,parent,false);
        return new NotificationAdapter.NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        NotificationModel notificationModel = arrayList.get(position);
        holder.notificationMsg.setText((Html.fromHtml("<b> <font color='#0645AD'>" + notificationModel.getSenderName()
                + "</b></font>"+" "+notificationModel.getNotificationMsg())));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class NotificationHolder extends RecyclerView.ViewHolder{
        TextView notificationMsg;
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            notificationMsg = itemView.findViewById(R.id.notification_msg);
        }
    }
}
