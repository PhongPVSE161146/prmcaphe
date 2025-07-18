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
        // Hàm được gọi khi Activity được tạo lần đầu
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Kích hoạt hiển thị toàn màn hình (Edge-to-Edge)
        setContentView(R.layout.activity_change_password); // Gắn layout giao diện cho Activity

        UserDAO user = new UserDAO(this); // Khởi tạo DAO để thao tác với dữ liệu người dùng

        // Ánh xạ các thành phần giao diện với biến Java
        EditText emailUser = findViewById(R.id.etEmail);
        EditText new_pass = findViewById(R.id.etNewPassword);
        EditText cofirm_pass = findViewById(R.id.etConfirmPassword);
        Button btn_change_pass = findViewById(R.id.btnSend);

        // Xử lý sự kiện khi người dùng nhấn nút "Gửi" (đổi mật khẩu)
        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu nhập vào từ người dùng
                String email = emailUser.getText().toString().trim();
                String password_new = new_pass.getText().toString().trim();
                String confirm_password = cofirm_pass.getText().toString().trim();

                // Kiểm tra các trường nhập có bị bỏ trống không
                if (email.isEmpty() || password_new.isEmpty() || confirm_password.isEmpty()) {
                    Toast.makeText(Change_Password.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu xác nhận có trùng với mật khẩu mới không
                if (!password_new.equals(confirm_password)) {
                    Toast.makeText(Change_Password.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mã hóa mật khẩu mới bằng thuật toán băm (hash)
                String hashedPassword = PasswordHasher.hashPassword(password_new);
                if (hashedPassword == null) {
                    Toast.makeText(Change_Password.this, "Lỗi khi mã hóa mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cập nhật mật khẩu trong cơ sở dữ liệu
                boolean chang_password_user = user.updatePassword(email, hashedPassword);
                if (chang_password_user) {
                    // Nếu thành công, chuyển về màn hình đăng nhập
                    Intent intent = new Intent(Change_Password.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(Change_Password.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu thất bại, hiển thị thông báo lỗi
                    Toast.makeText(Change_Password.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
