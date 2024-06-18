package com.example.doanmonhoc.activity.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doanmonhoc.R;
import com.example.doanmonhoc.activity.Main.MainActivity;
import com.example.doanmonhoc.activity.MainActivityForStaff;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button btnStart = findViewById(R.id.btnStart);

        // Kiểm tra xem id và roleId đã được lưu hay chưa
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        long id = sharedPreferences.getLong("id", -1);
        long Roleid = sharedPreferences.getLong("Roleid", -1);


        if (id != -1 && Roleid != -1) {
            // Nếu id và roleId đã được lưu
            Intent intent;
            if (Roleid == 1) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivityForStaff.class);
            }
            startActivity(intent);
            finish();
            return;
        }

        // Nếu chưa có id và roleId
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
