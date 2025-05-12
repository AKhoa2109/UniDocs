package vn.anhkhoa.projectwebsitebantailieu.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivitySignInBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.AccountType;
import vn.anhkhoa.projectwebsitebantailieu.enums.Role;
import vn.anhkhoa.projectwebsitebantailieu.model.request.LoginRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OtpRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.UserRegisterRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserResponse;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

public class SignIn extends AppCompatActivity {
    private static final String TAG = "GoogleSignIn";
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    EditText etPass, etEmail;
    ActivitySignInBinding binding;
    SessionManager session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionManager.getInstance(this);

        //neu da login thi chuyen den main
        checkExistingSession();
        setupGoogleSignIn();

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        binding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ForgetPassword.class);
                startActivity(intent);
            }
        });

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Set up Google Sign In button click listener
        binding.civGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void setupGoogleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        handleSignInResult(account);
                    } catch (ApiException e) {
                        handleSignInError(e);
                    }
                });
    }

    private void checkExistingSession() {
        if (session.isLoggedIn()) {
            navigateToMainActivity();
            finish();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(GoogleSignInAccount account) {
        if (account != null) {
            // Kiểm tra thông tin bắt buộc
            if (account.getEmail() == null || account.getId() == null) {
                showError("Thiếu thông tin tài khoản");
                return;
            }

            String email = account.getEmail();
            String name = account.getDisplayName();
            String avatar = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

            // Kiểm tra email đã tồn tại chưa
            checkEmailExists(email, exists -> {
                if (!exists) {
                    // Đăng ký tài khoản mới với thông tin Google
                    registerWithGoogle(email, name, avatar, account);
                } else {
                    // Tiến hành đăng nhập
                    proceedWithGoogleLogin(account);
                }
            });
        }
    }

    private void handleSignInError(ApiException e) {
        String errorMessage = "Lỗi đăng nhập: ";
        switch (e.getStatusCode()) {
            case CommonStatusCodes.NETWORK_ERROR:
                errorMessage += "Lỗi kết nối mạng";
                break;
            case CommonStatusCodes.INTERNAL_ERROR:
                errorMessage += "Lỗi hệ thống";
                break;
            case CommonStatusCodes.SIGN_IN_REQUIRED:
                errorMessage += "Yêu cầu đăng nhập lại";
                break;
            default:
                errorMessage += "Mã lỗi " + e.getStatusCode();
        }
        showError(errorMessage);
        Log.e(TAG, "Google SignIn Error: " + e.getMessage());
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đăng xuất khi activity bị hủy (tùy chọn)
        mGoogleSignInClient.signOut();
    }

    public void login(){
        String email = binding.etEmail.getText().toString();
        String pass = binding.etPassword.getText().toString();
        LoginRequest loginRequest = new LoginRequest(email, pass);
        ApiService.apiService.login(loginRequest).enqueue(new Callback<ResponseData<UserResponse>>() {
            @Override
            public void onResponse(Call<ResponseData<UserResponse>> call, Response<ResponseData<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ResponseData<UserResponse> responseData = response.body();
                    //kiểm tra dữ liệu rỗng
                    if (responseData != null && responseData.getData() != null) {
                        UserResponse userResponse = responseData.getData();
                        Log.d("Login", "User: " + userResponse.getName());
                        session.saveUser(userResponse);

                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        intent.putExtra("user", userResponse);
                        startActivity(intent);
                    } else {
                        Log.e("Login", "Response data is null or empty.");
                        Toast.makeText(SignIn.this, "Đăng nhập thất bại! Dữ liệu trống.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignIn.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("Login", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<UserResponse>> call, Throwable t) {
                Toast.makeText(SignIn.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmailExists(String email, OnEmailCheckListener listener) {
        ApiService.apiService.checkEmail(email).enqueue(new Callback<ResponseData<Boolean>>() {
            @Override
            public void onResponse(Call<ResponseData<Boolean>> call, Response<ResponseData<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exists = response.body().getData();
                    listener.onResult(exists);
                } else {
                    showError("Lỗi kiểm tra email");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Boolean>> call, Throwable t) {
                showError("Lỗi kết nối");
            }
        });
    }

    interface OnEmailCheckListener {
        void onResult(boolean exists);
    }

    private void proceedWithGoogleLogin(GoogleSignInAccount account) {
        UserResponse user = new UserResponse(
                0,
                account.getEmail(),
                account.getDisplayName(),
                account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : ""
        );

        session.saveUser(user);

        navigateToMainActivity();
        showSuccess("Đăng nhập thành công với Google!");
    }

    private void registerWithGoogle(String email, String name, String avatar, GoogleSignInAccount account) {
        UserRegisterRequest userDto = new UserRegisterRequest();
        userDto.setEmail(email);
        userDto.setName(name);
        userDto.setPassword("");
        userDto.setPhoneNumber("");
        userDto.setRole(Role.USER);
        userDto.setAvatar(avatar);
        userDto.setType(AccountType.GOOGLE);

        ApiService.apiService.register(userDto).enqueue(new Callback<ResponseData<OtpRequest>>() {
            @Override
            public void onResponse(Call<ResponseData<OtpRequest>> call, Response<ResponseData<OtpRequest>> response) {
                if (response.isSuccessful()) {
                    // Giả định backend tự động active tài khoản khi đăng ký bằng Google
                    proceedWithGoogleLogin(account);
                } else {
                    showError("Đăng ký thất bại");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<OtpRequest>> call, Throwable t) {
                showError("Lỗi kết nối");
            }
        });
    }
}