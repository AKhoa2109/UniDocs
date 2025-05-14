package vn.anhkhoa.projectwebsitebantailieu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivityVerificationOtpBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OtpRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.PasswordResetRequest;

public class VerificationOTP extends AppCompatActivity {
    ActivityVerificationOtpBinding binding;
    private OtpRequest otpRequest;
    private CountDownTimer countDownTimer;
    private static final long TOTAL_TIME = 5 * 60 * 1000;
    private boolean isExpired = false;
    private String functional;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityVerificationOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        otpRequest = (OtpRequest) getIntent().getSerializableExtra("otp_response");
        functional = (String) getIntent().getStringExtra("fp");
        if (otpRequest != null) {
            Log.d("OTP", "Email: " + otpRequest.getEmail());
            Log.d("OTP", "OTP: " + otpRequest.getOtp());
        }
        startOtpCountdown();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(functional.equals("signup")){
                    handlerConfirmSUClick();
                }
                else{
                    handlerConfirmFPClick();
                }
            }
        });
    }
    private void startOtpCountdown() {
        countDownTimer = new CountDownTimer(TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Tính phút và giây còn lại
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                // Hiển thị dưới dạng mm:ss
                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                binding.tvTimer.setText(time);
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("00:00");
                Toast.makeText(VerificationOTP.this, "OTP đã hết hạn. Vui lòng yêu cầu lại.", Toast.LENGTH_SHORT).show();
                isExpired = true;
                binding.btnConfirm.setEnabled(false);
            }
        }.start();

        binding.btnConfirm.setEnabled(true);
    }

    private void handlerConfirmSUClick(){
        if (isExpired) {
            Toast.makeText(this, "Không thể xác nhận: OTP đã hết hạn. Vui lòng gửi lại mã mới.", Toast.LENGTH_SHORT).show();
            return;
        }
        String otp = binding.pinView.getText().toString();
        if(otp.length()<6){
            Toast.makeText(this, getString(R.string.otp_invalid_lenght), Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            getAPIVerifyOTP(otpRequest);
        }
    }

    private void handlerConfirmFPClick(){
        if (isExpired) {
            Toast.makeText(this, "Không thể xác nhận: OTP đã hết hạn. Vui lòng gửi lại mã mới.", Toast.LENGTH_SHORT).show();
            return;
        }
        String otp = binding.pinView.getText().toString();
        if(otp.length()<6){
            Toast.makeText(this, getString(R.string.otp_invalid_lenght), Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            getAPICheckOtp(otpRequest);
        }
    }

    private void getAPIVerifyOTP(OtpRequest otpRequest){
        ApiService.apiService.verifyOtpForActivation(otpRequest).enqueue(new Callback<ResponseData<String>>() {
            @Override
            public void onResponse(Call<ResponseData<String>> call, Response<ResponseData<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleServerMessage(response.body().getData());
                } else {
                    Toast.makeText(VerificationOTP.this, getString(R.string.successfull), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<String>> call, Throwable t) {
                Toast.makeText(VerificationOTP.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getAPICheckOtp(OtpRequest otpRequest){
        ApiService.apiService.checkOtp(otpRequest).enqueue(new Callback<ResponseData<String>>() {
            @Override
            public void onResponse(Call<ResponseData<String>> call, Response<ResponseData<String>> response) {
                if(response.isSuccessful() && response.body() != null){
                    handleServerMessage(response.body().getData());
                }
                else{
                    Toast.makeText(VerificationOTP.this, getString(R.string.successfull), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<String>> call, Throwable t) {

            }
        });
    }


    private void handleServerMessage(String msg) {
        if (msg.equals(getString(R.string.user_not_found))) {
            setErrorPinView(getString(R.string.user_not_found));
        }
        else if (msg.equals(getString(R.string.no_otp))) {
            setErrorPinView(getString(R.string.no_otp));
        }
        else if (msg.equals(getString(R.string.otp_expired))) {
            Toast.makeText(this, R.string.otp_expired, Toast.LENGTH_SHORT).show();
        }
        else if (msg.equals(getString(R.string.otp_invalid))) {
            setErrorPinView(getString(R.string.otp_invalid));
        }
        else if (msg.equals(getString(R.string.successfull)) && functional.equals("signup")) {
            Toast.makeText(this, R.string.successfull, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(VerificationOTP.this, SignIn.class);
            startActivity(intent);
        }
        else if (msg.equals(getString(R.string.successfull)) && functional.equals("forgetPassword")) {
            Toast.makeText(this, R.string.successfull, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(VerificationOTP.this, ResetPasswordActivity.class);
            intent.putExtra("otp", otpRequest);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void setErrorPinView(String msg){
        binding.pinView.setError(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}