package com.example.project_of_me;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.Cart;
import com.example.project_of_me.Models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlacedOrderActivity extends AppCompatActivity {
    private RecyclerView rvPlacedOrders;
    private TextView tvOrdersEmpty,tvTitle;
    private PlacedOrderAdapter placedOrderAdapter;
    private List<Cart> placedCarts;
    private CartDAO orderDAO;
    private int userId;
    private ImageView imgCart,imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_order);

        rvPlacedOrders = findViewById(R.id.rvPlacedOrders);
        imgCart = findViewById(R.id.imgCart);
        tvTitle = findViewById(R.id.tvTitle);
        imgUser = findViewById(R.id.imgUser);
//        tvOrdersEmpty = findViewById(R.id.tvOrdersEmpty);
        rvPlacedOrders.setLayoutManager(new LinearLayoutManager(this));
        placedCarts = new ArrayList<>();
        orderDAO = new CartDAO(this);

        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");
        userId = 1;
        if (!email.isEmpty()){
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null){
                userId = user.getId();
            }
        }
        loadPlacedOrders();
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlacedOrderActivity.this, PlacedOrderActivity.class);
                startActivity(intent);
                Toast.makeText(PlacedOrderActivity.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlacedOrderActivity.this, Home.class);
                startActivity(intent);
                Toast.makeText(PlacedOrderActivity.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlacedOrderActivity.this, Cart.class);
                startActivity(intent);
                Toast.makeText(PlacedOrderActivity.this, "Quantity increased", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlacedOrders() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            List<Cart> carts = orderDAO.getCompletedOrders(userId);
            handler.post(() -> {
                if (carts != null && !carts.isEmpty()) {
                    placedCarts.clear();
                    placedCarts.addAll(carts);
                    placedOrderAdapter = new PlacedOrderAdapter(PlacedOrderActivity.this, placedCarts);
                    rvPlacedOrders.setAdapter(placedOrderAdapter);
                    rvPlacedOrders.setVisibility(View.VISIBLE);
                    tvOrdersEmpty.setVisibility(View.GONE);
                } else {
                    rvPlacedOrders.setVisibility(View.GONE);
//                    tvOrdersEmpty.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}
