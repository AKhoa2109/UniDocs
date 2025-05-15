package vn.anhkhoa.projectwebsitebantailieu.activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.fragment.SearchDocumentFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.SearchFragment;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.FilterCriteria;
import vn.anhkhoa.projectwebsitebantailieu.utils.ShareViewModel;

public class SearchActivity extends AppCompatActivity {
    private ImageView ivFilter;
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
        // Xử lý hien thi ivFilter
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
                if(currentFragment instanceof SearchDocumentFragment){
                    ivFilter.setVisibility(View.VISIBLE);
                } else {
                    ivFilter.setVisibility(View.GONE);
                }
            }
        });

        // Xử lý sự kiện
        handlerImgFilter();
        handlerEditText();
        handlerEditTextChange();

    }

    private void initViews() {
        edtSearch = findViewById(R.id.edtSearch);
        ivFilter = findViewById(R.id.ivFilter);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v->{ onBackPressed();});
    }

    private void handlerEditText(){
        // Xử lý sự kiện khi người dùng nhấn Enter hoặc nút Done trên bàn phím
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchQuery = edtSearch.getText().toString().trim();
                    Fragment currentFragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);
                    if (!searchQuery.isEmpty()) {
                        if (currentFragment instanceof SearchFragment) {
                            ((SearchFragment) currentFragment).handlerSaveHistorySearch(searchQuery);
                            openSearchDocumentFragment(searchQuery);
                        }
                        else if (currentFragment instanceof SearchDocumentFragment) {
                            openSearchDocumentFragment(searchQuery);
                        }
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
                        Fragment currentFragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);
                        if(currentFragment instanceof SearchFragment){
                            SearchFragment searchFragment = (SearchFragment) currentFragment;
                            if(!query.isEmpty()){
                                searchFragment.handlerEditSearchChange(query);
                            }
                            else{
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

    public void openSearchDocumentFragment(String query){
        SearchDocumentFragment fragment = new SearchDocumentFragment();
        Bundle args = new Bundle();
        args.putString("SEARCH_QUERY", query);
        args.putString("sortType","relevance");
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void handlerImgFilter(){
        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });
    }

    private void showFilterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(view);

        ChipGroup cgCategory = view.findViewById(R.id.cgCategory);
        ChipGroup cgRating = view.findViewById(R.id.cgRating);
        RangeSlider rangeSlider = view.findViewById(R.id.rangeSlider);

        ApiService.apiService.getListCategory().enqueue(new Callback<ResponseData<List<CategoryDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<CategoryDto>>> call, Response<ResponseData<List<CategoryDto>>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<CategoryDto> categories = response.body().getData();
                    for(CategoryDto category : categories){
                        Chip chip = new Chip(SearchActivity.this,null, R.style.ChipTheme);
                        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        chip.setLayoutParams(layoutParams);
                        chip.setCheckable(true);
                        chip.setClickable(true);
                        chip.setId(category.getCateId().intValue());
                        chip.setText(category.getCateName());
                        cgCategory.addView(chip);
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<CategoryDto>>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm các nút Apply và Cancel
        builder.setPositiveButton("Áp dụng", (dialog, which) -> {
            // Lấy danh sách category id dạng List<Long>
            List<Long> selectedCateIdsList = new ArrayList<>();
            for (int i = 0; i < cgCategory.getChildCount(); i++) {
                Chip chip = (Chip) cgCategory.getChildAt(i);
                if (chip.isChecked()) {
                    Log.d("TESTFILTER", "" + chip.getId());
                    selectedCateIdsList.add((long) chip.getId());
                }
            }
            // Chuyển sang mảng Long[]
            Long[] selectedCateIds = selectedCateIdsList.isEmpty()
                    ? new Long[0]
                    : selectedCateIdsList.toArray(new Long[selectedCateIdsList.size()]);

            // Lấy danh sách rating dạng List<Integer>
            List<Integer> selectedRatingList = new ArrayList<>();
            for (int i = 0; i < cgRating.getChildCount(); i++) {
                Chip chip = (Chip) cgRating.getChildAt(i);
                if (chip.isChecked()) {
                    Log.d("TESTFILTER", "" + chip.getText());
                    String ratingText = chip.getText().toString().split(" ")[0];
                    selectedRatingList.add(Integer.parseInt(ratingText));
                }
            }
            // Chuyển sang mảng Integer[]
            Integer[] selectedRating = selectedRatingList.isEmpty()
                    ? new Integer[0]
                    : selectedRatingList.toArray(new Integer[selectedRatingList.size()]);

            // Lấy giá trị từ RangeSlider
            List<Float> priceValue = rangeSlider.getValues();
            Double minPrice = priceValue.get(0) > 0 ? (double) priceValue.get(0) : null;
            Double maxPrice = priceValue.get(1) < 100000 ? (double) priceValue.get(1) : null;

            // Tạo đối tượng FilterCriteria với các mảng được truyền vào
            FilterCriteria criteria = new FilterCriteria(
                    selectedCateIds,
                    selectedRating,
                    minPrice,
                    maxPrice
            );

            // Cập nhật ViewModel với tiêu chí lọc
            ShareViewModel viewModel = new ViewModelProvider(SearchActivity.this).get(ShareViewModel.class);
            viewModel.setFilterCriteria(criteria);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}