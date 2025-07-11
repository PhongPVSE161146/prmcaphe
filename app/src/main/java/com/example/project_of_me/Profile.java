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

    // Mục "Món ăn yêu thích" đã có
    private TextView btnFavorites;

    // Mục "Đơn hàng của bạn"
    private LinearLayout llPlacedOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = new UserDAO(this); // Khởi tạo DAO để thao tác dữ liệu người dùng
        tvUsername = findViewById(R.id.tvUserName); // TextView hiển thị tên người dùng

        // Lấy thông tin email đã đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String emailPref = sharedPreferences.getString("email", null);

        if (emailPref != null) {
            // Hiển thị lời chào nếu có email trong SharedPreferences
            Toast.makeText(this, "Xin chào " + emailPref, Toast.LENGTH_SHORT).show();
        }

        // Gọi phương thức lấy và hiển thị thông tin user
        getUserInfo(emailPref);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Lắng nghe kết quả trả về từ Activity khác (nếu có chỉnh sửa thông tin)
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            // Nếu chỉnh sửa thành công, lấy lại email và load lại thông tin người dùng
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            String emailPref = sharedPreferences.getString("email", null);
            getUserInfo(emailPref);
        }
    }

    // Phương thức lấy thông tin người dùng từ CSDL thông qua email và hiển thị lên giao diện
    public void getUserInfo(String email) {
        User currentUser = user.getUserByEmail(email); // Truy vấn user theo email

        if (currentUser != null) {
            tvUsername.setText(currentUser.getName()); // Hiển thị tên người dùng lên TextView
        } else {
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show(); // Thông báo lỗi nếu không tìm thấy
        }
    }
}

