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

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.HistorySearchAdapter;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivityMainBinding;
import vn.anhkhoa.projectwebsitebantailieu.fragment.SearchFragment;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public class SearchActivity extends AppCompatActivity {

    private SearchFragment searchFragment;
    DatabaseHandler databaseHandler;
    RecyclerView rcHistorySearch;
    EditText edtSearch;
    ArrayList<DocumentDto> documents;
    HistorySearchAdapter historySearchAdapter;

    private int id = 0;
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

        AnhXa();

        documents = new ArrayList<>();

        LinearLayoutManager linearLayoutCateManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        rcHistorySearch.setLayoutManager(linearLayoutCateManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcHistorySearch.addItemDecoration(dividerItemDecoration);

        initDatabase();

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_SEND ||
                        (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                    String name = edtSearch.getText().toString().trim();
                    if (!name.isEmpty()) {
                        databaseHandler.QueryData("INSERT INTO HistorySearch VALUES('"+id+"','"+name+"')");
                        id+=1;
                        loadAllHistory();
                    }
                    return true;
                }
                return false;

            }
        });

        historySearchAdapter = new HistorySearchAdapter(documents);
        rcHistorySearch.setAdapter(historySearchAdapter);

    }

    @Override
    protected void onDestroy() {
        databaseHandler.close();
        super.onDestroy();
    }

    private void AnhXa(){
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        rcHistorySearch = (RecyclerView) findViewById(R.id.rcHistorySearch);
    }

    private void loadAllHistory(){
        documents.clear();
        Cursor cursor = databaseHandler.GetData("SELECT * FROM HistorySearch ORDER BY Id DESC");
        while(cursor.moveToNext()){
            Long id = cursor.getLong(0);
            String name = cursor.getString(1);
            documents.add(new DocumentDto(id, name));
        }
        historySearchAdapter.notifyDataSetChanged();
        cursor.close();
    }

    private void initDatabase(){
        databaseHandler = new DatabaseHandler(this, "historySearch.sqlite",null,1);
        databaseHandler.QueryData("CREATE TABLE IF NOT EXISTS HistorySearch(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name VARCHAR(200))");
    }
}