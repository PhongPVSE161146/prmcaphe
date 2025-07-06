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
        // Hàm được gọi khi Activity được khởi tạo lần đầu tiên
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ánh xạ các view từ layout
        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        imgCart = findViewById(R.id.imgCart);
        tvTitle = findViewById(R.id.tvTitle);
        imgUser = findViewById(R.id.imgUser);
        btnOrder = findViewById(R.id.btnOrder);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo các danh sách
        orderItemList = new ArrayList<>();
        cartItemList = new ArrayList<>();

        // Khởi tạo DAO để truy xuất cơ sở dữ liệu
        orderDAO = new OrderDAO(this);
        cartDAO = new CartDAO(this);

        // Lấy userId từ SharedPreferences dựa trên email đã lưu
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

        // Sử dụng Executor để truy vấn dữ liệu giỏ hàng trong luồng nền (background thread)
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            // Lấy danh sách sản phẩm trong giỏ hàng từ CartDAO
            List<CartItemDetail> items = cartDAO.getCartItems(userId);
            if (items != null && !items.isEmpty()) {
                cartItemList.clear();
                cartItemList.addAll(items);
            }

            // Cập nhật giao diện ở luồng chính (UI thread)
            handler.post(() -> {
                if (cartItemList.isEmpty()) {
                    // Nếu giỏ hàng rỗng, hiển thị thông báo
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    rvOrderItems.setVisibility(View.GONE);
                } else {
                    // Nếu có sản phẩm, hiển thị danh sách và thiết lập adapter
                    tvEmptyCart.setVisibility(View.GONE);
                    rvOrderItems.setVisibility(View.VISIBLE);
                    orderAdapter = new OrderAdapter(Order.this, cartItemList, tvTotalPrice);
                    rvOrderItems.setAdapter(orderAdapter);
                }
            });
        });

        // Xử lý sự kiện khi người dùng nhấn nút "Đặt hàng"
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderAdapter != null) {
                    // Lấy danh sách sản phẩm được chọn để đặt hàng
                    List<CartItemDetail> selectedItems = orderAdapter.getSelectedItems();
                    if (selectedItems.isEmpty()) {
                        Toast.makeText(Order.this, "Vui lòng chọn ít nhất 1 sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tính tổng tiền cho các sản phẩm đã chọn
                    double totalSelected = 0;
                    for (CartItemDetail item : selectedItems) {
                        totalSelected += item.getPrice() * item.getQuantity();
                    }

                    // Thực hiện đặt hàng qua CartDAO
                    int orderId = cartDAO.checkoutCart(userId, "Cash", ""); // Mặc định thanh toán tiền mặt và không có địa chỉ

                    if (orderId != -1) {
                        // Nếu đặt hàng thành công, chuyển sang màn hình OrderSuccessActivity
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

        // Sự kiện nhấn vào icon người dùng => mở danh sách đơn hàng đã đặt
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Order.this, PlacedOrderActivity.class);
                startActivity(intent);
                Toast.makeText(Order.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện nhấn vào tiêu đề để trở về trang chủ
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Order.this, Home.class);
                startActivity(intent);
                Toast.makeText(Order.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện nhấn vào giỏ hàng (refresh lại activity Order)
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
        // Hàm này được gọi mỗi khi Activity quay trở lại foreground
        // Mục đích: cập nhật lại số lượng sản phẩm trong giỏ hàng (badge)
        super.onResume();
        cartBadgeManager.updateCartCount();
    }
}
