package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ChatAdapter;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentConversationListBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.ChatModel;

import androidx.core.view.ViewCompat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConversationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversationListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView rcChat;
    private ChatAdapter chatAdapter;
    private List<ChatModel> chatList;
    private FragmentConversationListBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConversationListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConversationListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversationListFragment newInstance(String param1, String param2) {
        ConversationListFragment fragment = new ConversationListFragment();
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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        View mainView = view.findViewById(R.id.fragment_conversation_layout);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.svConversation.setQueryHint("Tìm kiếm");
        AnhXa();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerViewChatList.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(chatList, getContext());
        binding.recyclerViewChatList.setAdapter(chatAdapter);

        chatAdapter.setOnItemClickListener(() -> {
            //Toast.makeText(getContext(), "Click ", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openChatFragment();
            }
        });

    }

    public void AnhXa(){
        chatList = new ArrayList<>();
        chatList.add(new ChatModel("George Alan", "Lorem ipsum dolor sit amet", "4:30 PM", 1, "https://nano-ceramic.vn/wp-content/uploads/2024/12/300-hinh-anh-dai-dien-dep-cho-facebook-tiktok-zalo-79.jpg"));
        chatList.add(new ChatModel("Uber Cars", "Sender: Lorem ipsum...", "4:20 PM", 0, "https://nano-ceramic.vn/wp-content/uploads/2024/12/300-hinh-anh-dai-dien-dep-cho-facebook-tiktok-zalo-79.jpg"));
        chatList.add(new ChatModel("Safiya Fareena", "Video", "4:15 PM", 2, "https://nano-ceramic.vn/wp-content/uploads/2024/12/300-hinh-anh-dai-dien-dep-cho-facebook-tiktok-zalo-79.jpg"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConversationListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}