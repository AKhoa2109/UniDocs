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
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivitySignInBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.request.LoginRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserResponse;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

public class SignIn extends AppCompatActivity {
    EditText etPass, etEmail;

    ActivitySignInBinding binding;
    SessionManager session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionManager.getInstance(this);

        //neu da login thi chuyen den main
        if(session.isLoggedIn()){
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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
}