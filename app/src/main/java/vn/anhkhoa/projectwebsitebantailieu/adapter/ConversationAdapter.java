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
import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemConsersationBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.ChatModel;
import vn.anhkhoa.projectwebsitebantailieu.model.response.ConversationOverviewDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.DateTimeUtils;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<ConversationOverviewDto> conList;
    private Context context;
    private OnItemClickListener listener;

    public ConversationAdapter(List<ConversationOverviewDto> conList, Context context) {
        this.conList = conList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConsersationBinding binding = ItemConsersationBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversationOverviewDto con = conList.get(position);

        // Hiển thị avatar
        Glide.with(context)
                .load(con.getAvatarUrl())
                .placeholder(R.drawable.ic_person)
                .into(holder.binding.imgAvatar);

        // Gán dữ liệu vào View
        holder.binding.tvName.setText(con.getDisplayName());
        holder.binding.tvLastMessage.setText(con.getLastMessageContent());
        int unreadCountInt = (con.getUnreadCount() != null ? con.getUnreadCount().intValue() : 0);
        holder.binding.tvUnreadCount.setText(String.valueOf(unreadCountInt));
        holder.binding.tvTime.setText(DateTimeUtils.formatTime(con.getLastMessageTime()));

        // Hiển thị badge tin nhắn chưa đọc nếu > 0
        if(con.getUnreadCount() > 0) {
            holder.binding.tvUnreadCount.setVisibility(View.VISIBLE);
            holder.binding.tvUnreadCount.setText(String.valueOf(con.getUnreadCount()));
        } else {
            holder.binding.tvUnreadCount.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click item
        holder.itemView.setOnClickListener(v -> {
            if(listener != null) {
                //khi nhan se truyen vao doi tuong cha
                listener.onItemClick(con);
            }
        });
    }

    // Phương thức cập nhật danh sách dữ liệu mới
    public void updateList(List<ConversationOverviewDto> newList) {
        this.conList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return conList != null ? conList.size() : 0;
    }

    // Interface để xử lý click
    public interface OnItemClickListener {
        void onItemClick(ConversationOverviewDto item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemConsersationBinding binding;
        public ViewHolder(ItemConsersationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
