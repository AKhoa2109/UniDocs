package vn.anhkhoa.projectwebsitebantailieu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivityResetPasswordBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivitySignUpBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OtpRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.PasswordResetRequest;

public class ResetPasswordActivity extends AppCompatActivity {
    ActivityResetPasswordBinding binding;
    private OtpRequest otpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        otpRequest = (OtpRequest) getIntent().getSerializableExtra("otp");
        if (otpRequest != null) {
            Log.d("OTP", "Email: " + otpRequest.getEmail());
            Log.d("OTP", "OTP: " + otpRequest.getOtp());
        }

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPassword()){
                    String password = binding.edtConfirmPassword.getText().toString();
                    PasswordResetRequest passwordResetRequest = new PasswordResetRequest(otpRequest.getEmail(),password,otpRequest.getOtp());
                    getApiVerifyOtpPassReset(passwordResetRequest);
                }
            }
        });
    }

    private void getApiVerifyOtpPassReset(PasswordResetRequest passwordResetRequest){
        ApiService.apiService.verifyOtpForResetPass(passwordResetRequest).enqueue(new Callback<ResponseData<String>>() {
            @Override
            public void onResponse(Call<ResponseData<String>> call, Response<ResponseData<String>> response) {
                if(response.isSuccessful() && response.body() != null){
                    handleServerMessage(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<String>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPassword(){
        String password = binding.edtPassword.getText().toString();
        String confirmPassword = binding.edtConfirmPassword.getText().toString();
        if(!password.equals(confirmPassword)){
            return false;
        }
        return true;
    }

    private void handleServerMessage(String msg) {
        if (msg.equals(getString(R.string.user_not_found))) {
            Toast.makeText(this, getString(R.string.user_not_found), Toast.LENGTH_LONG).show();
        }
        else if (msg.equals(getString(R.string.successfull)) ) {
            Toast.makeText(this, R.string.successfull, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ResetPasswordActivity.this, SignIn.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}