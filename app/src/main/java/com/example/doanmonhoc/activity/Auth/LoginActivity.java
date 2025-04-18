package com.example.doanmonhoc.activity.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmonhoc.R;
import com.example.doanmonhoc.activity.Main.MainActivity;
import com.example.doanmonhoc.api.KiotApiService;
import com.example.doanmonhoc.model.LoginResponse;
import com.example.doanmonhoc.model.Staff;
import com.example.doanmonhoc.utils.PrefsUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText edtUsername = findViewById(R.id.edtUsername);
        EditText edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView quenMatKhau = findViewById(R.id.quenMatKhau);

        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                login(username, password);
            } else {
                Toast.makeText(LoginActivity.this, "Hãy nhập username và mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
        quenMatKhau.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void login(String username, String password) {
        Staff staff = new Staff(username, password);
        KiotApiService.apiService.loginUser(staff).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    String message = loginResponse.getMessage();
                    long Roleid = loginResponse.getRoleid();
                    long id = loginResponse.getId();
                    String staffName = loginResponse.getStaffName();
                    String staffImage = loginResponse.getStaffImage();

                    // Lưu vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences(PrefsUtils.PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(PrefsUtils.PREFS_ID, id);
                    editor.putLong(PrefsUtils.PREFS_ROLE, Roleid);
                    editor.putString(PrefsUtils.PREFS_NAME, staffName);
                    editor.putString(PrefsUtils.PREFS_IMAGE, staffImage);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Sai username hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "Sai username hoặc password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}