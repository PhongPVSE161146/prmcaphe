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
import com.example.project_of_me.Models.OrderItem;
import com.example.project_of_me.Models.CartItem;

import com.example.project_of_me.Models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.project_of_me.Utils.CartBadgeManager;

public class Order extends AppCompatActivity {
    private RecyclerView rvOrderItems;
    private TextView tvEmptyCart, tvTotalPrice, tvTitle;
    private Button btnPlaceOrder, btnOrder;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderItemList;
    private List<CartItemDetail> cartItemList;

    private OrderDAO orderDAO;
    private CartDAO cartDAO;

    private int userId; // Lấy từ SharedPreferences
    private int pendingOrderId = -1; // Lưu lại orderId của đơn hàng pending
    private ImageView imgCart, imgUser;
    private TextView tvCartCount;
    private CartBadgeManager cartBadgeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        imgCart = findViewById(R.id.imgCart);
        tvTitle = findViewById(R.id.tvTitle);
        imgUser = findViewById(R.id.imgUser);
        btnOrder = findViewById(R.id.btnOrder);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
//        orderAdapter = new OrderAdapter(Order.this, orderItemList, tvTotalPrice);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo các danh sách
        orderItemList = new ArrayList<>();
        cartItemList = new ArrayList<>();

        orderDAO = new OrderDAO(this);
        cartDAO = new CartDAO(this);

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");
        userId = 1; // Giá trị mặc định nếu không lấy được
        if (!email.isEmpty()) {
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userId = user.getId();
            }
        }

        // Khởi tạo badge giỏ hàng
        tvCartCount = findViewById(R.id.tvCartCount);
        cartBadgeManager = CartBadgeManager.getInstance(this, tvCartCount);

        // Truy vấn đơn hàng pending và các order item hiện có
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<CartItemDetail> items = cartDAO.getCartItems(userId);
            if (items != null && !items.isEmpty()) {
                cartItemList.clear();
                cartItemList.addAll(items);
            }
            handler.post(() -> {
                if (cartItemList.isEmpty()) {
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    rvOrderItems.setVisibility(View.GONE);
                } else {
                    tvEmptyCart.setVisibility(View.GONE);
                    rvOrderItems.setVisibility(View.VISIBLE);
                    orderAdapter = new OrderAdapter(Order.this, cartItemList, tvTotalPrice);
                    rvOrderItems.setAdapter(orderAdapter);
                }
            });
        });

        // Xử lý sự kiện click nút "Đặt hàng"
        btnOrder.setOnClickListener(v -> {
            if (orderAdapter == null) {
                Toast.makeText(Order.this, "Không có sản phẩm nào trong giỏ hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            List<CartItemDetail> selectedItems = orderAdapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(Order.this, "Vui lòng chọn ít nhất 1 sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalSelected = 0;

            // Cập nhật số lượng chính xác theo UI
            for (CartItemDetail item : selectedItems) {
                int position = cartItemList.indexOf(item);
                int newQuantity = orderAdapter.getCurrentQuantity(position);

                totalSelected += item.getPrice() * newQuantity;
                cartDAO.updateQuantity(item.getOrderItemId(), newQuantity); // luôn update
            }

            // Gọi hàm checkoutCart theo danh sách item đã chọn
            boolean success = true;
            for (CartItemDetail item : selectedItems) {
                int orderId = cartDAO.checkoutSingleItem(userId, item.getOrderItemId(), "Cash", "");
                if (orderId == -1) {
                    success = false;
                }
            }

            if (success) {
                Intent intent = new Intent(Order.this, OrderSuccessActivity.class);
                intent.putExtra("total_amount", totalSelected);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Order.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Order.this, Profile.class);
                startActivity(intent);
                Toast.makeText(Order.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Order.this, Home.class);
                startActivity(intent);
                Toast.makeText(Order.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Order.this, Order.class);
                startActivity(intent);
                Toast.makeText(Order.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật số lượng item trong giỏ hàng mỗi khi activity được resume
        cartBadgeManager.updateCartCount();
    }
}
