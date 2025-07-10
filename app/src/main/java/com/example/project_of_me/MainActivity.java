package com.example.project_of_me;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_of_me.Models.User;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Utils.PasswordHasher;

import android.content.SharedPreferences;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.project_of_me.Services.CartNotificationService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private BroadcastReceiver bootReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        UserDAO user = new UserDAO(this);
        Button btn_login = findViewById(R.id.btnLogin);
        EditText value_email = findViewById(R.id.etUsername);
        EditText value_password = findViewById(R.id.etPassword);
        TextView tv_forgot_password = findViewById(R.id.tvForgotPassword);
        TextView tv_sign_up = findViewById(R.id.tvRegister);

         // Xử lý sự kiện khi bấm nút "Đăng nhập"
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = value_email.getText().toString().trim();
                String password = value_password.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mã hoá mật khẩu trước khi kiểm tra
                String hashedPassword = PasswordHasher.hashPassword(password);
                if (hashedPassword == null) {
                    Toast.makeText(MainActivity.this, "Lỗi khi mã hóa mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra người dùng có tồn tại không
                boolean exist_user = user.checkUser(email, hashedPassword);
                if (exist_user) {
                    // Lưu email vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.apply();

                    // Lấy thông tin người dùng và kiểm tra quyền
                    User currentUser = user.getUserByEmail(email);
                    if (currentUser != null && "admin".equals(currentUser.getRole())) {
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công với quyền admin", Toast.LENGTH_SHORT).show();
                        // Có thể mở trang AdminActivity nếu cần
                    } else {
                        // Mở trang chính cho user
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign up
        tv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Sign_Up.class);
                startActivity(intent);
            }
        });

        // Forgot Password
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = value_email.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, Change_Password.class);
                if (!email.isEmpty()) {
                    intent.putExtra("USERNAME_KEY", email); // Gửi dữ liệu qua Intent
                }
                startActivity(intent);
            }
        });

        // Kiểm tra và yêu cầu quyền thông báo trên Android 13 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                startCartNotificationService();
            }
        } else {
            startCartNotificationService();
        }

        // Đăng ký BroadcastReceiver
        bootReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                    startCartNotificationService();
                }
            }
        };
        registerReceiver(bootReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bootReceiver != null) {
            unregisterReceiver(bootReceiver);
        }
    }

    private void startCartNotificationService() {
        Intent serviceIntent = new Intent(this, CartNotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCartNotificationService();
            }
        }
    }
}