package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ChatAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentChatBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.ChatStatus;
import vn.anhkhoa.projectwebsitebantailieu.enums.ChatType;
import vn.anhkhoa.projectwebsitebantailieu.model.ChatLineDto;
import vn.anhkhoa.projectwebsitebantailieu.model.response.ConversationOverviewDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.FilePickerUtils;
import vn.anhkhoa.projectwebsitebantailieu.utils.LocalDateTimeAdapter;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;
import android.net.Uri;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment  implements FilePickerUtils.FilePickerCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentChatBinding binding;
    private List<ChatLineDto> chatLineDtoList;
    private ChatAdapter chatAdapter;

    private ConversationOverviewDto conversationOverviewDto;
    SessionManager sessionManager;
    private StompClient mStompClient;
    private Disposable topicSubscription;
    private Gson gson;
    private FilePickerUtils filePickerUtils;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View mainView = view.findViewById(R.id.fragment_chat_layout);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
        } else {
            gson = null;
        }
        sessionManager = SessionManager.getInstance(requireContext());
        // Khởi tạo filePickerUtils với Activity chứa Fragment và callback là chính fragment này
        filePickerUtils = new FilePickerUtils(this, this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            conversationOverviewDto = (ConversationOverviewDto) bundle.getSerializable("conversationOverviewDto");
        }

        //load ành , ten
        Glide.with(requireContext())
                .load(conversationOverviewDto.getAvatarUrl())
                .placeholder(R.drawable.ic_person)
                .into(binding.ivAvatar);
        binding.tvConName.setText(conversationOverviewDto.getDisplayName());

        //btnBack
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goi su kien back cua he thong
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        //btn gui
        binding.flSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        //khi co noi dung nhap thi an button gui anh va file
        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //neu nguoi dung nhap noi dung
                if(charSequence.length() >0){
                    binding.ivSendPicture.setVisibility(View.GONE);
                    binding.ivSendFile.setVisibility(View.GONE);
                }
                else{
                    binding.ivSendPicture.setVisibility(View.VISIBLE);
                    binding.ivSendFile.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
        //btn gui anh
        binding.ivSendPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chon anh va gui
                filePickerUtils.checkPermissionAndPick(FilePickerUtils.PICKER_TYPE_IMAGE);
                //ToastUtils.show(requireContext(), "gui anh");
            }
        });
        
        //btn gui file
        binding.ivSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chọn file va gui
                filePickerUtils.checkPermissionAndPick(FilePickerUtils.PICKER_TYPE_MULTIPLE);
            }
        });
        connectSocket();

        // rvMessages.getItemAnimator().setChangeDuration(false);
        RecyclerView.ItemAnimator animator = binding.rvMessages.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false); // Tắt animation thay đổi
        }

        chatLineDtoList = new ArrayList<>();
        //adapter
        chatAdapter = new ChatAdapter(sessionManager.getUser().getUserId(), getContext());
        binding.rvMessages.setAdapter(chatAdapter);
        //layout manager từ dưới lên
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);

        chatAdapter.submitList(chatLineDtoList);

        //lay tin nhan cu
        ApiService.apiService.getChatMessages(conversationOverviewDto.getConversationId()).enqueue(new Callback<ResponseData<List<ChatLineDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<ChatLineDto>>> call, Response<ResponseData<List<ChatLineDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseData<List<ChatLineDto>> data = response.body();
                    // chatLineDtoList.clear();
                    List<ChatLineDto> newLines = data.getData();

                    chatLineDtoList.addAll(newLines);
                    chatAdapter.submitList(new ArrayList<>(chatLineDtoList));
                    //cuon den tn cuoi
                    binding.rvMessages.post(() -> binding.rvMessages.scrollToPosition(chatAdapter.getItemCount() - 1));
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<ChatLineDto>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //gui anh va file
    private void sendPictureAndFile(List<File> files, int currentPickerType) {
        //tao tin nhan
        ChatLineDto message = new ChatLineDto();
        message.setConversationId(conversationOverviewDto.getConversationId());
        message.setUserId(sessionManager.getUser().getUserId());
        message.setContent("");
        //type
        if(currentPickerType == FilePickerUtils.PICKER_TYPE_IMAGE){
            message.setChatType(ChatType.IMAGES);
        }
        else if (currentPickerType == FilePickerUtils.PICKER_TYPE_MULTIPLE){
            message.setChatType(ChatType.FILE);
        }

        message.setSendAt(LocalDateTime.now());

        message.setChatStatus(ChatStatus.SENT);
        //chuyen sang gson
        String jsonMessage = gson.toJson(message);
        RequestBody messageBody = RequestBody.create(MediaType.parse("application/json"), jsonMessage);
        MultipartBody.Part messagePart = MultipartBody.Part.createFormData("message", "message.json", messageBody);

        List<MultipartBody.Part> fileParts = new ArrayList<>();
        ContentResolver contentResolver = requireContext().getContentResolver();
        for (File file : files) {
            Uri uri = Uri.fromFile(file);
            String mimeType = contentResolver.getType(uri);
            // Nếu không xác định được MIME type, sử dụng application/octet-stream làm mặc định
            if (mimeType == null || mimeType.isEmpty()) {
                mimeType = "application/octet-stream";
            }
            RequestBody requestFile = null;

            if(currentPickerType == FilePickerUtils.PICKER_TYPE_IMAGE){
                requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            }
            else{
                requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            }

            MultipartBody.Part body = MultipartBody.Part.createFormData("files", file.getName(), requestFile);
            fileParts.add(body);
        }

        //api gui hinh anh
        ToastUtils.show(requireContext(), "Hình ảnh/file đang được gửi");
        ApiService.apiService.sendChatPicture(messagePart, fileParts).enqueue(new Callback<ChatLineDto>() {
            @Override
            public void onResponse(Call<ChatLineDto> call, Response<ChatLineDto> response) {
                if(response.isSuccessful() && response.body() != null){
                    ToastUtils.show(requireContext(), "gui file thanh cong");
                    Log.d("TAG", "Message sent successfully: ");
                }
                else {
//                    Log.e("TAG", "Error sending message: " + response.message());
                    String errorMessage = "Error sending message: " + response.message();
                    // Nếu errorBody không null, in thêm thông tin lỗi chi tiết
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += "\n" + response.errorBody().string();
                        } catch (IOException e) {
                            errorMessage += "\nLỗi khi đọc error body: " + e.getMessage();
                        }
                    }
                    Log.e("TAG", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ChatLineDto> call, Throwable t) {
                Log.e("TAG", "Failed to send message: " + t.getMessage());
                ToastUtils.show(requireContext(),"onFailure");
            }
        });
    }

    //khi chon xong file
    @Override
    public void onFilesPicked(List<File> files,int currentPickerType) {
        sendPictureAndFile(files, currentPickerType);
    }

    //gui tin nhan thuong
    private void sendMessage() {
        // Toast.makeText(getContext(), "da nhan gui", Toast.LENGTH_SHORT).show();
        String content = binding.etMessage.getText().toString().trim();
        if (!content.isEmpty()) {
            ChatLineDto message = new ChatLineDto();
            // thuoc tinh
            message.setConversationId(conversationOverviewDto.getConversationId());
            message.setUserId(sessionManager.getUser().getUserId());
            message.setContent(content);
            message.setChatType(ChatType.MESS);
            message.setSendAt(LocalDateTime.now());

            message.setChatStatus(ChatStatus.SENT);

            // Cập nhật giao diện ngay lập tức (thêm tin nhắn của người gửi)
            chatAdapter.addMessage(message);

            // Chuyển đổi đối tượng ChatLine thành JSON
            String jsonMessage = gson.toJson(message);
            Log.d("JSON Output", jsonMessage);

            // Gửi tin nhắn tới endpoint /app/chat/{conId}
            mStompClient.send("/app/chat/" + conversationOverviewDto.getConversationId(), jsonMessage).subscribe();

            // Xóa nội dung EditText sau khi gửi
            binding.etMessage.setText("");

            //cuon den cuoi
            binding.rvMessages.post(() -> binding.rvMessages.scrollToPosition(chatAdapter.getItemCount() - 1));
        }
    }

    //thay doi kich thuoc rc chat khi ban phim xuat hien
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        RecyclerView rvMessages = binding.rvMessages;
        View bottomInputLayout = binding.bottomInputLayout;
        View root = binding.getRoot();

        // Lắng nghe sự thay đổi kích thước màn hình (bao gồm cả bàn phím)
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                root.getWindowVisibleDisplayFrame(rect);
                float screenHeight = root.getHeight();
                float keypadHeight = screenHeight - rect.bottom;

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rvMessages.getLayoutParams();
                // Nếu bàn phím đang hiển thị
                if (keypadHeight > screenHeight * 0.15) {
                    lp.weight = 0; // Hủy thuộc tính weight để height có thể tự do điều chỉnh
                    //tru layoutTop , bottomlayout*2(de noi len), keypadHeight
                    lp.height = (int) (screenHeight - keypadHeight - 2*bottomInputLayout.getHeight() - binding.layoutTopChat.getHeight());
                    rvMessages.setLayoutParams(lp);
                } else {
                    // Khôi phục thuộc tính weight cho RecyclerView để layout hoạt động đúng
                    lp.weight = 1;
                    lp.height = 0;  // toàn bộ không gian còn lại
                    rvMessages.setLayoutParams(lp);
                }
            }
        });

        return root;
    }

    public void connectSocket() {
        // Khởi tạo STOMP client với endpoint
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + ApiService.ipAddress + "/ws/websocket");
        mStompClient.connect();

        // Đăng ký subscribe topic nhận tin nhắn từ server
        topicSubscription = mStompClient.topic("/topic/conversation/" + conversationOverviewDto.getConversationId())
                .subscribe(topicMessage -> {
                    // Lấy payload (JSON) và deserialize thành ChatLine
                    ChatLineDto chatLineDto = gson.fromJson(topicMessage.getPayload(), ChatLineDto.class);

                    // Cập nhật giao diện ở UI thread
                    chatLineDtoList.add(chatLineDto);
                    chatAdapter.submitList(new ArrayList<>(chatLineDtoList), ()->{
                        if (isAtBottom()) {
                            // Nếu đang ở cuối -> cuộn xuống
                            binding.rvMessages.scrollToPosition(chatAdapter.getItemCount() - 1);
                        } else {
                            // Nếu không ở cuối -> hiện Toast
                            requireActivity().runOnUiThread(() ->
                                    ToastUtils.show(requireContext(), "Có tin nhắn mới", "#2ab857")
                            );
                        }
                    });

                    //
                }, throwable -> {
                    // Xử lý lỗi khi subscribes
                    throwable.printStackTrace();
                });
    }

    @Override
    public void onDestroy() {
        if (topicSubscription != null && !topicSubscription.isDisposed()) {
            topicSubscription.dispose();
        }
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
        super.onDestroy();
    }

    //kiem tra nguoi dung co o duoi
    private boolean isAtBottom() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.rvMessages.getLayoutManager();
        if (layoutManager == null) return false;

        int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        int totalItemCount = chatAdapter.getItemCount();

        return lastVisiblePosition == totalItemCount - 1;
    }
}