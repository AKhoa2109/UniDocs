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
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentUserDetailInfoBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.request.UserRegisterRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.FilePickerUtils;
import vn.anhkhoa.projectwebsitebantailieu.utils.LocalDateTimeAdapter;
import vn.anhkhoa.projectwebsitebantailieu.utils.LoadingDialog;
import vn.anhkhoa.projectwebsitebantailieu.utils.CheckValidInput;

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

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            // Hiển thị ảnh đã chọn
                            binding.civAvatar.setImageURI(selectedImageUri);
                            
                            // Chuyển đổi Uri thành File
                            String[] projection = {MediaStore.Images.Media.DATA};
                            android.database.Cursor cursor = requireContext().getContentResolver().query(selectedImageUri, projection, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                String filePath = cursor.getString(columnIndex);
                                cursor.close();
                                
                                File imageFile = new File(filePath);
                                if (imageFile.exists()) {
                                    uploadToCloudinary(imageFile);
                                } else {
                                    Toast.makeText(getContext(), "Không thể truy cập file ảnh", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Không thể truy cập file ảnh", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Không thể xử lý ảnh đã chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentUserDetailLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        setupImagePicker();
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
            // Check for Android 13+ (API 33+) permission
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (requireContext().checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) 
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 100);
                } else {
                    openImagePicker();
                }
            } else {
                // For Android 12 and below, check for legacy storage permission
                if (requireContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) 
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                } else {
                    openImagePicker();
                }
            }
        });

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUpdate();
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).showBottomNavigation();
                }
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Cần quyền truy cập ảnh để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
            }
        }
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

    private boolean checkInput() {
        boolean isValid = true;

        // Check name
        String name = binding.edtName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.tilName.setError("Vui lòng nhập họ tên");
            binding.tilName.setErrorEnabled(true);
            isValid = false;
        } else {
            binding.tilName.setError(null);
            binding.tilName.setErrorEnabled(false);
        }

        // Check email
        String email = binding.edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            binding.tilEmail.setError("Vui lòng nhập email");
            binding.tilEmail.setErrorEnabled(true);
            isValid = false;
        } else if (!CheckValidInput.checkEmail(email)) {
            binding.tilEmail.setError("Email không hợp lệ");
            binding.tilEmail.setErrorEnabled(true);
            isValid = false;
        } else {
            binding.tilEmail.setError(null);
            binding.tilEmail.setErrorEnabled(false);
        }

        // Check phone
        String phone = binding.edtPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            binding.tilPhone.setError("Vui lòng nhập số điện thoại");
            binding.tilPhone.setErrorEnabled(true);
            isValid = false;
        } else if (!CheckValidInput.checkPhoneNumber(phone)) {
            binding.tilPhone.setError("Số điện thoại không hợp lệ");
            binding.tilPhone.setErrorEnabled(true);
            isValid = false;
        } else {
            binding.tilPhone.setError(null);
            binding.tilPhone.setErrorEnabled(false);
        }

        // Check address
        String address = binding.edtAddress.getText().toString().trim();
        if (address.isEmpty()) {
            binding.tilAddress.setError("Vui lòng nhập địa chỉ");
            binding.tilAddress.setErrorEnabled(true);
            isValid = false;
        } else {
            binding.tilAddress.setError(null);
            binding.tilAddress.setErrorEnabled(false);
        }

        // Check gender
        String gender = binding.actvGender.getText().toString().trim();
        if (gender.isEmpty()) {
            binding.tilgender.setError("Vui lòng chọn giới tính");
            binding.tilgender.setErrorEnabled(true);
            isValid = false;
        } else {
            binding.tilgender.setError(null);
            binding.tilgender.setErrorEnabled(false);
        }

        // Check birthday
        String birthday = binding.edtBirthday.getText().toString().trim();
        if (birthday.isEmpty()) {
            binding.tilBirthday.setError("Vui lòng chọn ngày sinh");
            binding.tilBirthday.setErrorEnabled(true);
            isValid = false;
        } else if (!CheckValidInput.checkAge(birthday)) {
            binding.tilBirthday.setError("Bạn phải đủ 18 tuổi");
            binding.tilBirthday.setErrorEnabled(true);
            isValid = false;
        } else {
            binding.tilBirthday.setError(null);
            binding.tilBirthday.setErrorEnabled(false);
        }

        return isValid;
    }

    private void confirmUpdate(){
        // Validate all inputs
        if (!checkInput()) {
            return;
        }

        // Show loading dialog
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.show(getChildFragmentManager(), "loading");

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
                // Dismiss loading dialog
                loadingDialog.dismiss();
                
                if(response.isSuccessful() && response.body().getData() != null){
                    UserRegisterRequest user = response.body().getData();
                    bindView(user);
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<UserRegisterRequest>> call, Throwable t) {
                // Dismiss loading dialog
                loadingDialog.dismiss();
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
        Glide.with(requireContext()).load(userRegisterRequest.getAvatar()).error(R.drawable.avatar_nam).into(binding.civAvatar);
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