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
    private TextView tvEmptyCart,tvTotalPrice,tvTitle;
    private Button btnPlaceOrder,btnOrder;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderItemList;
    private List<CartItemDetail> cartItemList;

    private OrderDAO orderDAO;
    private CartDAO cartDAO;

    private int userId; // Lấy từ SharedPreferences
    private int pendingOrderId = -1; // Lưu lại orderId của đơn hàng pending
    private ImageView imgCart,imgUser;
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
        if (!email.isEmpty()){
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null){
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
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderAdapter != null) {
                    // Lấy danh sách các order item được tick chọn
                    List<CartItemDetail> selectedItems = orderAdapter.getSelectedItems();
                    if (selectedItems.isEmpty()) {
                        Toast.makeText(Order.this, "Vui lòng chọn ít nhất 1 sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tính tổng tiền của các item được chọn
                    double totalSelected = 0;
                    for (CartItemDetail item : selectedItems) {
                        totalSelected += item.getPrice() * item.getQuantity();
                    }

                    // Sử dụng CartDAO.checkoutCart() để đặt hàng
                    int orderId = cartDAO.checkoutCart(userId, "Cash", ""); // Mặc định thanh toán tiền mặt và không có địa chỉ

                    if (orderId != -1) {
                        // Chuyển sang OrderSuccessActivity, truyền tổng tiền và orderId
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
            }
        });
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Order.this, PlacedOrderActivity.class);
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
