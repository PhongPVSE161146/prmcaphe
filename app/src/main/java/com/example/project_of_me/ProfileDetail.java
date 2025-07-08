package com.example.project_of_me;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.User;

public class ProfileDetail extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvPhoneNumber, tvAddress, tvRole;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        // Ánh xạ view
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvRole = findViewById(R.id.tvRole);

        // DAO
        userDAO = new UserDAO(this);

        // Lấy email từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email != null) {
            loadUserData(email);
        } else {
            Toast.makeText(this, "Không tìm thấy email người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData(String email) {
        User user = userDAO.getUserByEmail1(email);
        if (user != null) {
            tvUsername.setText(user.getName());
            tvEmail.setText(user.getEmail());
            tvPhoneNumber.setText(user.getPhone());
            tvAddress.setText(user.getAddress());
            tvRole.setText(user.getRole());
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}
