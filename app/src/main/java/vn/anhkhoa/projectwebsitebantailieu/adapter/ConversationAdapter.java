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

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<ChatModel> chatList;
    private Context context;
    private OnItemClickListener listener;

    public ConversationAdapter(List<ChatModel> chatList, Context context) {
        this.chatList = chatList;
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
        ChatModel chat = chatList.get(position);

        // Gán dữ liệu vào View
        holder.binding.tvName.setText(chat.getName());
        holder.binding.tvName.setText(chat.getName());
        holder.binding.tvLastMessage.setText(chat.getLastMessage());
        holder.binding.tvTime.setText(chat.getTime());

        // Hiển thị avatar
        Glide.with(context)
                .load(chat.getAvatar())
                .placeholder(R.drawable.ic_person)
                .into(holder.binding.imgAvatar);

        // Hiển thị badge tin nhắn chưa đọc nếu > 0
        if(chat.getUnreadCount() > 0) {
            holder.binding.tvUnreadCount.setVisibility(View.VISIBLE);
            holder.binding.tvUnreadCount.setText(String.valueOf(chat.getUnreadCount()));
        } else {
            holder.binding.tvUnreadCount.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click item
        holder.itemView.setOnClickListener(v -> {
            if(listener != null) {
                //khi nhan se truyen vao doi tuong cha
                listener.onItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList != null ? chatList.size() : 0;
    }

    // Interface để xử lý click
    public interface OnItemClickListener {
        void onItemClick();
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
