package vn.anhkhoa.projectwebsitebantailieu.fragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.adapter.HistorySearchAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.SuggestionDocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.SuggestionSearchAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.database.HistorySearchDao;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentSearchBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    private DatabaseHandler databaseHandler;
    private HistorySearchDao historySearchDao;
    private HistorySearchAdapter historySearchAdapter;
    private SuggestionSearchAdapter suggestionSearchAdapter;
    private SuggestionDocumentAdapter suggestionDocumentAdapter;
    private ArrayList<DocumentDto> allHistory, documents, displayDocument,suggestionDocument;
    private int currentPage = 1;
    private final int PAGE_SIZE = 5;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentSearchBinding.inflate(inflater, container, false);

        initViews();
        if(allHistory.size() == 0){
            binding.tvHandleRecyclerView.setVisibility(View.GONE);
        }
        handlerSeeMoreHistory();
        handlerDisplaySuggestionDocument();
        loadAllHistory();
        return binding.getRoot();

    }

    private void initViews(){
        databaseHandler = DatabaseHandler.getInstance(getContext());
        historySearchDao = new HistorySearchDao(getContext());
        // Khởi tạo danh sách tài liệu và adapter
        documents = new ArrayList<>();
        displayDocument = new ArrayList<>();
        allHistory = new ArrayList<>();
        suggestionDocument = new ArrayList<>();

        historySearchAdapter = new HistorySearchAdapter(documents);
        suggestionSearchAdapter = new SuggestionSearchAdapter(displayDocument);
        suggestionDocumentAdapter = new SuggestionDocumentAdapter(suggestionDocument);
        binding.rcHistorySearch.setAdapter(historySearchAdapter);
        binding.rcSearch.setAdapter(suggestionSearchAdapter);
        binding.rcSuggestionDocument.setAdapter(suggestionDocumentAdapter);

        // Thiết lập layout manager và divider cho RecyclerView
        binding.rcHistorySearch.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcSearch.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcSuggestionDocument.setLayoutManager(new GridLayoutManager(getContext(), 2));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rcHistorySearch.addItemDecoration(dividerItemDecoration);
        binding.rcSearch.addItemDecoration(dividerItemDecoration);

        if(allHistory.size() == 0){
            binding.tvHandleRecyclerView.setVisibility(View.GONE);
        }
    }

    private void showHistoryRecycler() {
        binding.rcHistorySearch.setVisibility(View.VISIBLE);
        binding.rcSearch.setVisibility(View.GONE);
    }

    private void showSearchRecycler() {
        binding.rcHistorySearch.setVisibility(View.GONE);
        binding.rcSearch.setVisibility(View.VISIBLE);
    }

    private void addSearchHistory(String name) {
        // Kiểm tra xem tìm kiếm đã tồn tại chưa
        if (!isSearchExist(name)) {
            historySearchDao.addSearchQuery(name);
        } else {
            Toast.makeText(getContext(), "Tìm kiếm đã tồn tại trong lịch sử", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSearchExist(String searchQuery) {
        boolean exists = historySearchDao.queryExists(searchQuery);
        return exists;
    }

    public void loadAllHistory() {
        currentPage = 1;
        allHistory = historySearchDao.getAllHistory();
        updateDisplayHistory();
    }

    private void updateDisplayHistory() {
        int total = allHistory.size();
        int count = Math.min(currentPage * PAGE_SIZE, total);
        documents.clear();
        // Chỉ lấy phần cần hiển thị từ allHistory
        documents.addAll(allHistory.subList(0, count));
        historySearchAdapter.notifyDataSetChanged();

        if(count >= total) {
            binding.tvHandleRecyclerView.setText("Xóa lịch sử");
        } else {
            binding.tvHandleRecyclerView.setText("Xem thêm");
        }
        binding.tvHandleRecyclerView.setVisibility(total == 0 ? View.GONE : View.VISIBLE);
    }

    public void callApiSearch(String s){
        ApiService.apiService.searchDocument(s).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    displayDocument.clear();
                    displayDocument.addAll(response.body().getData());
                    suggestionSearchAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void callApiSuggestionDoc(String s){
        ApiService.apiService.searchDocument(s).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    suggestionDocument.clear();
                    suggestionDocument.addAll(response.body().getData());
                    suggestionDocumentAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void handlerSeeMoreHistory(){
        binding.tvHandleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.tvHandleRecyclerView.getText().toString().trim().equals("Xóa lịch sử")){
                    historySearchDao.deleteSearchQuery();
                    allHistory.clear();
                    documents.clear();
                    historySearchAdapter.notifyDataSetChanged();
                    binding.tvHandleRecyclerView.setVisibility(View.GONE);
                }
                else{
                    currentPage++;
                    updateDisplayHistory();
                }
            }
        });
    }

    public void handlerDisplaySuggestionDocument(){
        ArrayList<DocumentDto> allHistory = historySearchDao.getAllHistory();
        for(DocumentDto doc : allHistory){
            callApiSuggestionDoc(doc.getDocName());
        }
    }

    public void handlerEditSearchChange(String query){
        binding.tvHandleRecyclerView.setVisibility(View.GONE);
        binding.tvSuggestion.setVisibility(View.GONE);
        binding.rcSuggestionDocument.setVisibility(View.GONE);
        showSearchRecycler();
        callApiSearch(query);
    }

    public void handlerEditSearchNotChange(String query){
        binding.tvSuggestion.setVisibility(View.VISIBLE);
        binding.rcSuggestionDocument.setVisibility(View.VISIBLE);
        showHistoryRecycler();
        loadAllHistory();
    }

    public void handlerSaveHistorySearch(String query){
        addSearchHistory(query);
        loadAllHistory();
    }
}