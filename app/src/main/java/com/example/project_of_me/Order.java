package com.example.project_of_me;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.DAO.OrderDAO;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.CartItemDetail;
import com.example.project_of_me.Models.User;
import com.example.project_of_me.Utils.CartBadgeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Order extends AppCompatActivity {

    private RecyclerView rvOrderItems;
    private TextView tvEmptyCart, tvTotalPrice, tvTitle;
    private Button btnOrder;
    private OrderAdapter orderAdapter;
    private List<CartItemDetail> cartItemList;

    private OrderDAO orderDAO;
    private CartDAO cartDAO;

    private int userId; // Lấy từ SharedPreferences
    private ImageView imgCart, imgUser;
    private TextView tvCartCount;
    private CartBadgeManager cartBadgeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ánh xạ View
        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        imgCart = findViewById(R.id.imgCart);
        imgUser = findViewById(R.id.imgUser);
        tvTitle = findViewById(R.id.tvTitle);
        btnOrder = findViewById(R.id.btnOrder);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        cartItemList = new ArrayList<>();
        orderDAO = new OrderDAO(this);
        cartDAO = new CartDAO(this);

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");
        userId = 1; // default

        if (!email.isEmpty()) {
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userId = user.getId();
            }
        }

        // Badge giỏ hàng
        tvCartCount = findViewById(R.id.tvCartCount);
        cartBadgeManager = CartBadgeManager.getInstance(this, tvCartCount);

        // Load danh sách cart từ DB
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<CartItemDetail> items = cartDAO.getCartItems(userId);
            handler.post(() -> {
                cartItemList.clear();
                if (items != null && !items.isEmpty()) {
                    cartItemList.addAll(items);
                    tvEmptyCart.setVisibility(View.GONE);
                    rvOrderItems.setVisibility(View.VISIBLE);

                    orderAdapter = new OrderAdapter(Order.this, cartItemList, tvTotalPrice);
                    rvOrderItems.setAdapter(orderAdapter);
                } else {
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    rvOrderItems.setVisibility(View.GONE);
                }
            });
        });

        // Nút đặt hàng
        btnOrder.setOnClickListener(v -> {
            if (orderAdapter != null) {
                List<CartItemDetail> selectedItems = orderAdapter.getSelectedItems();
                if (selectedItems.isEmpty()) {
                    Toast.makeText(Order.this, "Vui lòng chọn ít nhất 1 sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalSelected = 0;
                for (CartItemDetail item : selectedItems) {
                    totalSelected += item.getPrice() * item.getQuantity();
                }

                int orderId = cartDAO.checkoutCart(userId, "Cash", "");
                if (orderId != -1) {
                    Intent intent = new Intent(Order.this, OrderSuccessActivity.class);
                    intent.putExtra("total_amount", totalSelected);
                    intent.putExtra("order_id", orderId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Order.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Order.this, "Không có sản phẩm nào trong giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        // Icon user
        imgUser.setOnClickListener(v -> {
            startActivity(new Intent(Order.this, PlacedOrderActivity.class));
            Toast.makeText(Order.this, "Chuyển đến đơn hàng đã đặt", Toast.LENGTH_SHORT).show();
        });

        // Icon title
        tvTitle.setOnClickListener(v -> {
            startActivity(new Intent(Order.this, Home.class));
            Toast.makeText(Order.this, "Trở về trang chủ", Toast.LENGTH_SHORT).show();
        });

        // Icon cart
        imgCart.setOnClickListener(v -> {
            startActivity(new Intent(Order.this, Order.class));
            Toast.makeText(Order.this, "Làm mới giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartBadgeManager.updateCartCount();
    }
}
