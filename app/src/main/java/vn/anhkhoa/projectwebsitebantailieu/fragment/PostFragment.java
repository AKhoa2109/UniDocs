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

import com.google.gson.Gson;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentPostBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.DocumentType;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.FilePickerUtils;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment implements FilePickerUtils.FilePickerCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private FilePickerUtils filePickerUtils;
    private FragmentPostBinding binding;
    private SessionManager sessionManager;
    private List<CategoryDto> categoryDtoList;
    private ArrayAdapter categoryAdapter;
    //danh muc duoc chon
    private Long selectedCateId = null;
    private List<File> selectedImageFiles = new ArrayList<>();

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.buttonUpload.setEnabled(!show);
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
        binding = FragmentPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View mainView = binding.fragmentPost;
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sessionManager = SessionManager.getInstance(requireContext());
        filePickerUtils = new FilePickerUtils(this, this);

        //lay id category khi duoc chon
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CategoryDto selectedCategory = categoryDtoList.get(i);
                selectedCateId = selectedCategory.getCateId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //load category
        categoryDtoList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item , categoryDtoList);
        binding.spinnerCategory.setAdapter(categoryAdapter);
        ApiService.apiService.getListCategory().enqueue(new Callback<ResponseData<List<CategoryDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<CategoryDto>>> call, Response<ResponseData<List<CategoryDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseData<List<CategoryDto>> responseData = response.body();
                    if (responseData.getStatus() == 200) {
                        List<CategoryDto> categories = responseData.getData();
                        categoryDtoList.clear();
                        categoryDtoList.addAll(categories);

                        categoryAdapter.notifyDataSetChanged(); // Cập nhật giao diện Spinner
                    } else {
                        ToastUtils.show(binding.getRoot().getContext(), "Co loi xay ra API tai category");
                    }
                } else {
                    ToastUtils.show(binding.getRoot().getContext(), "Co loi xay ra API tai category");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<CategoryDto>>> call, Throwable t) {
                ToastUtils.show(binding.getRoot().getContext(), "Loi ket noi internet!");
            }
        });

