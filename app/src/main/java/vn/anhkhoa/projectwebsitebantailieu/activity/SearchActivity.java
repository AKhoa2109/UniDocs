package vn.anhkhoa.projectwebsitebantailieu.activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.fragment.SearchFragment;

public class SearchActivity extends AppCompatActivity {
    private EditText edtSearch;
    private Handler searchHandler;
    private Runnable searchRunnable;

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
        searchHandler = new Handler();
        if (savedInstanceState == null) {
            SearchFragment searchFragment = new SearchFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, searchFragment);
            transaction.commit();
        }

        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);

        // Xử lý sự kiện
        handlerEditText();
        handlerEditTextChange();

    }

    private void initViews() {
        edtSearch = findViewById(R.id.edtSearch);
    }

    private void handlerEditText(){
        // Xử lý sự kiện khi người dùng nhấn Enter hoặc nút Done trên bàn phím
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchQuery = edtSearch.getText().toString().trim();
                    SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);
                    if (!searchQuery.isEmpty() && searchFragment != null) {
                        searchFragment.handlerSaveHistorySearch(searchQuery);
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
                        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);
                        if(!query.isEmpty() && searchFragment !=null){
                            searchFragment.handlerEditSearchChange(query);
                        }
                        else{
                            if(searchFragment!=null){
                                searchFragment.handlerEditSearchNotChange(query);
                            }
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
}