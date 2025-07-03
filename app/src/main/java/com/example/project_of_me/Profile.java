package com.example.project_of_me;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Profile extends AppCompatActivity {
    private UserDAO user;
    private TextView tvEditProfile, tvChangePassword, tvLogout;
    private TextView tvUsername, tvEmail;
    private ImageView tvImg;
    private BottomNavigationView bottomNavigationView;
    // Mục "Đồ uống yêu thích" đã có
    private TextView btnFavorites;
    // Mục "Đơn hàng của bạn"
    private LinearLayout llPlacedOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Gọi phương thức onCreate của lớp cha
        setContentView(R.layout.activity_profile); // Gán layout XML tương ứng cho Activity này

        user = new UserDAO(this); // Khởi tạo DAO để làm việc với dữ liệu người dùng
        tvUsername = findViewById(R.id.tvUserName); // Gán biến hiển thị tên người dùng với ID trong layout

        // Lấy dữ liệu SharedPreferences chứa thông tin đăng nhập người dùng
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String emailPref = sharedPreferences.getString("email", null); // Lấy email đã lưu

        // Nếu có email lưu trong SharedPreferences thì hiển thị thông báo chào mừng
        if (emailPref != null) {
            Toast.makeText(this, "Xin chào " + emailPref, Toast.LENGTH_SHORT).show();
        }

        // Gọi phương thức để lấy thông tin người dùng từ database và hiển thị
        getUserInfo(emailPref);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kiểm tra khi Activity con trả kết quả về
        if (requestCode == 101 && resultCode == RESULT_OK) {
            // Khi kết quả trả về thành công, tải lại thông tin người dùng

            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            String emailPref = sharedPreferences.getString("email", null); // Lấy lại email từ SharedPreferences

            getUserInfo(emailPref); // Cập nhật lại thông tin hiển thị
        }
    }

    // Phương thức lấy thông tin người dùng từ database dựa theo email và hiển thị ra giao diện
    public void getUserInfo(String email) {
        User currentUser = user.getUserByEmail(email); // Gọi DAO để lấy thông tin người dùng dựa trên email

        if (currentUser != null) {
            tvUsername.setText(currentUser.getName()); // Nếu có dữ liệu, hiển thị tên người dùng
        } else {
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show(); // Báo lỗi nếu không có
        }
    }
}
