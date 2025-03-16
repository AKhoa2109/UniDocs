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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatModel> chatList;
    private Context context;
    private OnChatItemClickListener listener;

    public ChatAdapter(List<ChatModel> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    public void setOnChatItemClickListener(OnChatItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConsersationBinding binding = ItemConsersationBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
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
                listener.onChatItemClick(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList != null ? chatList.size() : 0;
    }

    //interface lang nghe su kien
    public interface OnChatItemClickListener {
        void onChatItemClick(ChatModel chat);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        ItemConsersationBinding binding;
        public ChatViewHolder(ItemConsersationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
