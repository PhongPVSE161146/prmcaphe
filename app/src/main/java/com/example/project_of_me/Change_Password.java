package com.example.project_of_me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Utils.PasswordHasher;

public class Change_Password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo DAO để làm việc với database người dùng
        UserDAO user = new UserDAO(this);

        // Ánh xạ các thành phần giao diện
        EditText emailUser = findViewById(R.id.etEmail);              // Email người dùng
        EditText new_pass = findViewById(R.id.etNewPassword);         // Mật khẩu mới
        EditText cofirm_pass = findViewById(R.id.etConfirmPassword);  // Mật khẩu xác nhận
        Button btn_change_pass = findViewById(R.id.btnSend);          // Nút đổi mật khẩu

        // Xử lý khi nhấn nút "Đổi mật khẩu"
        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các ô nhập
                String email = emailUser.getText().toString().trim();
                String password_new = new_pass.getText().toString().trim();
                String confirm_password = cofirm_pass.getText().toString().trim();

                // Kiểm tra không được để trống bất kỳ trường nào
                if (email.isEmpty() || password_new.isEmpty() || confirm_password.isEmpty()) {
                    Toast.makeText(Change_Password.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu xác nhận phải khớp với mật khẩu mới
                if (!password_new.equals(confirm_password)) {
                    Toast.makeText(Change_Password.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mã hóa mật khẩu mới trước khi lưu vào database
                String hashedPassword = PasswordHasher.hashPassword(password_new);
                if (hashedPassword == null) {
                    Toast.makeText(Change_Password.this, "Lỗi khi mã hóa mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gọi DAO để cập nhật mật khẩu mới trong database
                boolean chang_password_user = user.updatePassword(email, hashedPassword);

                // Kiểm tra kết quả và thông báo cho người dùng
                if (chang_password_user) {
                    // Đổi mật khẩu thành công, chuyển về màn hình chính
                    Intent intent = new Intent(Change_Password.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(Change_Password.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    // Đổi mật khẩu thất bại (có thể email không tồn tại)
                    Toast.makeText(Change_Password.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
