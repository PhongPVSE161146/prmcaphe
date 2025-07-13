package com.example.project_of_me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.Models.Cart;
import com.example.project_of_me.Models.User;
import com.example.project_of_me.Utils.CartBadgeManager;

public class OrderSuccessActivity extends AppCompatActivity {

    private TextView tvSuccessMessage, tvTotalAmount, tvTitle, tvOrderId, tvOrderDate, tvCartCount;
    private Button btnBackToHome, btnPayment;
    private ImageView imgCart, imgUser;
    private CartDAO cartDAO;
    private int userId;
    private int orderId;
    private CartBadgeManager cartBadgeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Khởi tạo các view
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
//        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnPayment = findViewById(R.id.btnPayment);
        imgCart = findViewById(R.id.imgCart);
        tvTitle = findViewById(R.id.tvTitle);
        imgUser = findViewById(R.id.imgUser);

        // Khởi tạo badge giỏ hàng
        tvCartCount = findViewById(R.id.tvCartCount);
        cartBadgeManager = CartBadgeManager.getInstance(this, tvCartCount);

        cartDAO = new CartDAO(this);

        // Lấy thông tin từ Intent
        double totalAmount = getIntent().getDoubleExtra("total_amount", 0);
        orderId = getIntent().getIntExtra("order_id", -1);

        // Lấy userId từ SharedPreferences
        String email = getSharedPreferences("UserData", MODE_PRIVATE).getString("email", "");
        userId = 1; // Giá trị mặc định
        if (!email.isEmpty()) {
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userId = user.getId();
            }
        }

        // Hiển thị thông tin đơn hàng
        tvTotalAmount.setText(String.format("Tổng tiền: %,.0f₫", totalAmount));
        tvSuccessMessage.setText("Đặt hàng thành công!");
// Luôn hiển thị ngày hiện tại
        String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        tvOrderDate.setText("Ngày đặt: " + currentDate);


        // Xử lý sự kiện click nút "Trở về trang chủ"
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, Home.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện click nút "Xem đơn hàng"
        btnPayment.setOnClickListener(v -> {
//            Intent intent = new Intent(OrderSuccessActivity.this, PlacedOrderActivity.class);
//            startActivity(intent);
//            finish();
            Toast.makeText(OrderSuccessActivity.this, "Cooming soon", Toast.LENGTH_SHORT).show();
        });
        // Xử lý sự kiện click icon giỏ hàng
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, Cart.class);
            startActivity(intent);
        });
        // Xử lý sự kiện click icon user
        imgUser.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, PlacedOrderActivity.class);
            startActivity(intent);
        });
        // Xử lý sự kiện click title
        tvTitle.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, Home.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật số lượng item trong giỏ hàng mỗi khi activity được resume
        cartBadgeManager.updateCartCount();
    }
}
