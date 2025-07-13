package com.example.project_of_me;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.User;
import com.example.project_of_me.Database.ConnectDB;
import com.example.project_of_me.Utils.PasswordHasher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import android.view.View;

import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Sign_Up extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo Activity đăng ký người dùng
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Kích hoạt chế độ hiển thị toàn màn hình (Edge-to-Edge)
        setContentView(R.layout.activity_sign_up);

        // Kết nối CSDL
        ConnectDB con = new ConnectDB(this);

        // Ánh xạ các view trong layout
        Button btn_sign_up = findViewById(R.id.btnSignUp);
        EditText value_username = findViewById(R.id.etUsername);
        EditText value_email = findViewById(R.id.etEmail);
        EditText value_password = findViewById(R.id.etPassword);
        EditText value_phone = findViewById(R.id.et_phone);
        EditText value_address = findViewById(R.id.et_address);

        // Khởi tạo DAO để thao tác với bảng User
        UserDAO user = new UserDAO(this);

        // Xử lý sự kiện khi người dùng bấm nút Đăng ký
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các ô nhập
                String username = value_username.getText().toString().trim();
                String email = value_email.getText().toString().trim();
                String password = value_password.getText().toString().trim();
                String phone = value_phone.getText().toString().trim();
                String address = value_address.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào có bị trống không
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Sign_Up.this, "Dữ liệu không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mã hóa mật khẩu trước khi lưu
                String hashedPassword = PasswordHasher.hashPassword(password);
                if (hashedPassword == null) {
                    Toast.makeText(Sign_Up.this, "Lỗi khi mã hóa mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo đối tượng User mới
                User newUser = new User(username, email, phone, hashedPassword, address, "user");

                // Thêm user vào CSDL
                boolean addnew_user = user.insertUser(newUser);

                // Thông báo kết quả
                if (addnew_user) {
                    Intent intent = new Intent(Sign_Up.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(Sign_Up.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Sign_Up.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Hàm hỗ trợ chuyển ảnh từ tài nguyên drawable thành đối tượng Bitmap
    public Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }

    // Hàm chuyển ảnh bitmap thành mảng byte để lưu vào databasee
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // Nén ảnh ở chất lượng 100%
        return stream.toByteArray();
    }

    // Hàm thêm dữ liệu mẫu cho bảng User (dùng để test)
    private void insertSampleUsers() {
        UserDAO userDAO = new UserDAO(this);
        Context context = this;

        // Lấy ảnh mẫu và mã hóa mật khẩu
        byte[] profile1 = bitmapToByteArray(getBitmapFromDrawable(context, R.drawable.profile1));
        String hashedPassword1 = PasswordHasher.hashPassword("123456");
        String hashedPassword2 = PasswordHasher.hashPassword("123456");

        // Tạo user mẫu 1
        User user1 = new User("Duy Đat1908", "dat1908@example.com", "0123456789", hashedPassword1, "can tho", "user");
        userDAO.insertUser(user1);

        // Tạo user mẫu 2 (admin)
        User user2 = new User("Admin123", "admin@example.com", "0123456789", hashedPassword2, "bac lieu", "admin");
        userDAO.insertUser(user2);

        Toast.makeText(this, "Đã thêm dữ liệu user mẫu vào CSDL", Toast.LENGTH_SHORT).show();
    }
}
