package com.example.project_of_me;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
    private ImageView imgCart, imgUser, imgBanner;
    private TextView tvTitle;
    private TextView tvCartCount;
    private CartBadgeManager cartBadgeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        UserDAO userDAO = new UserDAO(this);

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
        coffeeDAO = new CoffeeDAO(this);
        etSearch = findViewById(R.id.etSearch);
        imgBanner = findViewById(R.id.imgBanner); // Gán ID banner
//        insertSampleFoods(); // Nếu cần insert mẫu
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", ""); // Email mặc định là ""
        if (!userEmail.isEmpty()) {
            User user = userDAO.getUserByEmail(userEmail);
            if (user != null) {
//                tvWelcome.setText("Welcome " + user.getName());
            }
        }
        // Lấy danh sách món ăn từ cơ sở dữ liệu
        listCoffeeHot = coffeeDAO.getAllCoffeeByType("hot");
        listCoffeeCold = coffeeDAO.getAllCoffeeByType("cold");
        Log.d("HomeActivity", "Hot Coffee List Size: " + listCoffeeHot.size());
        Log.d("HomeActivity", "Cold Coffee List Size: " + listCoffeeCold.size());
        // Thiết lập Adapter cho RecyclerView
        adapterHot = new ProductAdapter(this, listCoffeeHot);
        adapterCold = new ProductAdapter(this, listCoffeeCold);

        // Gán adapter vào RecyclerView
        recyclerView.setAdapter(adapterHot);
        recyclerViewCold.setAdapter(adapterCold);
        tvSeeAll.setOnClickListener(v -> {
            // Chuyển đến Activity khác khi nhấn vào tvSeeAll
            Intent intent = new Intent(Home.this, List_All_Drink.class);
            startActivity(intent);
        });
        tvSeeAll1.setOnClickListener(v -> {
            // Chuyển đến Activity khác khi nhấn vào tvSeeAll
            Intent intent = new Intent(Home.this, List_All_Drink.class);
            startActivity(intent);
        });
        tvSeeAll2.setOnClickListener(v -> {
            // Chuyển đến Activity khác khi nhấn vào tvSeeAll
            Intent intent = new Intent(Home.this, List_All_Drink.class);
            startActivity(intent);
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Profile.class);
                startActivity(intent);
                Toast.makeText(Home.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Home.class);
                startActivity(intent);
                Toast.makeText(Home.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Order.class);
                startActivity(intent);
                Toast.makeText(Home.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // 👉 Sự kiện nhấn vào Banner để mở Google Maps
        imgBanner.setOnClickListener(v -> {
            // Dùng geo URI nếu muốn chính xác vị trí
//    String geoUri = "geo:0,0?q=Trường+Đại+Học+FPT";
//    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//    intent.setPackage("com.google.android.apps.maps");

            // Cách an toàn hơn (dùng đường dẫn web của Google Maps)
            String mapUrl = "https://www.google.com/maps/search/?api=1&query=Trường+Đại+Học+FPT+HCM";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            startActivity(intent);
        });

        // Khởi tạo badge giỏ hàng
        tvCartCount = findViewById(R.id.tvCartCount);
        cartBadgeManager = CartBadgeManager.getInstance(this, tvCartCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật số lượng item trong giỏ hàng mỗi khi activity được resume
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
        FileOutputStream fos = null;
        File directory = context.getFilesDir(); // Lưu trong bộ nhớ trong
        String fileName = "coffee_image_" + System.currentTimeMillis() + ".png";
        File file = new File(directory, fileName);

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);  // Nén ảnh thành PNG
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();  // Trả về đường dẫn tới tệp ảnh
    }

    private void insertSampleFoods() {
        // Chỉ thêm dữ liệu mẫu nếu CSDL trống
        if (coffeeDAO.getCoffeeCount() == 0) {
            Context context = this;
            String cop1Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop1));
            String cop2Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop2));
            String cop3Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.co3));
            String cop4Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop4));
            String cop5Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop5));
            String cop6Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop6));
            String cop7Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop7));
            String cop8Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop8));
            String cop9Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop9));
            String cop10Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop10));
            String cop11Image = saveImageToInternalStorage(getBitmapFromDrawable(context, R.drawable.cop11));

            // Món 1 - Phở Bò
            Coffee cop1 = new Coffee();
            cop1.setProductName("Americano");
            cop1.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop1.setBriefDescription("");
            cop1.setTechnicalSpecifications("");
            cop1.setPrice(10000);
            cop1.setImageURL(cop1Image); // Gán ảnh đã chuyển đổi
            cop1.setCategoryID(1);
            coffeeDAO.insertCoffee(cop1);

            // Món 2 - Bánh Mì Thịt
            Coffee cop2 = new Coffee();
            cop2.setProductName("Chocolate");
            cop2.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop2.setBriefDescription("");
            cop2.setTechnicalSpecifications("");
            cop2.setPrice(35000);
            cop2.setImageURL(cop2Image); // Gán ảnh đã chuyển đổi
            cop2.setCategoryID(1);
            coffeeDAO.insertCoffee(cop2);

            // Món 3 - Cơm Tấm Sườn
            Coffee cop3 = new Coffee();
            cop3.setProductName("Ice Cappuchino");
            cop3.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop3.setBriefDescription("");
            cop3.setTechnicalSpecifications("");
            cop3.setPrice(65000);
            cop3.setImageURL(cop3Image); // Gán ảnh đã chuyển đổi
            cop3.setCategoryID(1);
            coffeeDAO.insertCoffee(cop3);
            // Món 4 - Bún Chả
            Coffee cop4 = new Coffee();
            cop4.setProductName("Ice Americano");
            cop4.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop4.setBriefDescription("");
            cop4.setTechnicalSpecifications("");
            cop4.setPrice(60000);
            cop4.setImageURL(cop4Image); // Gán ảnh đã chuyển đổi
            cop4.setCategoryID(1);
            coffeeDAO.insertCoffee(cop4);

            // Món 5 - Gỏi Cuốn
            Coffee cop5 = new Coffee();
            cop5.setProductName("Ice Americano");
            cop5.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop5.setBriefDescription("");
            cop5.setTechnicalSpecifications("");
            cop5.setPrice(45000);
            cop5.setImageURL(cop5Image); // Gán ảnh đã chuyển đổi
            cop5.setCategoryID(1);
            coffeeDAO.insertCoffee(cop5);

            Coffee cop6 = new Coffee();
            cop6.setProductName("IMacha Latte");
            cop6.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop6.setBriefDescription("");
            cop6.setTechnicalSpecifications("");
            cop6.setPrice(45000);
            cop6.setImageURL(cop6Image); // Gán ảnh đã chuyển đổi
            cop6.setCategoryID(1);
            coffeeDAO.insertCoffee(cop6);

            Coffee cop7 = new Coffee();
            cop7.setProductName("Mocha");
            cop7.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop7.setBriefDescription("");
            cop7.setTechnicalSpecifications("");
            cop7.setPrice(45000);
            cop7.setImageURL(cop7Image); // Gán ảnh đã chuyển đổi
            cop7.setCategoryID(2);
            coffeeDAO.insertCoffee(cop7);

            Coffee cop8 = new Coffee();
            cop8.setProductName("Ice Cappuchino");
            cop8.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop8.setBriefDescription("");
            cop8.setTechnicalSpecifications("");
            cop8.setPrice(45000);
            cop8.setImageURL(cop8Image); // Gán ảnh đã chuyển đổi
            cop8.setCategoryID(2);
            coffeeDAO.insertCoffee(cop8);

            Coffee cop9 = new Coffee();
            cop9.setProductName("Ice Matcha");
            cop9.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop9.setBriefDescription("");
            cop9.setTechnicalSpecifications("");
            cop9.setPrice(45000);
            cop9.setImageURL(cop9Image); // Gán ảnh đã chuyển đổi
            cop9.setCategoryID(2);
            coffeeDAO.insertCoffee(cop9);

            Coffee cop10 = new Coffee();
            cop10.setProductName("Ice Americano2");
            cop10.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop10.setBriefDescription("");
            cop10.setTechnicalSpecifications("");
            cop10.setPrice(45000);
            cop10.setImageURL(cop10Image); // Gán ảnh đã chuyển đổi
            cop10.setCategoryID(2);
            coffeeDAO.insertCoffee(cop10);

            Coffee cop11 = new Coffee();
            cop11.setProductName("Ice Americano1");
            cop11.setFullDescription("A classic hot coffee made with a shot of espresso and hot water.");
            cop11.setBriefDescription("");
            cop11.setTechnicalSpecifications("");
            cop11.setPrice(45000);
            cop11.setImageURL(cop11Image); // Gán ảnh đã chuyển đổi
            cop11.setCategoryID(2);
            coffeeDAO.insertCoffee(cop11);

            Toast.makeText(this, "Đã thêm 5 món ăn mẫu vào CSDL", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Dữ liệu mẫu đã có trong CSDL", Toast.LENGTH_SHORT).show();
        }
    }
}



