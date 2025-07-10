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
    private UserDAO user; // Đối tượng DAO để truy cập dữ liệu người dùng từ SQLite
    private TextView tvEditProfile, tvChangePassword; // Các TextView cho sửa hồ sơ và đổi mật khẩu (chưa sử dụng)
    private TextView tvUsername, tvEmail; // Hiển thị tên và email người dùng (tvEmail chưa dùng)
    private ImageView tvImg; // Hình ảnh đại diện người dùng (chưa sử dụng trong code này)
    private BottomNavigationView bottomNavigationView; // Thanh điều hướng dưới (chưa sử dụng trong code này)

    // Mục "Món ăn yêu thích" đã có (btnFavorites chưa được sử dụng trong đoạn code này)
    private TextView btnFavorites;

    // Các LinearLayout tương tác: nút đăng xuất và xem thông tin chi tiết hồ sơ
    private LinearLayout btnLogOut, AllProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Gọi hàm onCreate của Activity cha
        setContentView(R.layout.activity_profile); // Gán layout XML cho Activity này

        user = new UserDAO(this); // Khởi tạo đối tượng DAO để lấy dữ liệu người dùng

        // Ánh xạ các thành phần trong layout XML vào biến Java
        tvUsername = findViewById(R.id.tvUserName);
        btnLogOut = findViewById(R.id.logout);
        AllProfile = findViewById(R.id.AllProfile);

        // Lấy thông tin email đã lưu từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String emailPref = sharedPreferences.getString("email", null);

        // Nếu có email, hiển thị lời chào và lấy thông tin người dùng từ CSDL
        if (emailPref != null) {
            Toast.makeText(this, "Xin chào " + emailPref, Toast.LENGTH_SHORT).show();
        }

        getUserInfo(emailPref); // Gọi phương thức hiển thị thông tin người dùng

        // Xử lý sự kiện click vào nút đăng xuất
        btnLogOut.setOnClickListener(v -> {
            // Xóa thông tin lưu trữ (email) trong SharedPreferences
            SharedPreferences preferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Xóa toàn bộ dữ liệu đã lưu (hoặc dùng editor.remove("email") để xóa riêng email)
            editor.apply(); // Áp dụng thay đổi

            // Chuyển người dùng về màn hình đăng nhập (MainActivity)
            Intent intent = new Intent(Profile.this, MainActivity.class); // Tạo intent để chuyển về MainActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa backstack
            startActivity(intent);

            // Kết thúc Profile activity
            finish();
        });

        // Xử lý sự kiện click vào "AllProfile" để xem thông tin chi tiết người dùng
        AllProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, ProfileDetail.class); // Tạo intent để chuyển sang ProfileDetail
            startActivity(intent); // Bắt đầu activity mới
            finish(); // Kết thúc Profile activity hiện tại
        });
    }

    
     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Nếu từ activity khác trả về với request code đúng và thành công
        if (requestCode == 101 && resultCode == RESULT_OK) {
            // Tải lại thông tin người dùng từ SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            String emailPref = sharedPreferences.getString("email", null);
            getUserInfo(emailPref); // Cập nhật lại hiển thị người dùng
        }
    }

    // Phương thức lấy thông tin người dùng từ database thông qua email
    public void getUserInfo(String email) {
        User currentUser = user.getUserByEmail(email); // Lấy dữ liệu người dùng từ DAO

        if (currentUser != null) {
            tvUsername.setText(currentUser.getName()); // Hiển thị tên người dùng nếu có dữ liệu
        } else {
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show(); // Báo lỗi nếu không có
        }
    }
}