package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentUserDetailInfoBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.request.UserRegisterRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.FilePickerUtils;
import vn.anhkhoa.projectwebsitebantailieu.utils.LocalDateTimeAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDetailInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailInfoFragment extends Fragment implements FilePickerUtils.FilePickerCallback{
    FragmentUserDetailInfoBinding binding;
    private UserRegisterRequest userRegisterRequest;
    private ArrayAdapter<String> adapter;
    private FilePickerUtils filePickerUtils;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;
    private String imageUrl;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserDetailInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDetailInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDetailInfoFragment newInstance(String param1, String param2) {
        UserDetailInfoFragment fragment = new UserDetailInfoFragment();
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
        binding = FragmentUserDetailInfoBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userRegisterRequest = (UserRegisterRequest) bundle.getSerializable("user");
        }
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentUserDetailLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupGenderInput();
        filePickerUtils = new FilePickerUtils(this, this);

        View.OnClickListener showDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year  = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day   = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        requireContext(),
                        (view1, y, m, d) -> {
                            // Định dạng: dd/MM/yyyy
                            String formatted = String.format("%02d/%02d/%04d", d, m + 1, y);
                            binding.edtBirthday.setText(formatted);

                            // Lưu vào UserRegisterRequest
                            LocalDate selectedDate = LocalDate.of(y, m + 1, d);
                            userRegisterRequest.setDob(selectedDate);
                        },
                        year, month, day
                );
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        };

        binding.ivEditAvatar.setOnClickListener(v -> {
            // Mở picker chỉ hình, cho phép chọn nhiều (nếu cần)
            filePickerUtils.checkPermissionAndPick(FilePickerUtils.PICKER_TYPE_IMAGE);
        });

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUpdate();
            }
        });

        // Gắn cho cả EditText và icon endIcon
        binding.edtBirthday.setFocusable(false);
        binding.edtBirthday.setClickable(true);
        binding.edtBirthday.setOnClickListener(showDatePicker);

        binding.tilBirthday.setEndIconDrawable(R.drawable.date_of_birth);
        binding.tilBirthday.setEndIconOnClickListener(showDatePicker);

        bindView(userRegisterRequest);
    }

    @Override
    public void onFilesPicked(List<File> files, int currentPickerType) {
        if (files.isEmpty()) {
            Toast.makeText(getContext(), "Không có file nào được chọn", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = files.get(0);
        binding.civAvatar.setImageURI(Uri.fromFile(file));  // hiển thị tạm
        uploadToCloudinary(file);
    }

    private void uploadToCloudinary(File file) {
        // Tạo RequestBody và MultipartBody
        RequestBody reqFile = RequestBody.create(
                MediaType.parse("image/*"),
                file
        );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), reqFile);

        ApiService.apiService.uploadAvatar(body).enqueue(new Callback<ResponseData<String>>() {
            @Override
            public void onResponse(Call<ResponseData<String>> call, Response<ResponseData<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    imageUrl = response.body().getData();
                } else {
                    Toast.makeText(getContext(), "Upload thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmUpdate(){
        String name = binding.edtName.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String phone = binding.edtPhone.getText().toString();
        String address = binding.edtAddress.getText().toString();
        String gender = binding.actvGender.getText().toString();
        String input = binding.edtBirthday.getText().toString();
        
        // Chuyển đổi từ định dạng dd/MM/yyyy sang yyyy/MM/dd
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate birthday = LocalDate.parse(input, inputFormatter);
        String formattedDate = birthday.format(outputFormatter);
        
        String avatar = imageUrl;

        UserRegisterRequest user = new UserRegisterRequest(name, email, avatar, phone, address, gender, birthday);
        ApiService.apiService.updateUser(user).enqueue(new Callback<ResponseData<UserRegisterRequest>>() {
            @Override
            public void onResponse(Call<ResponseData<UserRegisterRequest>> call, Response<ResponseData<UserRegisterRequest>> response) {
                if(response.isSuccessful() && response.body().getData() != null){
                    UserRegisterRequest user = response.body().getData();
                    bindView(user);
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<UserRegisterRequest>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupGenderInput() {
        String[] genders = getResources().getStringArray(R.array.gender);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                genders
        );

        binding.actvGender.setAdapter(adapter);
        binding.actvGender.setThreshold(1); // Hiển thị dropdown khi nhập 1 ký tự

        // Xử lý khi chọn item
        binding.actvGender.setOnItemClickListener((parent, view, position, id) -> {
            String selected = parent.getItemAtPosition(position).toString();
            binding.actvGender.setText(selected, false);
            userRegisterRequest.setGender(selected); // Cập nhật giá trị trong userRegisterRequest
        });

        // Đảm bảo dropdown hiển thị khi click vào TextInputLayout
        binding.tilgender.setOnClickListener(v -> {
            binding.actvGender.showDropDown();
        });
    }

    private void bindView(UserRegisterRequest userRegisterRequest) {
        Glide.with(getContext()).load(userRegisterRequest.getAvatar()).into(binding.civAvatar);
        binding.edtName.setText(userRegisterRequest.getName());
        binding.edtEmail.setText(userRegisterRequest.getEmail());
        binding.edtPhone.setText(userRegisterRequest.getPhoneNumber());
        binding.edtAddress.setText(userRegisterRequest.getAddress());
        
        // Xử lý hiển thị giới tính
        if (userRegisterRequest.getGender() != null && !userRegisterRequest.getGender().isEmpty()) {
            binding.actvGender.setText(userRegisterRequest.getGender(), false);
        }

        // Xử lý hiển thị ngày sinh
        if (userRegisterRequest.getDob() != null) {
            LocalDate dob = userRegisterRequest.getDob();
            // Định dạng ngày tháng: dd/MM/yyyy
            String formattedDate = String.format("%02d/%02d/%04d",
                    dob.getDayOfMonth(),
                    dob.getMonthValue(),
                    dob.getYear());
            binding.edtBirthday.setText(formattedDate);
        }
    }


}