//        binding.buttonUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DocumentDto documentDto = getFormData();
//                if(documentDto == null) return;
//                ApiService.apiService.pushDocument(documentDto).enqueue(new Callback<ResponseData<DocumentDto>>() {
//                    @Override
//                    public void onResponse(Call<ResponseData<DocumentDto>> call, Response<ResponseData<DocumentDto>> response) {
//                        if(response.isSuccessful() && response.body() != null){
//                            ResponseData<DocumentDto> responseData = response.body();
//                            if (responseData.getStatus() == 200){
//                                ToastUtils.show(binding.getRoot().getContext(), "Tai len thanh cong: " + responseData.getData().getDocName());
//                            }else {
//                                ToastUtils.show(binding.getRoot().getContext(), "Loi API");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseData<DocumentDto>> call, Throwable t) {
//                        ToastUtils.show(binding.getRoot().getContext(), "Loi ket noi internet!");
//                    }
//                });
//            }
//        });

        binding.buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePickerUtils.checkPermissionAndPick(FilePickerUtils.PICKER_TYPE_IMAGE);
            }
        });

        binding.buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentDto documentDto = getFormData();
                Gson gson = new Gson();
                String documentJson = gson.toJson(documentDto);
                RequestBody documentBody = RequestBody.create(MediaType.parse("application/json"), documentJson);

                List<MultipartBody.Part> fileParts = new ArrayList<>();
                if (!selectedImageFiles.isEmpty()) {
                    for (File file : selectedImageFiles) {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                        MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), requestFile);
                        fileParts.add(part);
                    }
                }

                if(documentDto == null) return;
                showLoading(true);
                ApiService.apiService.pushDocumentMedia(documentBody, fileParts).enqueue(new Callback<ResponseData<DocumentDto>>() {
                    @Override
                    public void onResponse(Call<ResponseData<DocumentDto>> call, Response<ResponseData<DocumentDto>> response) {
                        showLoading(false);
                        if(response.isSuccessful() && response.body() != null){
                            ResponseData<DocumentDto> responseData = response.body();
                            if (responseData.getStatus() == 200){
                                ToastUtils.show(binding.getRoot().getContext(), "Tai len thanh cong: " + responseData.getData().getDocName());
                            }else {
                                ToastUtils.show(binding.getRoot().getContext(), "Loi API");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData<DocumentDto>> call, Throwable t) {
                        showLoading(false);
                        ToastUtils.show(binding.getRoot().getContext(), "Loi ket noi internet!");
                    }
                });
            }
        });
    }

    public DocumentDto getFormData(){
        DocumentDto documentDto = new DocumentDto();

        String docName = binding.editTextDocName.getText().toString().trim();
        String sellPriceStr = binding.editTextSellPrice.getText().toString().trim();
        String originalPriceStr = binding.editTextOriginalPrice.getText().toString().trim();
        String docPageStr = binding.editTextDocPage.getText().toString().trim();
        String docDesc = binding.editTextDocDescription.getText().toString().trim();


        if (docName.isEmpty()) {
            ToastUtils.show(requireContext(),"Vui lòng nhập tên tài liệu");
            return null;
        }
        documentDto.setDocName(docName);

        if (sellPriceStr.isEmpty()) {
            ToastUtils.show(requireContext(),"Giá bán không hợp lệ");
            return null;
        }
        try {
            documentDto.setSellPrice(Double.valueOf(sellPriceStr));
        } catch (NumberFormatException e) {
            ToastUtils.show(requireContext(),"Giá bán không hợp lệ");

            return null;
        }

        if (originalPriceStr.isEmpty()) {
            ToastUtils.show(requireContext(),"Giá ban đầu không hợp lệ");
            return null;
        }
        try {
            documentDto.setOriginalPrice(Double.valueOf(originalPriceStr));
        } catch (NumberFormatException e) {
            ToastUtils.show(requireContext(),"Giá ban đầu không hợp lệ");
            return null;
        }

        if (docPageStr.isEmpty()) {
            ToastUtils.show(requireContext(),"Số trang không hợp lệ");
            return null;
        }
        try {
            documentDto.setDocPage(Integer.valueOf(docPageStr));
        } catch (NumberFormatException e) {
            ToastUtils.show(requireContext(),"Số trang không hợp lệ");
            return null;
        }
        documentDto.setDocPage(Integer.valueOf(docPageStr));

        documentDto.setDocDesc(docDesc);

        //type doc
        int checkedRadioButtonId = binding.radioGroupDocType.getCheckedRadioButtonId();
        if(checkedRadioButtonId == binding.radioButtonPhysical.getId()){
            documentDto.setType(DocumentType.PHYSICAL);
        }else if (checkedRadioButtonId == binding.radioButtonDigital.getId()){
            documentDto.setType(DocumentType.DIGITAL);
        }else if (checkedRadioButtonId == binding.radioButtonBoth.getId()){
            documentDto.setType(DocumentType.BOTH);
        } else {
            ToastUtils.show(requireContext(),"lòng chọn loại tài liệu");
            return null;
        }
        //documentDto.setMaxQuantity(binding.);
        //file, anh
        documentDto.setDocImageUrl("day la link");
        //bo not null
        documentDto.setMaxQuantity(10);

        documentDto.setUserId(sessionManager.getUser().getUserId());
        documentDto.setCateId(selectedCateId);

        return documentDto;
    }

    @Override
    public void onFilesPicked(List<File> files, int currentPickerType) {
        if (currentPickerType == FilePickerUtils.PICKER_TYPE_IMAGE) {
            selectedImageFiles.addAll(files);
//            selectedImageAdapter.notifyDataSetChanged();
//            //kiem tra neu co hinh thì hien thi khung hinh
//            updateSelectedImagesVisibility();
        }
    }

//    public DocumentDto getFormData(){
//        DocumentDto documentDto = new DocumentDto();
//        documentDto.setDocName(binding.editTextDocName.getText().toString());
//        //documentDto.setDocImageUrl(documentDto.getDocImageUrl());
//        //file
//        documentDto.setSellPrice(Double.valueOf(binding.editTextSellPrice.getText().toString()));
//        documentDto.setOriginalPrice(Double.valueOf(binding.editTextOriginalPrice.getText().toString()));
//        documentDto.setDocPage(Integer.valueOf(binding.editTextDocPage.getText().toString()));
//
//        documentDto.setDocDesc(binding.editTextDocDescription.getText().toString());
//
//        //type doc
//        int checkedRadioButtonId = binding.radioGroupDocType.getCheckedRadioButtonId();
//        if(checkedRadioButtonId == binding.radioButtonPhysical.getId()){
//            documentDto.setType(DocumentType.PHYSICAL);
//        }else if (checkedRadioButtonId == binding.radioButtonDigital.getId()){
//            documentDto.setType(DocumentType.DIGITAL);
//        }else{
//            documentDto.setType(DocumentType.BOTH);
//        }
//        //documentDto.setMaxQuantity(binding.);
//
//        documentDto.setUserId(sessionManager.getUser().getUserId());
//
//        return documentDto;
//    }
}