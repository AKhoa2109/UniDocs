package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ConversationAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentConversationListBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.response.ConversationOverviewDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

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
    private ConversationAdapter conversationAdapter;
    private List<ConversationOverviewDto> conList;
    private FragmentConversationListBinding binding;

    private SessionManager sessionManager;
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
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentConversationLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sessionManager = SessionManager.getInstance(requireContext());
        binding.svConversation.setQueryHint("Tìm kiếm");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerViewChatList.setLayoutManager(layoutManager);

        conList = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(conList, getContext());
        binding.recyclerViewChatList.setAdapter(conversationAdapter);

        getConversation();
        conversationAdapter.setOnItemClickListener((conversation) -> {
            //Toast.makeText(getContext(), "Click ", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openChatFragment(conversation);
            }
        });

        binding.svConversation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

    }

    //loc conversation theo ten trong recyclerview
    private void filterList(String newText) {
        // Chưa có dữ liệu , thì không loc
        if (conList.isEmpty()) {
            return;
        }
        List<ConversationOverviewDto> filteredList  = new ArrayList<>();

        String query = (newText == null) ? "" : newText.trim();
        if(query.isEmpty()){
            filteredList.addAll(conList);
        }else {
            for(ConversationOverviewDto con : conList){
                if(con.getDisplayName().toLowerCase().contains(query.toLowerCase())){
                    filteredList.add(con);
                }
            }
        }

        conversationAdapter.updateList(filteredList);
    }

    public void getConversation() {
        ApiService.apiService.findConversationsOverview(sessionManager.getUser().getUserId()).enqueue(new Callback<ResponseData<List<ConversationOverviewDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<ConversationOverviewDto>>> call, Response<ResponseData<List<ConversationOverviewDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    conList.clear();
                    ResponseData<List<ConversationOverviewDto>> responseData = response.body();
                    conList.addAll(responseData.getData());
                    conversationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<ConversationOverviewDto>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConversationListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}