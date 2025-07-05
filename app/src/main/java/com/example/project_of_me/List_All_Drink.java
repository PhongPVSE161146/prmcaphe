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
    private RecyclerView recyclerView, recyclerViewCold;
    private ProductAdapter adapterHot, adapterCold;
    private CoffeeDAO coffeeDAO;
    private BottomNavigationView bottomNavigationView;
    private EditText etSearch;
    private TextView tvSeeAll, tvSeeAll1, tvSeeAll2;
    private List<Coffee> listCoffee;
    private ImageView imgCart, imgUser;
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
        tvTitle = findViewById(R.id.tvTitle);

        etSearch = findViewById(R.id.etSearch);

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterUnder50k = findViewById(R.id.btnFilterUnder50k);
        btnFilter50kTo100k = findViewById(R.id.btnFilter50kTo100k);
        btnFilterOver100k = findViewById(R.id.btnFilterOver100k);

        coffeeDAO = new CoffeeDAO(this);
        originalList = coffeeDAO.getAllCoffee();

        adapterHot = new ProductAdapter(this, originalList);
        recyclerView.setAdapter(adapterHot);

        setupSearch();
        setupFilters();

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_All_Drink.this, PlacedOrderActivity.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Chuyển đến đơn hàng đã đặt", Toast.LENGTH_SHORT).show();
            }
        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_All_Drink.this, Home.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Trở về trang chủ", Toast.LENGTH_SHORT).show();
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_All_Drink.this, Order.class);
                startActivity(intent);
                Toast.makeText(List_All_Drink.this, "Chuyển đến giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

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

        updateFilterButtonStates(btnFilterAll); // Default chọn "Tất cả"
    }

    private void updateFilterButtonStates(Button selectedButton) {
        btnFilterAll.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilterUnder50k.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilter50kTo100k.setBackgroundTintList(getColorStateList(R.color.white));
        btnFilterOver100k.setBackgroundTintList(getColorStateList(R.color.white));

        selectedButton.setBackgroundTintList(getColorStateList(R.color.blue));
        selectedButton.setTextColor(getColor(R.color.black));
    }

    private void applyFilters() {
        List<Coffee> filteredList = new ArrayList<>();

        for (Coffee coffee : originalList) {
            boolean matchesSearch = currentSearchText.isEmpty() ||
                    coffee.getProductName().toLowerCase().contains(currentSearchText) ||
                    coffee.getFullDescription().toLowerCase().contains(currentSearchText);

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
                default:
                    matchesPrice = true;
                    break;
            }

            if (matchesSearch && matchesPrice) {
                filteredList.add(coffee);
            }
        }

        adapterHot.updateFoodList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        originalList = coffeeDAO.getAllCoffee();
        applyFilters();
    }
}
