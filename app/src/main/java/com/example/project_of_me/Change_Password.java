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
        UserDAO user = new UserDAO(this);
        EditText emailUser = findViewById(R.id.etEmail);
        EditText new_pass = findViewById(R.id.etNewPassword);
        EditText cofirm_pass = findViewById(R.id.etConfirmPassword);
        Button btn_change_pass = findViewById(R.id.btnSend);

        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailUser.getText().toString().trim();
                String password_new = new_pass.getText().toString().trim();
                String confirm_password = cofirm_pass.getText().toString().trim();

                if (email.isEmpty() || password_new.isEmpty() || confirm_password.isEmpty()) {
                    Toast.makeText(Change_Password.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password_new.equals(confirm_password)) {
                    Toast.makeText(Change_Password.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hash password mới trước khi lưu
                String hashedPassword = PasswordHasher.hashPassword(password_new);
                if (hashedPassword == null) {
                    Toast.makeText(Change_Password.this, "Lỗi khi mã hóa mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean chang_password_user = user.updatePassword(email, hashedPassword);
                if (chang_password_user) {
                    Intent intent = new Intent(Change_Password.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(Change_Password.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Change_Password.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}