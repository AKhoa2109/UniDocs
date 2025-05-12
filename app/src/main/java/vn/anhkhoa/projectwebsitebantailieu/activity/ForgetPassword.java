package vn.anhkhoa.projectwebsitebantailieu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivityForgetPasswordBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivityResetPasswordBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OtpRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

public class ForgetPassword extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.btnSubmitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.edtEmail.getText().toString();
                String phoneNumber = binding.edtPhoneNumber.getText().toString();
                getApiForgotPassword(email,phoneNumber);
            }
        });

    }

    private void getApiForgotPassword(String email, String phoneNumber){
        ApiService.apiService.forgotPassword(email,phoneNumber).enqueue(new Callback<ResponseData<OtpRequest>>() {
            @Override
            public void onResponse(Call<ResponseData<OtpRequest>> call, Response<ResponseData<OtpRequest>> response) {
                if(response.isSuccessful() && response.body() != null){
                    OtpRequest otpRequest = response.body().getData();
                    Intent intent = new Intent(ForgetPassword.this, VerificationOTP.class);
                    intent.putExtra("otp_response", otpRequest);
                    intent.putExtra("fp","forgetPassword");
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<OtpRequest>> call, Throwable t) {
                ToastUtils.show(ForgetPassword.this, "Lỗi");
            }
        });
    }
}