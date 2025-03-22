package vn.anhkhoa.projectwebsitebantailieu.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.HistorySearchAdapter;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.database.HistorySearchDao;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public class SearchActivity extends AppCompatActivity {

    private DatabaseHandler databaseHandler;
    private HistorySearchDao historySearchDao;
    private RecyclerView rcHistorySearch;
    private EditText edtSearch;
    private ArrayList<DocumentDto> documents;
    private HistorySearchAdapter historySearchAdapter;

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

        // Khởi tạo danh sách tài liệu và adapter
        documents = new ArrayList<>();
        historySearchAdapter = new HistorySearchAdapter(documents);
        rcHistorySearch.setAdapter(historySearchAdapter);

        // Thiết lập layout manager và divider cho RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcHistorySearch.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcHistorySearch.addItemDecoration(dividerItemDecoration);

        // Xử lý sự kiện khi người dùng nhấn Enter hoặc nút Done trên bàn phím
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_SEND ||
                        (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String searchQuery = edtSearch.getText().toString().trim();
                    if (!searchQuery.isEmpty()) {
                        addSearchHistory(searchQuery);
                        loadAllHistory(); // Tải lại lịch sử tìm kiếm
                    }
                    return true;
                }
                return false;
            }
        });

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
        rcHistorySearch = findViewById(R.id.rcHistorySearch);
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
        ArrayList<DocumentDto> newDocuments = historySearchDao.getAllHistory();
        documents.addAll(newDocuments);
        historySearchAdapter.notifyDataSetChanged();
    }
}