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
    private int currentQuantity = 1; // Số lượng hiện tại, bắt đầu từ 1
    private final int MIN_QUANTITY = 1; // Số lượng tối thiểu
    private final int MAX_QUANTITY = 99; // Số lượng tối đa (tùy chỉnh theo nhu cầu)
    private TextView tvQuantity,tvTitle;
    private ImageButton btnIncrease, btnDecrease;
    private Coffee currentFood; // Object chứa thông tin món ăn
    private int finalUserId; // ID người dùng
    private ImageView imgCart,imgUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Khởi tạo views
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
        // Lấy userId từ SharedPreferences hoặc Intent
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        Log.d("FoodDetail", "Email: " + email);

        // Mặc định userId là 1 nếu chưa đăng nhập
        int userIdTemp = 1;
        if (!email.isEmpty()) {
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userIdTemp = user.getId();
                Log.d("FoodDetail", "UserID: " + userIdTemp);
            }
        }
        finalUserId = userIdTemp; // Gán giá trị cho biến thành viên

        Intent intent = getIntent();
        int foodId = intent.getIntExtra("foodId", -1);
        Log.d("ID", "onCreate: " + foodId);

        if (foodId != -1) {
            // Lấy dữ liệu từ database
            CoffeeDAO coffeeDAO = new CoffeeDAO(this);
            Coffee food = coffeeDAO.getCoffeeById(foodId);

            if (food != null) {
                // QUAN TRỌNG: Gán food cho currentFood
                currentFood = food;
                foodName.setText(food.getProductName());
                foodPrice.setText(String.format("%,.0f₫", food.getPrice()));
                foodDescription.setText(food.getFullDescription());
                // Setup UI và events
                setupQuantityControls();
                setupAddToCartButton(btnAddToCart);
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Thông tin món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetail.this, PlacedOrderActivity.class);
                startActivity(intent);
                Toast.makeText(ProductDetail.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetail.this, Home.class);
                startActivity(intent);
                Toast.makeText(ProductDetail.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

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
        updateQuantityDisplay();

        // Thiết lập sự kiện cho nút "Tăng"
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        // Thiết lập sự kiện cho nút "Giảm"
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });
    }
    private void setupAddToCartButton(Button btnAddToCart) {
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }
    // Phương thức tăng số lượng
    private void increaseQuantity() {
        if (currentQuantity < MAX_QUANTITY) {
            currentQuantity++;
            updateQuantityDisplay();
        } else {
            Toast.makeText(this, "Số lượng tối đa là " + MAX_QUANTITY, Toast.LENGTH_SHORT).show();
        }
    }
    // Phương thức giảm số lượng
    private void decreaseQuantity() {
        if (currentQuantity > MIN_QUANTITY) {
            currentQuantity--;
            updateQuantityDisplay();
        } else {
            Toast.makeText(this, "Số lượng tối thiểu là " + MIN_QUANTITY, Toast.LENGTH_SHORT).show();
        }
    }

    private void addToCart() {
        try {
            // Kiểm tra null trước khi sử dụng
            if (currentFood == null) {
                Toast.makeText(this, "Thông tin món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("FoodDetail", "Adding to cart - UserID: " + finalUserId);
            Log.d("FoodDetail", "Adding to cart - ProductID: " + currentFood.getProductID());
            Log.d("FoodDetail", "Adding to cart - Quantity: " + currentQuantity);
            Log.d("FoodDetail", "Adding to cart - Price: " + currentFood.getPrice());

            CartDAO cartDAO = new CartDAO(this);
            boolean success = cartDAO.addCartItem(
                finalUserId,
                currentFood.getProductID(),
                currentQuantity,
                currentFood.getPrice()
            );

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

    // Cập nhật hiển thị số lượng và trạng thái các nút
    private void updateQuantityDisplay() {
        tvQuantity.setText(String.valueOf(currentQuantity));

        // Cập nhật trạng thái của các nút
        btnDecrease.setEnabled(currentQuantity > MIN_QUANTITY);
        btnIncrease.setEnabled(currentQuantity < MAX_QUANTITY);

        // Thay đổi alpha để hiển thị nút bị vô hiệu hóa
        btnDecrease.setAlpha(currentQuantity > MIN_QUANTITY ? 1.0f : 0.5f);
        btnIncrease.setAlpha(currentQuantity < MAX_QUANTITY ? 1.0f : 0.5f);
    }

    // Getter cho số lượng hiện tại (có thể dùng khi thêm vào giỏ hàng)
    public int getCurrentQuantity() {
        return currentQuantity;
    }

    // Setter cho số lượng (nếu cần thiết lập từ bên ngoài)
    public void setCurrentQuantity(int quantity) {
        if (quantity >= MIN_QUANTITY && quantity <= MAX_QUANTITY) {
            this.currentQuantity = quantity;
            updateQuantityDisplay();
        }
    }
}