package vn.anhkhoa.projectwebsitebantailieu.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.HistorySearchAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.SuggestionDocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.SuggestionSearchAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.database.HistorySearchDao;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public class SearchActivity extends AppCompatActivity {
    private DatabaseHandler databaseHandler;
    private HistorySearchDao historySearchDao;
    private RecyclerView rcHistorySearch, rcSearch, rcSuggestionDoc;
    private EditText edtSearch;
    private TextView tvSeeMore, tvSuggestion;
    private ArrayList<DocumentDto> allHistory,documents, displayDocument,suggestionDocument;
    private HistorySearchAdapter historySearchAdapter;
    private SuggestionSearchAdapter suggestionSearchAdapter;
    private SuggestionDocumentAdapter suggestionDocumentAdapter;
    private Handler searchHandler;
    private Runnable searchRunnable;
    private int currentPage = 1;
    private final int PAGE_SIZE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các view
        initViews();
        databaseHandler = DatabaseHandler.getInstance(this);
        historySearchDao = new HistorySearchDao(this);
        searchHandler = new Handler();

        // Khởi tạo danh sách tài liệu và adapter
        documents = new ArrayList<>();
        displayDocument = new ArrayList<>();
        allHistory = new ArrayList<>();
        suggestionDocument = new ArrayList<>();

        historySearchAdapter = new HistorySearchAdapter(documents);
        suggestionSearchAdapter = new SuggestionSearchAdapter(displayDocument);
        suggestionDocumentAdapter = new SuggestionDocumentAdapter(suggestionDocument);
        rcHistorySearch.setAdapter(historySearchAdapter);
        rcSearch.setAdapter(suggestionSearchAdapter);
        rcSuggestionDoc.setAdapter(suggestionDocumentAdapter);

        // Thiết lập layout manager và divider cho RecyclerView
        rcHistorySearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcSuggestionDoc.setLayoutManager(new GridLayoutManager(this, 2));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcHistorySearch.addItemDecoration(dividerItemDecoration);
        rcSearch.addItemDecoration(dividerItemDecoration);

        if(allHistory.size() == 0){
            tvSeeMore.setVisibility(View.GONE);
        }

        showHistoryRecycler();
        // Xử lý sự kiện
        handlerEditText();
        handlerEditTextChange();
        handlerSeeMoreHistory();
        handlerDisplaySuggestionDocument();
        // Tải lịch sử tìm kiếm khi khởi động activity
        loadAllHistory();
    }

    @Override
    protected void onDestroy() {
        // Đóng kết nối database khi activity bị hủy
        if (databaseHandler != null) {
            databaseHandler.close();
        }
        super.onDestroy();
    }

    private void initViews() {
        edtSearch = findViewById(R.id.edtSearch);
        tvSeeMore = findViewById(R.id.tvHandleRecyclerView);
        tvSuggestion = findViewById(R.id.tvSuggestion);
        rcHistorySearch = findViewById(R.id.rcHistorySearch);
        rcSearch = findViewById(R.id.rcSearch);
        rcSuggestionDoc = findViewById(R.id.rcSuggestionDocument);
    }

    private void showHistoryRecycler() {
        rcHistorySearch.setVisibility(View.VISIBLE);
        rcSearch.setVisibility(View.GONE);
    }

    private void showSearchRecycler() {
        rcHistorySearch.setVisibility(View.GONE);
        rcSearch.setVisibility(View.VISIBLE);
    }

    private void addSearchHistory(String name) {
        // Kiểm tra xem tìm kiếm đã tồn tại chưa
        if (!isSearchExist(name)) {
            historySearchDao.addSearchQuery(name);
        } else {
            Toast.makeText(this, "Tìm kiếm đã tồn tại trong lịch sử", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSearchExist(String searchQuery) {
        boolean exists = historySearchDao.queryExists(searchQuery);
        return exists;
    }

    private void loadAllHistory() {
        currentPage = 1;
        allHistory = historySearchDao.getAllHistory();
        updateDisplayHistory();
    }

    private void handlerEditText(){
        // Xử lý sự kiện khi người dùng nhấn Enter hoặc nút Done trên bàn phím
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchQuery = edtSearch.getText().toString().trim();
                    if (!searchQuery.isEmpty()) {
                        addSearchHistory(searchQuery);
                        loadAllHistory();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void handlerEditTextChange(){
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(searchRunnable !=null){
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String query = edtSearch.getText().toString().trim();
                        if(!query.isEmpty()){
                            tvSeeMore.setVisibility(View.GONE);
                            tvSuggestion.setVisibility(View.GONE);
                            rcSuggestionDoc.setVisibility(View.GONE);
                            showSearchRecycler();
                            callApiSearch(query);
                        }
                        else{
                            tvSuggestion.setVisibility(View.VISIBLE);
                            rcSuggestionDoc.setVisibility(View.VISIBLE);
                            showHistoryRecycler();
                            loadAllHistory();
                        }
                    }
                };
                searchHandler.postDelayed(searchRunnable, 300);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void handlerSeeMoreHistory(){
        tvSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvSeeMore.getText().toString().trim().equals("Xóa lịch sử")){
                    historySearchDao.deleteSearchQuery();
                    allHistory.clear();
                    documents.clear();
                    historySearchAdapter.notifyDataSetChanged();
                    tvSeeMore.setVisibility(View.GONE);
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
        for(DocumentDto documentDto : allHistory){
            callApiSuggestionDoc(documentDto.getDoc_name());
        }
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
                    Toast.makeText(SearchActivity.this, "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SearchActivity.this, "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateDisplayHistory() {
        int total = allHistory.size();
        int count = Math.min(currentPage * PAGE_SIZE, total);
        documents.clear();
        // Chỉ lấy phần cần hiển thị từ allHistory
        documents.addAll(allHistory.subList(0, count));
        historySearchAdapter.notifyDataSetChanged();

        if(count >= total) {
            tvSeeMore.setText("Xóa lịch sử");
        } else {
            tvSeeMore.setText("Xem thêm");
        }
        tvSeeMore.setVisibility(total == 0 ? View.GONE : View.VISIBLE);
    }
}