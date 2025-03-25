package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemMessageReceivedBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemMessageSentBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.ChatLineDto;

public class ChatAdapter extends ListAdapter<ChatLineDto, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private final long currentUserId;
    private final AsyncListDiffer<ChatLineDto> mDiffer;

    //tinh toan chieu rong tin nhan
    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int maxWidth = (int) (screenWidth * 0.75f);

    public ChatAdapter(long currentUserId) {
        super(DIFF_CALLBACK);
        this.currentUserId = currentUserId;
        mDiffer = new AsyncListDiffer<>(this, DIFF_CALLBACK);
    }

    @Override
    public int getItemViewType(int position) {
        ChatLineDto message = getItem(position);
        return currentUserId == (message.getUserId() != null ? message.getUserId() : -1L) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SENT) {
            ItemMessageSentBinding sentBinding = ItemMessageSentBinding.inflate(inflater, parent,false);
            return new SentMessageViewHolder(sentBinding);
        } else {
            ItemMessageReceivedBinding receivedBinding = ItemMessageReceivedBinding.inflate(inflater, parent,false);
            return new ReceivedMessageViewHolder(receivedBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatLineDto message = getItem(position);
        //neu true thi hien thi thoi gian
        boolean isLastMessageByUser = (position == getItemCount() - 1) ||
                !getItem(position + 1).getUserId().equals(message.getUserId());

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message, isLastMessageByUser);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message, isLastMessageByUser);
        }
    }

    //them phan tu
    public void addMessage(ChatLineDto newMessage) {
        List<ChatLineDto> currentList = new ArrayList<>(getCurrentList());
        currentList.add(newMessage);
        submitList(currentList);
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder{
        ItemMessageSentBinding sentBinding;
        public SentMessageViewHolder(@NonNull ItemMessageSentBinding sentBinding) {
            super(sentBinding.getRoot());
            this.sentBinding = sentBinding;

            sentBinding.tvMessage.setMaxWidth(maxWidth);
        }

        public void bind(ChatLineDto message, boolean showTime) {
            sentBinding.tvMessage.setText(message.getContent());
            //kiem tra hien thoi gian
            if (showTime) {
                sentBinding.tvTime.setText(formatTime(message.getSendAt()));
                sentBinding.tvTime.setVisibility(View.VISIBLE);
            } else {
                sentBinding.tvTime.setVisibility(View.GONE);
            }
        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        ItemMessageReceivedBinding receivedBinding;

        public ReceivedMessageViewHolder(@NonNull ItemMessageReceivedBinding receivedBinding) {
            super(receivedBinding.getRoot());
            this.receivedBinding = receivedBinding;
            receivedBinding.tvMessage.setMaxWidth(maxWidth);
        }

        public void bind(ChatLineDto message, boolean showTime) {
            receivedBinding.tvMessage.setText(message.getContent());
            //kiem tra hien thoi gian
            if (showTime) {
                receivedBinding.tvTime.setText(formatTime(message.getSendAt()));
                receivedBinding.tvTime.setVisibility(View.VISIBLE);
            } else {
                receivedBinding.tvTime.setVisibility(View.GONE);
            }
        }
    }

    private static String formatTime(LocalDateTime dateTime) {
        if(dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
            return dateTime.format(formatter);
        }
        return "";
    }

    //so sanh de cap nhat
    private static final DiffUtil.ItemCallback<ChatLineDto> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChatLineDto>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChatLineDto oldItem, @NonNull ChatLineDto newItem) {
                    //so sanh an toan null neu 1 trong 2 null
                    return Objects.equals(oldItem.getChatLineId(), newItem.getChatLineId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull ChatLineDto oldItem, @NonNull ChatLineDto newItem) {
                    return Objects.equals(oldItem.getContent(), newItem.getContent())
                            && oldItem.getChatStatus() == newItem.getChatStatus();
                }
            };
}
