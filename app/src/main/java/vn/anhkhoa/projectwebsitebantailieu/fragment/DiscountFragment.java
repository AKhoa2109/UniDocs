package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentDiscountBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.DiscountStatus;
import vn.anhkhoa.projectwebsitebantailieu.enums.DiscountType;
import vn.anhkhoa.projectwebsitebantailieu.enums.Scope;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DiscountDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiscountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscountFragment.
     */

    private FragmentDiscountBinding binding;
    DiscountDto discountDto = new DiscountDto();
    ArrayAdapter<String> docAdapter;
    ArrayAdapter<String> catAdapter;
    private SessionManager session;
    // TODO: Rename and change types and number of parameters
    public static DiscountFragment newInstance(String param1, String param2) {
        DiscountFragment fragment = new DiscountFragment();
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
        binding = FragmentDiscountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        session = SessionManager.getInstance(requireActivity());
        initView();
        loadData();
    }

    private void loadData() {
        //Load categories
        ApiService.apiService
                .getCategoryByUserId(session.getUser().getUserId())
                .enqueue(new Callback<ResponseData<List<CategoryDto>>>() {
                    @Override
                    public void onResponse(Call<ResponseData<List<CategoryDto>>> call,
                                           Response<ResponseData<List<CategoryDto>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryNames.clear();
                            categoryIds.clear();
                            for (CategoryDto dto : response.body().getData()) {
                                categoryNames.add(dto.getCateName());  // hoặc getter tương ứng
                                categoryIds.add(dto.getCateId());
                            }
                            catAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseData<List<CategoryDto>>> call, Throwable t) {
                        ToastUtils.show(requireContext(), "Không tải được danh mục");
                    }
                });

        // Load documents
        ApiService.apiService
                .getAllDocumentByUserId(session.getUser().getUserId())
                .enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
                    @Override
                    public void onResponse(Call<ResponseData<List<DocumentDto>>> call,
                                           Response<ResponseData<List<DocumentDto>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            documentNames.clear();
                            documentIds.clear();
                            for (DocumentDto dto : response.body().getData()) {
                                documentNames.add(dto.getDocName()); // hoặc getter tương ứng
                                documentIds.add(dto.getDocId());
                            }
                            docAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                        ToastUtils.show(requireContext(), "Không tải được tai lieu");
                    }
                });
    }

    // Dữ liệu Demo: tên và id (thường bạn load từ API)
    private List<String> categoryNames = new ArrayList<>();
    private List<Long>   categoryIds   = new ArrayList<>();

    private List<String> documentNames = new ArrayList<>();
    private List<Long>   documentIds   = new ArrayList<>();


    private void initView() {

        //chon ngay bat dau
        setupDateTimePicker(
                binding.buttonStartDate,
                dateTime ->{
                    binding.textViewStartDate.setText(
                            dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                    discountDto.setStartDate(dateTime);
                }
        );
        //chon ngay ket thuc
        setupDateTimePicker(
                binding.buttonEndDate,
                dateTime ->{
                    binding.textViewEndDate.setText(
                            dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                    discountDto.setEndAt(dateTime);
                }
        );

        // adapter cho list category
        catAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, categoryNames);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(catAdapter);

        //adapter cho list document
        docAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, documentNames);
        docAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDocument.setAdapter(docAdapter);

        //scope
        AtomicReference<Scope> scope = new AtomicReference<>(Scope.SHOP);

        binding.radioGroupScope.setOnCheckedChangeListener((group, scopeSelectedId) -> {
//           an 2 spinner
            binding.spinnerCategory.setVisibility(View.GONE);
            binding.spinnerDocument.setVisibility(View.GONE);
//            Reset selection của cả hai spinner về 0
            binding.spinnerCategory.setSelection(0, false);
            binding.spinnerDocument.setSelection(0, false);

            if (scopeSelectedId == R.id.radioButtonShop){
                scope.set(Scope.SHOP);
                //shop khong can id
                discountDto.setScopeId(null);
            }else if (scopeSelectedId == R.id.radioButtonCategory){
                binding.spinnerCategory.setVisibility(View.VISIBLE);
                scope.set(Scope.CATEGORY);
            }else if (scopeSelectedId == R.id.radioButtonDocument){
                binding.spinnerDocument.setVisibility(View.VISIBLE);
                scope.set(Scope.DOCUMENT);
            }
        });

        //btn creat discount
        binding.buttonCreateDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //neu sai thong tin thi khong tao
                if(validateCreateDiscout())
                    return;

                discountDto.setDiscountName(binding.editTextDiscountName.getText().toString().trim());
                discountDto.setScope(scope.get());
                if (discountDto.getScope() == Scope.SHOP){
                    discountDto.setScopeId(null);
                }else if (discountDto.getScope() == Scope.CATEGORY){
                    int docPos = binding.spinnerDocument.getSelectedItemPosition();
                    discountDto.setScopeId(documentIds.get(docPos));
                }else {
                    int catPos = binding.spinnerCategory.getSelectedItemPosition();
                    discountDto.setScopeId(categoryIds.get(catPos));
                }
                int typeSelectedId = binding.radioGroupDiscountType.getCheckedRadioButtonId();
                if (typeSelectedId == R.id.radioButtonPercent){
                    discountDto.setDiscountType(DiscountType.PERCENT);
                }else{
                    discountDto.setDiscountType(DiscountType.FIXED);
                }

                discountDto.setStatus(DiscountStatus.ACTIVE);
                discountDto.setUsageLimit(Integer.valueOf(binding.editTextUsageLimit.getText().toString().trim()));
                discountDto.setDiscountValue(Double.valueOf(binding.editTextDiscountValue.getText().toString().trim()));
                discountDto.setMinPrice(Double.valueOf(binding.editTextMinPrice.getText().toString().trim()));
                discountDto.setMaxPrice(Double.valueOf(binding.editTextMaxPrice.getText().toString().trim()));

                Long userId = session.getUser().getUserId();
                ApiService.apiService.addDiscount( userId,discountDto).enqueue(new Callback<ResponseData<Long>>() {
                    @Override
                    public void onResponse(Call<ResponseData<Long>> call, Response<ResponseData<Long>> response) {
                        if (response.isSuccessful() && response.body() != null){
                            Long discountId = response.body().getData();
                            ToastUtils.show(requireContext(), "Tao discount thanh cong, ID = " + discountId);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData<Long>> call, Throwable t) {
                        ToastUtils.show(requireContext(), "Khong the ket noi!");
                    }
                });
            }
        });

    }

    private boolean validateCreateDiscout(){
        boolean hasError = false;
        // Clear lỗi cũ
        binding.inputLayoutDiscountName.setError(null);
        binding.inputLayoutScopeId.setError(null);
        binding.inputLayoutDiscountValue.setError(null);
        binding.inputLayoutUsageLimit.setError(null);
        binding.inputLayoutMinPrice.setError(null);
        binding.inputLayoutMaxPrice.setError(null);

        // Tên mã giảm giá không được để trống
        String name = binding.editTextDiscountName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.inputLayoutDiscountName.setError("Vui lòng nhập tên mã giảm giá");
            hasError = true;
        }

        // 3. Phải chọn scope
        int checkedScopeId = binding.radioGroupScope.getCheckedRadioButtonId();
        if (checkedScopeId == -1) {
            ToastUtils.show(requireContext(), "Vui lòng chọn phạm vi áp dụng");
            hasError = true;
        }

        //  Kiểu giảm giá
        int checkedTypeId = binding.radioGroupDiscountType.getCheckedRadioButtonId();
        if (checkedTypeId == -1) {
            ToastUtils.show(requireContext(), "Vui lòng chọn loại giảm giá");
            hasError = true;
        }

        //Số lần sử dụng tối đa (>=1)
        String usageStr = binding.editTextUsageLimit.getText().toString().trim();
        if (usageStr.isEmpty()) {
            binding.inputLayoutUsageLimit.setError("Vui lòng nhập số lần sử dụng");
            hasError = true;
        } else {
            int usage = Integer.parseInt(usageStr);
            if (usage <= 0) {
                binding.inputLayoutUsageLimit.setError("Phải ≥ 1");
                hasError = true;
            }
        }

        // Giá trị đơn hàng tối thiểu (>0)
        String minStr = binding.editTextMinPrice.getText().toString().trim();
        if (minStr.isEmpty()) {
            binding.inputLayoutMinPrice.setError("Vui lòng nhập giá trị tối thiểu");
            hasError = true;
        }

        // Giá trị đơn hàng tối đa
        String maxStr = binding.editTextMaxPrice.getText().toString().trim();
        if (maxStr.isEmpty()) {
            binding.inputLayoutMaxPrice.setError("Vui lòng nhập giá trị tối đa có thể giảm");
            hasError = true;
        }

        String valueStr = binding.editTextDiscountValue.getText().toString().trim();
        int selectedType =  binding.radioGroupDiscountType.getCheckedRadioButtonId();
        if (valueStr.isEmpty()){
            binding.inputLayoutDiscountValue.setError("Vui lòng nhập giá trị giảm giá");
            hasError = true;
        }else if (selectedType == R.id.radioButtonPercent){
            double discountValue = Double.parseDouble(valueStr);
            if(discountValue<1 || discountValue > 100){
                binding.inputLayoutDiscountValue.setError("Phần trăm giảm giá phải từ 1% đến 100%");
                hasError = true;
            }
        }else if (selectedType == R.id.radioButtonFixed){
            double discountValue = Double.parseDouble(valueStr);
            if(discountValue <=0 ){
                binding.inputLayoutDiscountValue.setError("Giá trị giảm giá phải lớn hơn 0");
                hasError = true;
            }
        }

        LocalDateTime startDate = discountDto.getStartDate();
        LocalDateTime endDate = discountDto.getEndAt();
        if (startDate == null || endDate == null){
            ToastUtils.show(requireContext(), "Vui lòng chọn đủ ngày bắt đầu và kết thúc");
            hasError = true;
        }else {
            if (endDate.isBefore(startDate)){
                ToastUtils.show(requireContext(), "Ngày kết thúc phải sau ngày bắt đầu");
                hasError = true;
            }
        }

        return hasError;
    }
    private void setupDateTimePicker(MaterialButton button, Consumer<LocalDateTime> onSelected) {
        button.setOnClickListener(v -> {
            // Bước 1: chọn ngày
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Chọn ngày")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                LocalDate date = Instant.ofEpochMilli(selection)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                onSelected.accept(date.atStartOfDay());
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });
    }
}