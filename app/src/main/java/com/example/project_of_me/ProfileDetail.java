package com.example.project_of_me;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.User;

public class ProfileDetail extends AppCompatActivity {

    // Khai báo các TextView để hiển thị thông tin chi tiết người dùng
    private TextView tvUsername, tvEmail, tvPhoneNumber, tvAddress, tvRole;
    private UserDAO userDAO; // Đối tượng DAO để truy xuất dữ liệu người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Gọi phương thức onCreate của Activity cha
        setContentView(R.layout.activity_profile_detail); // Gắn layout XML cho activity

        // Ánh xạ các TextView trong layout XML với biến Java
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvRole = findViewById(R.id.tvRole);

        // Khởi tạo đối tượng DAO để làm việc với cơ sở dữ liệu
        userDAO = new UserDAO(this);

        // Lấy email từ SharedPreferences để xác định người dùng hiện tại
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = prefs.getString("email", null); // Lấy email từ bộ nhớ tạm

        // Nếu tìm thấy email thì load dữ liệu người dùng, ngược lại báo lỗi
        if (email != null) {
            loadUserData(email); // Gọi hàm để tải dữ liệu người dùng từ CSDL
        } else {
            Toast.makeText(this, "Không tìm thấy email người dùng", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
        }
    }

    private void loadUserData(String email) {
        User user = userDAO.getUserByEmail1(email);
        if (user != null) {
            tvUsername.setText(user.getName());
            tvEmail.setText(user.getEmail());
            tvPhoneNumber.setText(user.getPhone());
            tvAddress.setText(user.getAddress());
            tvRole.setText(user.getRole());
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}
