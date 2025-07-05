package com.example.project_of_me;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.Coffee;
import com.example.project_of_me.Models.User;
import com.example.project_of_me.DAO.CoffeeDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.List;

import com.example.project_of_me.Utils.CartBadgeManager;

public class Home extends AppCompatActivity {
    private RecyclerView recyclerView, recyclerViewCold;
    private ProductAdapter adapterHot, adapterCold;
    private CoffeeDAO coffeeDAO;
    private BottomNavigationView bottomNavigationView;
    private EditText etSearch;
    private TextView tvSeeAll, tvSeeAll1, tvSeeAll2;
    private List<Coffee> listCoffeeHot;
    private List<Coffee> listCoffeeCold;
    private ImageView imgCart, imgUser;
    private TextView tvTitle;
    private TextView tvCartCount;
    private CartBadgeManager cartBadgeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ các thành phần giao diện
        recyclerView = findViewById(R.id.recyclerHotDrink);
        recyclerViewCold = findViewById(R.id.recyclerColdDrink);
        tvSeeAll = findViewById(R.id.tvSeeAll);
        tvSeeAll1 = findViewById(R.id.tvSeeAll1);
        tvSeeAll2 = findViewById(R.id.tvSeeAll2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCold.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imgCart = findViewById(R.id.imgCart);
        imgUser = findViewById(R.id.imgUser);
        tvTitle = findViewById(R.id.tvTitle);
        etSearch = findViewById(R.id.etSearch);
        tvCartCount = findViewById(R.id.tvCartCount);

        coffeeDAO = new CoffeeDAO(this);
        cartBadgeManager = CartBadgeManager.getInstance(this, tvCartCount);

        // Nếu cần, có thể thêm dữ liệu mẫu
        // insertSampleFoods();

        // Lấy dữ liệu người dùng đang đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        if (!userEmail.isEmpty()) {
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(userEmail);
            if (user != null) {
                // Hiển thị thông tin người dùng nếu cần
            }
        }

        // Lấy danh sách cà phê từ cơ sở dữ liệu
        listCoffeeHot = coffeeDAO.getAllCoffeeByType("hot");
        listCoffeeCold = coffeeDAO.getAllCoffeeByType("cold");

        Log.d("HomeActivity", "Hot Coffee List Size: " + listCoffeeHot.size());
        Log.d("HomeActivity", "Cold Coffee List Size: " + listCoffeeCold.size());

        // Thiết lập Adapter cho RecyclerView
        adapterHot = new ProductAdapter(this, listCoffeeHot);
        adapterCold = new ProductAdapter(this, listCoffeeCold);
        recyclerView.setAdapter(adapterHot);
        recyclerViewCold.setAdapter(adapterCold);

        // Sự kiện click chuyển trang
        tvSeeAll.setOnClickListener(v -> startActivity(new Intent(Home.this, List_All_Drink.class)));
        tvSeeAll1.setOnClickListener(v -> startActivity(new Intent(Home.this, List_All_Drink.class)));
        tvSeeAll2.setOnClickListener(v -> startActivity(new Intent(Home.this, List_All_Drink.class)));

        imgUser.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Profile.class));
            Toast.makeText(Home.this, "Đang chuyển đến hồ sơ", Toast.LENGTH_SHORT).show();
        });

        tvTitle.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Home.class));
            Toast.makeText(Home.this, "Đã quay về trang chủ", Toast.LENGTH_SHORT).show();
        });

        imgCart.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Order.class));
            Toast.makeText(Home.this, "Đang chuyển đến giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartBadgeManager.updateCartCount();
    }

    public Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public String saveImageToInternalStorage(Bitmap bitmap) {
        Context context = getApplicationContext();
        File directory = context.getFilesDir();
        String fileName = "coffee_image_" + System.currentTimeMillis() + ".png";
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    // Phương thức insert dữ liệu mẫu có thể bật nếu cần
    private void insertSampleFoods() {
        if (coffeeDAO.getCoffeeCount() == 0) {
            Context context = this;
            String[] names = {
                "Americano", "Chocolate", "Ice Cappuchino", "Ice Americano",
                "Ice Americano", "IMacha Latte", "Mocha", "Ice Cappuchino",
                "Ice Matcha", "Ice Americano2", "Ice Americano1"
            };

            int[] images = {
                R.drawable.cop1, R.drawable.cop2, R.drawable.co3, R.drawable.cop4,
                R.drawable.cop5, R.drawable.cop6, R.drawable.cop7, R.drawable.cop8,
                R.drawable.cop9, R.drawable.cop10, R.drawable.cop11
            };

            for (int i = 0; i < names.length; i++) {
                Coffee coffee = new Coffee();
                coffee.setProductName(names[i]);
                coffee.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
                coffee.setBriefDescription("");
                coffee.setTechnicalSpecifications("");
                coffee.setPrice(45000 + i * 1000);
                coffee.setImageURL(saveImageToInternalStorage(getBitmapFromDrawable(context, images[i])));
                coffee.setCategoryID(i < 6 ? 1 : 2);
                coffeeDAO.insertCoffee(coffee);
            }

            Toast.makeText(this, "Đã thêm món mẫu", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Dữ liệu mẫu đã tồn tại", Toast.LENGTH_SHORT).show();
        }
    }
}
