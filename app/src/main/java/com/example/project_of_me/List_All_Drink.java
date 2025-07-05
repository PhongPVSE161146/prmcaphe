package com.example.project_of_me;

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
    // Khai báo RecyclerView để hiển thị danh sách sản phẩm
    private RecyclerView recyclerView, recyclerViewCold;

    // Adapter cho danh sách sản phẩm
    private ProductAdapter adapterHot, adapterCold;

    // DAO truy cập dữ liệu sản phẩm
    private CoffeeDAO coffeeDAO;

    // Thanh điều hướng dưới (chưa sử dụng trong code này)
    private BottomNavigationView bottomNavigationView;

    // Ô tìm kiếm
    private EditText etSearch;

    // Các TextView điều hướng hoặc hiển thị
    private TextView tvSeeAll, tvSeeAll1, tvSeeAll2;

    // Danh sách sản phẩm (tổng thể)
    private List<Coffee> listCoffee;

    // Icon giỏ hàng và người dùng
    private ImageView imgCart, imgUser;

    // Tiêu đề trang
    private TextView tvTitle;

    // Các nút lọc sản phẩm theo mức giá
    private Button btnFilterAll, btnFilterUnder50k, btnFilter50kTo100k, btnFilterOver100k;

    // Danh sách gốc chưa bị lọc
    private List<Coffee> originalList;

    // Biến lưu lại nội dung tìm kiếm hiện tại
    private String currentSearchText = "";

    // Biến lưu lại trạng thái lọc theo giá hiện tại
    private String currentPriceFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_drink);

        // Khởi tạo UserDAO (dù không được sử dụng trong đoạn code này)
        UserDAO userDAO = new UserDAO(this);

        // Ánh xạ RecyclerView và thiết lập layout theo chiều dọc
        recyclerView = findViewById(R.id.recyclerHotDrink);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Ánh xạ các View điều hướng
        imgCart = findViewById(R.id.imgCart);
        imgUser = findViewById(R.id.imgUser);
        tvTitle = findViewById(R.id.tvTitle);

        // Ánh xạ ô tìm kiếm
        etSearch = findViewById(R.id.etSearch);

        // Ánh xạ các nút lọc
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterUnder50k = findViewById(R.id.btnFilterUnder50k);
        btnFilter50kTo100k = findViewById(R.id.btnFilter50kTo100k);
        btnFilterOver100k = findViewById(R.id.btnFilterOver100k);

        // Khởi tạo DAO để lấy dữ liệu sản phẩm
        coffeeDAO = new CoffeeDAO(this);

        // Lấy danh sách tất cả sản phẩm từ database
        originalList = coffeeDAO.getAllCoffee();

        // Khởi tạo adapter và gán dữ liệu
        adapterHot = new ProductAdapter(this, originalList);
        recyclerView.setAdapter(adapterHot);

        // Cài đặt chức năng tìm kiếm
        setupSearch();

        // Cài đặt chức năng lọc theo mức giá
        setupFilters();

        // Sự kiện khi nhấn vào biểu tượng người dùng
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở trang đơn hàng đã đặt
                Intent intent = new Intent(List_All_Drink.this, PlacedOrderActivity.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện khi nhấn vào tiêu đề → quay về trang chủ
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_All_Drink.this, Home.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện khi nhấn vào biểu tượng giỏ hàng
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_All_Drink.this, Order.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thiết lập chức năng tìm kiếm sản phẩm theo tên hoặc mô tả
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Cập nhật nội dung tìm kiếm và lọc danh sách
                currentSearchText = s.toString().toLowerCase().trim();
                applyFilters();
            }
        });
    }

    // Thiết lập chức năng lọc theo mức giá
    private void setupFilters() {
        // Khi nhấn nút "Tất cả"
        btnFilterAll.setOnClickListener(v -> {
            currentPriceFilter = "all";
            updateFilterButtonStates(btnFilterAll);
            applyFilters();
        });

        // Khi nhấn nút "< 50k"
        btnFilterUnder50k.setOnClickListener(v -> {
            currentPriceFilter = "under50k";
            updateFilterButtonStates(btnFilterUnder50k);
            applyFilters();
        });

        // Khi nhấn nút "50k–100k"
        btnFilter50kTo100k.setOnClickListener(v -> {
            currentPriceFilter = "50kTo100k";
            updateFilterButtonStates(btnFilter50kTo100k);
            applyFilters();
        });

        // Khi nhấn nút "> 100k"
        btnFilterOver100k.setOnClickListener(v -> {
            currentPriceFilter = "over100k";
            updateFilterButtonStates(btnFilterOver100k);
            applyFilters();
        });

        // Mặc định chọn "Tất cả" lúc mới vào
        updateFilterButtonStates(btnFilterAll);
    }

    // Cập nhật trạng thái màu sắc cho nút lọc đang được chọn
    private void updateFilterButtonStates(Button selectedButton) {
        // Đặt lại màu cho tất cả nút về trắng
        btnFilterAll.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilterUnder50k.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilter50kTo100k.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilterOver100k.setBackgroundTintList(getColorStateList(R.color.white));

        // Tô màu nút được chọn để nổi bật
        selectedButton.setBackgroundTintList(getColorStateList(R.color.blue));
        selectedButton.setTextColor(getColor(R.color.black));
    }

    // Hàm lọc danh sách sản phẩm dựa trên tìm kiếm và giá tiền
    private void applyFilters() {
        List<Coffee> filteredList = new ArrayList<>();

        for (Coffee coffee : originalList) {
            // Điều kiện lọc theo tìm kiếm
            boolean matchesSearch = currentSearchText.isEmpty() ||
                    coffee.getProductName().toLowerCase().contains(currentSearchText) ||
                    coffee.getFullDescription().toLowerCase().contains(currentSearchText);

            // Điều kiện lọc theo giá
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

            // Thêm vào danh sách nếu thỏa cả 2 điều kiện
            if (matchesSearch && matchesPrice) {
                filteredList.add(coffee);
            }
        }

        // Cập nhật danh sách hiển thị trong RecyclerView
        adapterHot.updateFoodList(filteredList);
    }

    // Khi trở lại màn hình (onResume), làm mới lại dữ liệu từ database
    @Override
    protected void onResume() {
        super.onResume();
        // Lấy lại danh sách sản phẩm và áp dụng filter hiện tại
        originalList = coffeeDAO.getAllCoffee();
        applyFilters();
    }
}