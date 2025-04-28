package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.database.NotificationDao;
import vn.anhkhoa.projectwebsitebantailieu.enums.NotificationType;
import vn.anhkhoa.projectwebsitebantailieu.fragment.NotificationChildFragment;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationGroup;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private Context ctx;
    private List<NotificationGroup> groups;
    private NotificationDao notificationDao;

    private SessionManager sessionManager;
    public NotificationAdapter(Context ctx, List<NotificationGroup> groups) {
        this.ctx    = ctx;
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx)
                .inflate(R.layout.item_notification_group, parent, false);
        notificationDao = new NotificationDao(ctx);
        sessionManager = SessionManager.getInstance(ctx);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        NotificationGroup g = groups.get(pos);
        Integer count = notificationDao.getCountByTypeAndUserId(g.getType(),sessionManager.getUser().getUserId());
        h.tvTitle.setText(g.getType().name());
        h.tvCount.setText(String.valueOf(count));
        h.ivIcon.setImageResource(iconForType(g.getType()));
        h.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ctx instanceof MainActivity){
                    ((MainActivity) ctx).openNotificationChildFragment(g.getItems(), g.getType());
                }
            }
        });
    }

    @Override public int getItemCount() { return groups.size(); }

    private int iconForType(NotificationType type) {
        switch(type) {
            case CART:       return R.drawable.cart_notifi;
            case ORDER:      return R.drawable.order_notifi;
            case PAYMENT:    return R.drawable.payment_notifi;
            case DOWNLOAD:   return R.drawable.ic_download;
            case PROMOTION:  return R.drawable.promotion_notifi;
            case CHAT:       return R.drawable.chat_notifi;
            case REVIEW:     return R.drawable.review_notifi;
            default:         return R.drawable.ic_launcher_background;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon, ivArrow;
        TextView tvTitle, tvCount;

        ViewHolder(View v) {
            super(v);
            ivIcon         = v.findViewById(R.id.ivGroupIcon);
            ivArrow        = v.findViewById(R.id.ivGroupArrow);
            tvTitle        = v.findViewById(R.id.tvGroupTitle);
            tvCount        = v.findViewById(R.id.tvGroupCount);
        }
    }
}
