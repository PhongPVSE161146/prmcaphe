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
    // Các view hiển thị danh sách đơn hàng, tổng tiền, tiêu đề, giỏ hàng trống
    private RecyclerView rvOrderItems;
    private TextView tvEmptyCart,tvTotalPrice,tvTitle;

    // Nút đặt hàng
    private Button btnPlaceOrder,btnOrder;

    // Adapter hiển thị sản phẩm trong giỏ hàng
    private OrderAdapter orderAdapter;

    // Danh sách các sản phẩm đã thêm vào đơn hàng
    private List<OrderItem> orderItemList;

    // Danh sách chi tiết sản phẩm trong giỏ hàng (hiển thị)
    private List<CartItemDetail> cartItemList;

    // DAO thao tác database
    private OrderDAO orderDAO;
    private CartDAO cartDAO;

    // ID người dùng (lấy từ SharedPreferences)
    private int userId;

    // ID của đơn hàng chưa hoàn tất
    private int pendingOrderId = -1;

    // Các icon điều hướng
    private ImageView imgCart,imgUser;

    // Hiển thị số lượng sản phẩm trong giỏ hàng
    private TextView tvCartCount;

    // Quản lý badge hiển thị số lượng sản phẩm
    private CartBadgeManager cartBadgeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ánh xạ view
        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        imgCart = findViewById(R.id.imgCart);
        tvTitle = findViewById(R.id.tvTitle);
        imgUser = findViewById(R.id.imgUser);
        btnOrder = findViewById(R.id.btnOrder);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        // Thiết lập layout cho RecyclerView
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách
        orderItemList = new ArrayList<>();
        cartItemList = new ArrayList<>();

        // Khởi tạo DAO để thao tác DB
        orderDAO = new OrderDAO(this);
        cartDAO = new CartDAO(this);

        // Lấy email người dùng từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");
        userId = 1; // Giá trị mặc định nếu không có email

        // Nếu có email thì truy vấn người dùng để lấy userId
        if (!email.isEmpty()){
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null){
                userId = user.getId();
            }
        }

        // Ánh xạ TextView hiển thị số lượng sản phẩm trong giỏ hàng
        tvCartCount = findViewById(R.id.tvCartCount);

        // Khởi tạo CartBadgeManager để quản lý hiển thị số lượng sản phẩm
        cartBadgeManager = CartBadgeManager.getInstance(this, tvCartCount);

        // Sử dụng Executor để chạy truy vấn trong luồng riêng, tránh block UI
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // Truy vấn danh sách sản phẩm trong giỏ hàng
        executor.execute(() -> {
            List<CartItemDetail> items = cartDAO.getCartItems(userId);

            if (items != null && !items.isEmpty()) {
                cartItemList.clear();
                cartItemList.addAll(items);
            }

            // Cập nhật giao diện trên luồng chính
            handler.post(() -> {
                if (cartItemList.isEmpty()) {
                    tvEmptyCart.setVisibility(View.VISIBLE);       // Hiển thị thông báo giỏ trống
                    rvOrderItems.setVisibility(View.GONE);         // Ẩn danh sách nếu trống
                } else {
                    tvEmptyCart.setVisibility(View.GONE);
                    rvOrderItems.setVisibility(View.VISIBLE);

                    // Gắn adapter và hiển thị danh sách sản phẩm
                    orderAdapter = new OrderAdapter(Order.this, cartItemList, tvTotalPrice);
                    rvOrderItems.setAdapter(orderAdapter);
                }
            });
        });

        // Sự kiện khi nhấn nút "Đặt hàng"
        btnOrder.setOnClickListener(new View.OnClickListener() {    
            @Override
            public void onClick(View v) {
                if (orderAdapter != null) {
                    // Lấy danh sách các item được người dùng chọn
                    List<CartItemDetail> selectedItems = orderAdapter.getSelectedItems();

                    // Nếu không chọn gì, hiển thị cảnh báo
                    if (selectedItems.isEmpty()) {
                        Toast.makeText(Order.this, "Vui lòng chọn ít nhất 1 sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tính tổng tiền của các sản phẩm được chọn
                    double totalSelected = 0;
                    for (CartItemDetail item : selectedItems) {
                        totalSelected += item.getPrice() * item.getQuantity();
                    }

                    // Tiến hành đặt hàng với phương thức thanh toán mặc định là "Cash"
                    int orderId = cartDAO.checkoutCart(userId, "Cash", ""); // Không có địa chỉ

                    if (orderId != -1) {
                        // Thành công: chuyển sang màn hình thành công
                        Intent intent = new Intent(Order.this, OrderSuccessActivity.class);
                        intent.putExtra("total_amount", totalSelected);
                        intent.putExtra("order_id", orderId);
                        startActivity(intent);
                        finish();
                    } else {
                        // Thất bại: thông báo lỗi
                        Toast.makeText(Order.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Order.this, "Không có sản phẩm nào trong giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sự kiện nhấn vào icon user → sang màn hình đơn hàng đã đặt
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Order.this, PlacedOrderActivity.class);
                startActivity(intent);
                Toast.makeText(Order.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // Nhấn vào tiêu đề → về trang chủ
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Order.this, Home.class);
                startActivity(intent);
                Toast.makeText(Order.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // Nhấn lại biểu tượng giỏ hàng → reload lại activity
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
        // Mỗi lần quay lại activity → cập nhật lại số lượng trong giỏ hàng
        cartBadgeManager.updateCartCount();
    }
}
