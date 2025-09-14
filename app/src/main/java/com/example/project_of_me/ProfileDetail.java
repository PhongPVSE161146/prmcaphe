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
        tvEmail = findViewById(R.id.tvEmail);   // Hiển thị email người dùng
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
        User user = userDAO.getUserByEmail1(email); // Truy vấn người dùng bằng DAO

        // Nếu truy vấn thành công, hiển thị dữ liệu lên giao diện
        if (user != null) {
            tvUsername.setText(user.getName()); // Hiển thị tên người dùng
            tvEmail.setText(user.getEmail()); // Hiển thị email
            tvPhoneNumber.setText(user.getPhone()); // Hiển thị số điện thoại
            tvAddress.setText(user.getAddress()); // Hiển thị địa chỉ
            tvRole.setText(user.getRole()); // Hiển thị vai trò (role) người dùng
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo nếu không có dữ liệu
        }
    }
}
