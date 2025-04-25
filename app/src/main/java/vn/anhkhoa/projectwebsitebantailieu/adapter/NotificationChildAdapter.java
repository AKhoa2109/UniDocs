package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.database.NotificationDao;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;


public class NotificationChildAdapter extends RecyclerView.Adapter<NotificationChildAdapter.ViewHolder>{
    private Context mContext;
    private List<NotificationDto> mNotifications;
    private NotificationDao notificationDao;

    public NotificationChildAdapter(Context context, List<NotificationDto> notifications) {
        this.mContext = context;
        this.mNotifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_notification_child, parent, false);
        notificationDao = new NotificationDao(mContext);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationDto notification = mNotifications.get(position);
        Glide.with(mContext).load("https://cdn-icons-png.freepik.com/256/9187/9187529.png?semt=ais_hybrid").into(holder.ivNotificationIcon);
        holder.tvNotificationTitle.setText(notification.getTitle());
        holder.tvNotificationContent.setText(notification.getContent());
        holder.tvNotificationTime.setText(notification.getCreatedAt().toString());
        if (notification.isRead()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_notification_read);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_notification_unread);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification.setRead(true);
                notificationDao.updateIsRead(notification.getNotiId(), true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotifications != null ? mNotifications.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNotificationIcon;
        TextView tvNotificationTitle,tvNotificationContent,tvNotificationTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNotificationIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationContent = itemView.findViewById(R.id.tvNotificationContent);
            tvNotificationTime = itemView.findViewById(R.id.tvNotificationTime);
        }
    }
}

