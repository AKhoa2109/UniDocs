package vn.anhkhoa.projectwebsitebantailieu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivitySignUpBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.Role;
import vn.anhkhoa.projectwebsitebantailieu.model.request.UserRegisterRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OtpRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.CheckValidInput;
import vn.anhkhoa.projectwebsitebantailieu.utils.LoadingDialog;

public class SignUp extends AppCompatActivity {
    private UserRegisterRequest request;
    private LoadingDialog loadingDialog;
    ActivitySignUpBinding binding;

    public interface EmailCheckCallback {
        void onResult(boolean exists);
        void onError(Throwable t);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnSignup.setOnClickListener(view -> {
            String email = binding.edtEmail.getText().toString();
            String password = binding.edtPassword.getText().toString();
            String confirmPassword = binding.edtConfirmPassword.getText().toString();

            showLoading();

            getApiCheckEmail(email, new EmailCheckCallback() {
                @Override
                public void onResult(boolean exists) {
                    if (exists) {
                        dismissLoading();
                        binding.tilEmail.setError(getString(R.string.error_exist_email));
                    } else {
                        boolean isValid = validateInput(email,password,confirmPassword);
                        if (isValid) {
                            request = new UserRegisterRequest("Khoa",email,password,"khoa.png", Role.USER,false);
                            getApiRegister(request);
                        }
                        else{
                            dismissLoading();
                        }
                    }
                }

                @Override
                public void onError(Throwable t) {
                    // Xử lý lỗi gọi API
                    dismissLoading();
                    binding.tilEmail.setError("Không thể kiểm tra email. Vui lòng thử lại.");
                }
            });
        });
    }


    private boolean validateInput(String email, String password, String confirmPassword){
        boolean isValid = true;
        if(!CheckValidInput.checkEmail(email)){
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        else if(!CheckValidInput.checkPassword(password)){
            binding.tilPassword.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }
        else if(!CheckValidInput.checkConfirmPassword(password,confirmPassword)){
            binding.tilConfirmPassword.setError(getString(R.string.error_invalid_confirm_password));
            isValid = false;
        }
        return  isValid;
    }


    private void getApiRegister(UserRegisterRequest request){
        ApiService.apiService.register(request).enqueue(new Callback<ResponseData<OtpRequest>>() {
            @Override
            public void onResponse(Call<ResponseData<OtpRequest>> call, Response<ResponseData<OtpRequest>> response) {
                dismissLoading();
                if(response.isSuccessful()){
                    OtpRequest otpRequest = response.body().getData();
                    Intent intent = new Intent(SignUp.this, VerificationOTP.class);
                    intent.putExtra("otp_response", otpRequest);
                    startActivity(intent);
                }
                else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d("API_ERROR", "Code: " + response.code() + ", Error: " + errorBody);
                        Toast.makeText(SignUp.this, "Đăng ký lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData<OtpRequest>> call, Throwable t) {
                dismissLoading();
                Log.e("API_FAILURE", "Lỗi mạng: " + t.getMessage());
                Toast.makeText(SignUp.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getApiCheckEmail(String email, EmailCheckCallback callback){
        ApiService.apiService.checkEmail(email).enqueue(new Callback<ResponseData<Boolean>>() {
            @Override
            public void onResponse(Call<ResponseData<Boolean>> call, Response<ResponseData<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean exists = response.body().getData();
                    callback.onResult(exists != null && exists);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Boolean>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    private void showLoading() {
        loadingDialog = new LoadingDialog();
        loadingDialog.show(getSupportFragmentManager(), "loading");
    }

    private void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.dismiss();
        }
    }

}