package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemChatMultiImageReceivedBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemChatMultiImageSentBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemMessageReceivedBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemMessageSentBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.ChatType;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ImageFullscreenFragment;
import vn.anhkhoa.projectwebsitebantailieu.model.ChatLineDto;
import vn.anhkhoa.projectwebsitebantailieu.model.FileChatLine;
import vn.anhkhoa.projectwebsitebantailieu.utils.DateTimeUtils;

public class ChatAdapter extends ListAdapter<ChatLineDto, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_SENT = 3;

    private static final int VIEW_TYPE_IMAGE_RECEIVED = 4;

    private static final int VIEW_TYPE_FILE_SENT     = 5;
    private static final int VIEW_TYPE_FILE_RECEIVED = 6;
    private final long currentUserId;
    private final AsyncListDiffer<ChatLineDto> mDiffer;

    //tinh toan chieu rong tin nhan
    static int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    static int maxWidth = (int) (screenWidth * 0.75f);

    public ChatAdapter(long currentUserId) {
        super(DIFF_CALLBACK);
        this.currentUserId = currentUserId;
        mDiffer = new AsyncListDiffer<>(this, DIFF_CALLBACK);
    }

    @Override
    public int getItemViewType(int position) {
        //lay vi tri
        ChatLineDto message = getItem(position);
        // Xác định user gửi hay nhận
        boolean isSent = Objects.equals(currentUserId, message.getUserId());
        // Xác định loại chat
        ChatType type = message.getChatType() != null
                ? message.getChatType()
                : ChatType.MESS; // default

        //tra ve loai view tuong ung khi sent, received
        switch (type){
            case IMAGES:
                return isSent ? VIEW_TYPE_IMAGE_SENT : VIEW_TYPE_IMAGE_RECEIVED;
            case FILE:
                return isSent ? VIEW_TYPE_FILE_SENT : VIEW_TYPE_FILE_RECEIVED;
            case MESS:
            default:
                return isSent ? VIEW_TYPE_SENT: VIEW_TYPE_RECEIVED;
        }
      //  return currentUserId == (message.getUserId() != null ? message.getUserId() : -1L) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SENT) {
            ItemMessageSentBinding sentBinding = ItemMessageSentBinding.inflate(inflater, parent,false);
            return new SentMessageViewHolder(sentBinding);
        }
        else if(viewType == VIEW_TYPE_RECEIVED)
        {
            ItemMessageReceivedBinding receivedBinding = ItemMessageReceivedBinding.inflate(inflater, parent,false);
            return new ReceivedMessageViewHolder(receivedBinding);
        }
        else if(viewType == VIEW_TYPE_IMAGE_SENT)
        {
            ItemChatMultiImageSentBinding binding = ItemChatMultiImageSentBinding.inflate(inflater, parent,false);
            return new SentMultiImageViewHolder(binding);
        }
        else
        {
            ItemChatMultiImageReceivedBinding binding = ItemChatMultiImageReceivedBinding.inflate(inflater, parent,false);
            return new ReceivedMultiImageViewHolder(binding);
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
        }
        else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message, isLastMessageByUser);
        }
        else if (holder instanceof SentMultiImageViewHolder) {
            ((SentMultiImageViewHolder) holder).bind(message, isLastMessageByUser);
        }
        else if (holder instanceof ReceivedMultiImageViewHolder) {
            ((ReceivedMultiImageViewHolder) holder).bind(message, isLastMessageByUser);
        }
    }

    //them phan tu
    public void addMessage(ChatLineDto newMessage) {
        List<ChatLineDto> currentList = new ArrayList<>(getCurrentList());
        currentList.add(newMessage);
        submitList(currentList);
    }

    //view holder mess sent
    public static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        ItemMessageSentBinding sentBinding;
        public SentMessageViewHolder(@NonNull ItemMessageSentBinding sentBinding) {
            super(sentBinding.getRoot());
            this.sentBinding = sentBinding;

            sentBinding.tvMessage.setMaxWidth(maxWidth);
        }

        public void bind(ChatLineDto message, boolean showTime) {
            //gan noi dung
            sentBinding.tvMessage.setText(message.getContent());
            //kiem tra hien thoi gian
            if (showTime) {
                sentBinding.tvTime.setText(DateTimeUtils.formatTime(message.getSendAt()));
                sentBinding.tvTime.setVisibility(View.VISIBLE);
            } else {
                sentBinding.tvTime.setVisibility(View.GONE);
            }
        }
    }

    //View holder received mess
    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        ItemMessageReceivedBinding receivedBinding;

        public ReceivedMessageViewHolder(@NonNull ItemMessageReceivedBinding receivedBinding) {
            super(receivedBinding.getRoot());
            this.receivedBinding = receivedBinding;
            receivedBinding.tvMessage.setMaxWidth(maxWidth);
        }

        public void bind(ChatLineDto message, boolean showTime) {
            //gan noi dung
            receivedBinding.tvMessage.setText(message.getContent());
            //kiem tra hien thoi gian
            if (showTime) {
                receivedBinding.tvTime.setText(DateTimeUtils.formatTime(message.getSendAt()));
                receivedBinding.tvTime.setVisibility(View.VISIBLE);
            } else {
                receivedBinding.tvTime.setVisibility(View.GONE);
            }
        }
    }

    //view holder gui hinh
    public static class SentMultiImageViewHolder extends RecyclerView.ViewHolder{
        ItemChatMultiImageSentBinding binding;
        public SentMultiImageViewHolder(@NonNull ItemChatMultiImageSentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Lấy LayoutParams hiện tại của flexContainer
            ViewGroup.LayoutParams params = binding.flexContainer.getLayoutParams();
            // Thay đổi chiều rộng
            params.width = maxWidth; // set theo pixel
            // Áp dụng lại LayoutParams
            binding.flexContainer.setLayoutParams(params);
        }

        public void bind(ChatLineDto message, boolean showTime) {
            //noi dung
        //    binding.tvMessage.setText(message.getContent());

            //hien thi anh
            displayImagesInFlexbox(binding.flexContainer, message.getFileChatLines());

            //kiem tra hien thoi gian
            if (showTime) {
                binding.tvTime.setText(DateTimeUtils.formatTime(message.getSendAt()));
                binding.tvTime.setVisibility(View.VISIBLE);
            } else {
                binding.tvTime.setVisibility(View.GONE);
            }
        }

    }

    //view holder nhan hinh
    public static class ReceivedMultiImageViewHolder extends RecyclerView.ViewHolder{
        ItemChatMultiImageReceivedBinding binding;
        public ReceivedMultiImageViewHolder(@NonNull ItemChatMultiImageReceivedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Lấy LayoutParams hiện tại của flexContainer
            ViewGroup.LayoutParams params = binding.flexContainer.getLayoutParams();
            // Thay đổi chiều rộng
            params.width = maxWidth; // set theo pixel
            // Áp dụng lại LayoutParams
            binding.flexContainer.setLayoutParams(params);
        }

        public void bind(ChatLineDto message, boolean showTime) {
            //noi dung
            //    binding.tvMessage.setText(message.getContent());

            //hien thi anh
            displayImagesInFlexbox(binding.flexContainer, message.getFileChatLines());

            //kiem tra hien thoi gian
            if (showTime) {
                binding.tvTime.setText(DateTimeUtils.formatTime(message.getSendAt()));
                binding.tvTime.setVisibility(View.VISIBLE);
            } else {
                binding.tvTime.setVisibility(View.GONE);
            }
        }
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



    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }


    private static void displayImagesInFlexbox(FlexboxLayout flexboxLayout, List<FileChatLine> imageUrls) {
        flexboxLayout.removeAllViews(); // Xóa các view cũ (nếu có)
        Context context = flexboxLayout.getContext();

        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (FileChatLine imageUrl : imageUrls) {
                ImageView imageView = new ImageView(flexboxLayout.getContext());
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(8, 8, 8, 8); // Điều chỉnh margin tùy ý
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                // Ví dụ với Glide:
                Glide.with(context)
                        .load(imageUrl.getFileUrl())
                        .override(maxWidth/2-16, maxWidth/2-16) // Kích thước hiển thị tối đa
                        .into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context ctx = v.getContext();
                        if (ctx instanceof MainActivity) {
                            MainActivity main = (MainActivity) ctx;
                            ImageFullscreenFragment imageFullscreenFragment = ImageFullscreenFragment.newInstance(imageUrl.getFileUrl());
                            main.showFragment(imageFullscreenFragment, "imageFullscreenFragment");
                        }
                    }
                });
                //them anh vao flexbox
                flexboxLayout.addView(imageView);
            }
        }
    }
}
