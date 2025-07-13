package com.example.project_of_me;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.Coffee;
import com.example.project_of_me.DAO.CoffeeDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class List_All_Drink extends AppCompatActivity {
    private RecyclerView recyclerView, recyclerViewCold;
    private ProductAdapter adapterHot, adapterCold;
    private CoffeeDAO coffeeDAO;
    private BottomNavigationView bottomNavigationView;
    private EditText etSearch;
    private TextView tvSeeAll, tvSeeAll1, tvSeeAll2;
    private List<Coffee> listCoffee;
    private ImageView imgCart, imgUser, imgBanner;
    private TextView tvTitle;
    private Button btnFilterAll, btnFilterUnder50k, btnFilter50kTo100k, btnFilterOver100k;
    private List<Coffee> originalList;
    private String currentSearchText = "";
    private String currentPriceFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_drink);
        UserDAO userDAO = new UserDAO(this);

        recyclerView = findViewById(R.id.recyclerHotDrink);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imgCart = findViewById(R.id.imgCart);
        imgUser = findViewById(R.id.imgUser);
        imgBanner = findViewById(R.id.imgBanner); // Gán ID banner
        tvTitle = findViewById(R.id.tvTitle);
        etSearch = findViewById(R.id.etSearch);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterUnder50k = findViewById(R.id.btnFilterUnder50k);
        btnFilter50kTo100k = findViewById(R.id.btnFilter50kTo100k);
        btnFilterOver100k = findViewById(R.id.btnFilterOver100k);

        coffeeDAO = new CoffeeDAO(this);
        // Lấy danh sách món ăn từ cơ sở dữ liệu
        originalList = coffeeDAO.getAllCoffee();
        adapterHot = new ProductAdapter(this, originalList);
        recyclerView.setAdapter(adapterHot);

        // Thiết lập search
        setupSearch();

        // Thiết lập filter
        setupFilters();

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_All_Drink.this, PlacedOrderActivity.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_All_Drink.this, Home.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_All_Drink.this, Order.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
        // 👉 Sự kiện nhấn vào Banner để mở Google Maps
        imgBanner.setOnClickListener(v -> {
            // Dùng geo URI nếu muốn chính xác vị trí
//    String geoUri = "geo:0,0?q=Trường+Đại+Học+FPT";
//    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//    intent.setPackage("com.google.android.apps.maps");

            // Cách an toàn hơn (dùng đường dẫn web của Google Maps)
            String mapUrl = "https://www.google.com/maps/search/?api=1&query=Trường+Đại+Học+FPT";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            startActivity(intent);
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchText = s.toString().toLowerCase().trim();
                applyFilters();
            }
        });
    }

    private void setupFilters() {
        btnFilterAll.setOnClickListener(v -> {
            currentPriceFilter = "all";
            updateFilterButtonStates(btnFilterAll);
            applyFilters();
        });

        btnFilterUnder50k.setOnClickListener(v -> {
            currentPriceFilter = "under50k";
            updateFilterButtonStates(btnFilterUnder50k);
            applyFilters();
        });

        btnFilter50kTo100k.setOnClickListener(v -> {
            currentPriceFilter = "50kTo100k";
            updateFilterButtonStates(btnFilter50kTo100k);
            applyFilters();
        });

        btnFilterOver100k.setOnClickListener(v -> {
            currentPriceFilter = "over100k";
            updateFilterButtonStates(btnFilterOver100k);
            applyFilters();
        });

        // Mặc định chọn "Tất cả"
        updateFilterButtonStates(btnFilterAll);
    }

    private void updateFilterButtonStates(Button selectedButton) {
        // Reset tất cả các nút về trạng thái bình thường
        btnFilterAll.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilterUnder50k.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilter50kTo100k.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilterOver100k.setBackgroundTintList(getColorStateList(R.color.white));

        // Đặt màu nền cho nút được chọn
        selectedButton.setBackgroundTintList(getColorStateList(R.color.blue));
        selectedButton.setTextColor(getColor(R.color.black));
    }

    private void applyFilters() {
        List<Coffee> filteredList = new ArrayList<>();

        for (Coffee coffee : originalList) {
            // Kiểm tra điều kiện tìm kiếm
            boolean matchesSearch = currentSearchText.isEmpty() ||
                    coffee.getProductName().toLowerCase().contains(currentSearchText) ||
                    coffee.getFullDescription().toLowerCase().contains(currentSearchText);

            // Kiểm tra điều kiện giá
            boolean matchesPrice = false;
            switch (currentPriceFilter) {
                case "under50k":
                    matchesPrice = coffee.getPrice() < 50000;
                    break;
                case "50kTo100k":
                    matchesPrice = coffee.getPrice() >= 50000 && coffee.getPrice() <= 100000;
                    break;
                case "over100k":
                    matchesPrice = coffee.getPrice() > 100000;
                    break;
                default: // "all"
                    matchesPrice = true;
                    break;
            }

            // Thêm vào danh sách nếu thỏa mãn cả hai điều kiện
            if (matchesSearch && matchesPrice) {
                filteredList.add(coffee);
            }
        }

        // Cập nhật adapter với danh sách đã lọc
        adapterHot.updateFoodList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh danh sách khi quay lại màn hình
        originalList = coffeeDAO.getAllCoffee();
        applyFilters();
    }
}


