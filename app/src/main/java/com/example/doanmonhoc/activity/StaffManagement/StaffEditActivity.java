package com.example.doanmonhoc.activity.StaffManagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanmonhoc.R;
import com.example.doanmonhoc.api.KiotApiService;
import com.example.doanmonhoc.model.Staff;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffEditActivity extends AppCompatActivity {
    private EditText txtName, txtDob, txtAddress, txtEmail, txtPhone;
    private TextView txtUsername, txtMaNV;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale, radioButtonFemale;
    private Button btnUpdate;
    private ImageButton btnCancel;
    private ImageView staffImage;

    private long staffId;
    private Staff staff;
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_edit);

        gson = new GsonBuilder().setDateFormat("MMM d, yyyy hh:mm:ss a").create();

        txtName = findViewById(R.id.txtName);
        txtDob = findViewById(R.id.txtDob);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtUsername = findViewById(R.id.txtusername);
        txtMaNV = findViewById(R.id.maNV);
        staffImage = findViewById(R.id.staffImage);
        btnUpdate = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        staffId = getIntent().getLongExtra("staffId", -1);

        KiotApiService.apiService.getStaffById(staffId).enqueue(new Callback<Staff>() {
            @Override
            public void onResponse(Call<Staff> call, Response<Staff> response) {
                if (response.isSuccessful() && response.body() != null) {
                    staff = response.body();
                    displayStaffDetails(staff);
                } else {
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<Staff> call, Throwable t) {
                showErrorMessage();
                t.printStackTrace();
            }
        });

        txtDob.setOnClickListener(v -> showDatePickerDialog());

        btnUpdate.setOnClickListener(v -> updateStaffDetails());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void displayStaffDetails(Staff staff) {
        txtMaNV.setText(staff.getStaffKey());
        txtName.setText(staff.getStaffName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        txtDob.setText(sdf.format(staff.getStaffDob()));

        if (staff.getStaffGender() == 1) {
            radioButtonMale.setChecked(true);
        } else {
            radioButtonFemale.setChecked(true);
        }
        txtAddress.setText(staff.getAddress());
        txtEmail.setText(staff.getStaffEmail());
        txtPhone.setText(staff.getStaffPhone());
        txtUsername.setText(staff.getUsername());

        String imageName = staff.getStaffImage();
        int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        Glide.with(this)
                .load(resourceId)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(staffImage);
    }

    private void showErrorMessage() {
        Toast.makeText(this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateStaffDetails() {
        // Lấy thông tin từ các EditText và RadioGroup
        String name = txtName.getText().toString().trim();
        String dob = txtDob.getText().toString().trim();
        String address = txtAddress.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        int gender = radioGroupGender.getCheckedRadioButtonId() == R.id.radioButtonMale ? 1 : 0;

        // Kiểm tra các trường thông tin có trống không
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(address) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin nhân viên
        staff.setStaffName(name);
        staff.setAddress(address);
        staff.setStaffEmail(email);
        staff.setStaffPhone(phone);
        staff.setStaffGender((byte) gender);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(dob);
            staff.setStaffDob(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API để cập nhật nhân viên
        KiotApiService.apiService.updateStaff(staffId, staff).enqueue(new Callback<Staff>() {
            @Override
            public void onResponse(Call<Staff> call, Response<Staff> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thông báo cập nhật thành công
                    Toast.makeText(StaffEditActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // Gửi kết quả về cho StaffManagementActivity
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);

                    // Tạo intent để quay lại StaffManagementActivity và tải lại danh sách nhân viên
                    Intent intent = new Intent(StaffEditActivity.this, StaffManagementActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    // Hiển thị thông báo lỗi nếu không thành công
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<Staff> call, Throwable t) {
                // Hiển thị thông báo lỗi và in ra log nếu gặp lỗi trong quá trình gọi API
                showErrorMessage();
                t.printStackTrace();
            }
        });
    }


    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                StaffEditActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    txtDob.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }
}
