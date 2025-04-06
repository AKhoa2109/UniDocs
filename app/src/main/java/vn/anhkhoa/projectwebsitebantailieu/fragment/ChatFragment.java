package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;
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
import vn.anhkhoa.projectwebsitebantailieu.model.ChatLineDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.response.ConversationOverviewDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.LocalDateTimeAdapter;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

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

    // Giả sử conId của cuộc trò chuyện là "123"
    private ConversationOverviewDto conversationOverviewDto;
    SessionManager sessionManager;
    private final Long conversationId = 3L;
    private final Long userId = 2L;

    private StompClient mStompClient;
    private Disposable topicSubscription;
    private Gson gson;

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

        connectSocket();


        // rvMessages.getItemAnimator().setChangeDuration(false);
        RecyclerView.ItemAnimator animator = binding.rvMessages.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false); // Tắt animation thay đổi
        }

        chatLineDtoList = new ArrayList<>();
        //adapter
        chatAdapter = new ChatAdapter(sessionManager.getUser().getUserId());
        binding.rvMessages.setAdapter(chatAdapter);
        //layout manager từ dưới lên
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);

        chatAdapter.submitList(chatLineDtoList);

        ApiService.apiService.getChatMessages(conversationOverviewDto.getConversationId()).enqueue(new Callback<ResponseData<List<ChatLineDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<ChatLineDto>>> call, Response<ResponseData<List<ChatLineDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseData<List<ChatLineDto>> data = response.body();
                    // chatLineDtoList.clear();
                    List<ChatLineDto> newLines = data.getData();
                    //Log.d("aaaa", newLines.get(0).toString());
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
        //cuộn den phan tu cuoi
        //rvMessages.post(() -> rvMessages.scrollToPosition(chatAdapter.getItemCount() - 1));

    }

    public void connectSocket() {
        // Khởi tạo STOMP client với endpoint (chú ý: endpoint của SockJS có thể cần /websocket)
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + ApiService.ipAddress + "/ws/websocket");
        mStompClient.connect();

        // Đăng ký subscribe topic nhận tin nhắn từ server
        topicSubscription = mStompClient.topic("/topic/conversation/" + conversationOverviewDto.getConversationId())
                .subscribe(topicMessage -> {
                    // Lấy payload (JSON) và deserialize thành ChatLine
                    ChatLineDto chatLineDto = gson.fromJson(topicMessage.getPayload(), ChatLineDto.class);

                    // Cập nhật giao diện ở UI thread
                    chatLineDtoList.add(chatLineDto);
                    chatAdapter.submitList(new ArrayList<>(chatLineDtoList));
                }, throwable -> {
                    // Xử lý lỗi khi subscribes
                    throwable.printStackTrace();
                });
    }

    private void sendMessage() {
        // Toast.makeText(getContext(), "da nhan gui", Toast.LENGTH_SHORT).show();
        String content = binding.etMessage.getText().toString().trim();
        if (!content.isEmpty()) {
            ChatLineDto message = new ChatLineDto();

            // thuoc tinh
            message.setConId(conversationOverviewDto.getConversationId());
            message.setUserId(sessionManager.getUser().getUserId());
            message.setContent(content);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                message.setSendAt(LocalDateTime.now());
            }
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


}