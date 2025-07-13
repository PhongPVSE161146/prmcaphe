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
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ConnectDB con = new ConnectDB(this);
//        insertSampleUsers();
        Button btn_sign_up = findViewById(R.id.btnSignUp);
        EditText value_username = findViewById(R.id.etUsername);
        EditText value_email = findViewById(R.id.etEmail);
        EditText value_password = findViewById(R.id.etPassword);
        EditText value_phone = findViewById(R.id.et_phone);
        EditText value_address = findViewById(R.id.et_address);
        UserDAO user = new UserDAO(this);
        // Sign up
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = value_username.getText().toString().trim();
                String email = value_email.getText().toString().trim();
                String password = value_password.getText().toString().trim();
                String phone = value_phone.getText().toString().trim();
                String address = value_address.getText().toString().trim();
                
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Sign_Up.this, "Dữ liệu không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Hash password trước khi lưu
                String hashedPassword = PasswordHasher.hashPassword(password);
                if (hashedPassword == null) {
                    Toast.makeText(Sign_Up.this, "Lỗi khi mã hóa mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(username, email, phone, hashedPassword, address, "user");
                boolean addnew_user = user.insertUser(newUser);
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
    public Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    private void insertSampleUsers() {
        UserDAO userDAO = new UserDAO(this);
        Context context = this;
        byte[] profile1 = bitmapToByteArray(getBitmapFromDrawable(context, R.drawable.profile1));
        // Hash password cho user mẫu
        String hashedPassword1 = PasswordHasher.hashPassword("123456");
        String hashedPassword2 = PasswordHasher.hashPassword("123456");
        // User 1
        User user1 = new User("Duy Đat1908", "dat1908@example.com", "0123456789", hashedPassword1, "can tho", "user");
        userDAO.insertUser(user1);
        // User 2
        User user2 = new User("Admin123", "admin@example.com", "0123456789", hashedPassword2, "bac lieu", "admin");
        userDAO.insertUser(user2);
        Toast.makeText(this, "Đã thêm dữ liệu user mẫu vào CSDL", Toast.LENGTH_SHORT).show();
    }
}