package com.example.project_of_me;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_of_me.DAO.CoffeeDAO;
import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.Coffee;
import com.example.project_of_me.Models.User;

public class ProductDetail extends AppCompatActivity {
    // Biến lưu số lượng hiện tại và giới hạn tối thiểu/tối đa
    private int currentQuantity = 1;
    private final int MIN_QUANTITY = 1;
    private final int MAX_QUANTITY = 99;

    // Các thành phần UI
    private TextView tvQuantity, tvTitle;
    private ImageButton btnIncrease, btnDecrease;
    private Coffee currentFood; // Món đang được hiển thị
    private int finalUserId; // ID người dùng
    private ImageView imgCart, imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo activity, ánh xạ view và xử lý dữ liệu ban đầu
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Ánh xạ view
        ImageView foodImage = findViewById(R.id.imgDrink);
        TextView foodName = findViewById(R.id.tvDrinkName);
        TextView foodPrice = findViewById(R.id.tvDrinkPrice);
        TextView foodDescription = findViewById(R.id.tvDrinkDescription);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        tvQuantity = findViewById(R.id.tvQuantity);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        imgCart = findViewById(R.id.imgCart);
        imgUser = findViewById(R.id.imgUser);
        tvTitle = findViewById(R.id.tvDetailTitle);

        // Lấy email người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        Log.d("FoodDetail", "Email: " + email);

        // Lấy userId từ email, mặc định là 1 nếu chưa đăng nhập
        int userIdTemp = 1;
        if (!email.isEmpty()) {
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userIdTemp = user.getId();
                Log.d("FoodDetail", "UserID: " + userIdTemp);
            }
        }
        finalUserId = userIdTemp;

        // Lấy ID món từ intent truyền vào
        Intent intent = getIntent();
        int foodId = intent.getIntExtra("foodId", -1);
        Log.d("ID", "onCreate: " + foodId);

        if (foodId != -1) {
            // Truy vấn dữ liệu món ăn từ CSDL
            CoffeeDAO coffeeDAO = new CoffeeDAO(this);
            Coffee food = coffeeDAO.getCoffeeById(foodId);

            if (food != null) {
                // Lưu món đang hiển thị
                currentFood = food;

                // Hiển thị thông tin ra giao diện
                foodName.setText(food.getProductName());
                foodPrice.setText(String.format("%,.0f₫", food.getPrice()));
                foodDescription.setText(food.getFullDescription());

                // Cài đặt các sự kiện
                setupQuantityControls();              // Cài đặt tăng giảm số lượng
                setupAddToCartButton(btnAddToCart);   // Cài đặt nút thêm vào giỏ hàng
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Thông tin món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Chuyển sang trang đơn hàng đã đặt
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetail.this, PlacedOrderActivity.class);
                startActivity(intent);
                Toast.makeText(ProductDetail.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // Trở về trang chủ
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetail.this, Home.class);
                startActivity(intent);
                Toast.makeText(ProductDetail.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // Mở trang giỏ hàng
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetail.this, Order.class);
                startActivity(intent);
                Toast.makeText(ProductDetail.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupQuantityControls() {
        // Cài đặt sự kiện cho nút tăng/giảm số lượng
        updateQuantityDisplay();

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(); // Tăng số lượng
            }
        });

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(); // Giảm số lượng
            }
        });
    }

    private void setupAddToCartButton(Button btnAddToCart) {
        // Cài đặt sự kiện khi người dùng bấm "Thêm vào giỏ"
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(); // Thực hiện thêm vào giỏ hàng
            }
        });
    }

    private void increaseQuantity() {
        // Tăng số lượng lên 1 nếu chưa đạt tối đa
        if (currentQuantity < MAX_QUANTITY) {
            currentQuantity++;
            updateQuantityDisplay();
        } else {
            Toast.makeText(this, "Số lượng tối đa là " + MAX_QUANTITY, Toast.LENGTH_SHORT).show();
        }
    }

    private void decreaseQuantity() {
        // Giảm số lượng xuống 1 nếu lớn hơn tối thiểu
        if (currentQuantity > MIN_QUANTITY) {
            currentQuantity--;
            updateQuantityDisplay();
        } else {
            Toast.makeText(this, "Số lượng tối thiểu là " + MIN_QUANTITY, Toast.LENGTH_SHORT).show();
        }
    }

    private void addToCart() {
        // Thêm món vào giỏ hàng với số lượng hiện tại
        try {
            if (currentFood == null) {
                Toast.makeText(this, "Thông tin món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // In thông tin để debug
            Log.d("FoodDetail", "Adding to cart - UserID: " + finalUserId);
            Log.d("FoodDetail", "Adding to cart - ProductID: " + currentFood.getProductID());
            Log.d("FoodDetail", "Adding to cart - Quantity: " + currentQuantity);
            Log.d("FoodDetail", "Adding to cart - Price: " + currentFood.getPrice());

            // Gọi DAO để thêm sản phẩm vào giỏ hàng
            CartDAO cartDAO = new CartDAO(this);
            boolean success = cartDAO.addCartItem(
                    finalUserId,
                    currentFood.getProductID(),
                    currentQuantity,
                    currentFood.getPrice()
            );

            // Thông báo kết quả
            if (success) {
                Toast.makeText(this, "Đã thêm " + currentQuantity + " món vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("FoodDetail", "Error adding to cart", e);
            Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuantityDisplay() {
        // Cập nhật hiển thị số lượng và trạng thái của nút tăng/giảm
        tvQuantity.setText(String.valueOf(currentQuantity));
        btnDecrease.setEnabled(currentQuantity > MIN_QUANTITY);
        btnIncrease.setEnabled(currentQuantity < MAX_QUANTITY);
        btnDecrease.setAlpha(currentQuantity > MIN_QUANTITY ? 1.0f : 0.5f);
        btnIncrease.setAlpha(currentQuantity < MAX_QUANTITY ? 1.0f : 0.5f);
    }

    public int getCurrentQuantity() {
        // Trả về số lượng hiện tại (nếu cần truy xuất bên ngoài)
        return currentQuantity;
    }

    public void setCurrentQuantity(int quantity) {
        // Thiết lập số lượng mới nếu hợp lệ
        if (quantity >= MIN_QUANTITY && quantity <= MAX_QUANTITY) {
            this.currentQuantity = quantity;
            updateQuantityDisplay();
        }
    }
}
