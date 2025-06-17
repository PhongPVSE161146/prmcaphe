package com.example.project_of_me.Utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.User;

public class CartBadgeManager {
    private static CartBadgeManager instance;
    private TextView tvCartCount;
    private CartDAO cartDAO;
    private int userId;

    private CartBadgeManager(Context context, TextView tvCartCount) {
        this.tvCartCount = tvCartCount;
        this.cartDAO = new CartDAO(context);
        
        // Lấy userId từ SharedPreferences
        String email = context.getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("email", "");
        userId = 1; // Giá trị mặc định
        if (!email.isEmpty()) {
            UserDAO userDAO = new UserDAO(context);
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userId = user.getId();
            }
        }
        
        updateCartCount();
    }

    public static CartBadgeManager getInstance(Context context, TextView tvCartCount) {
        if (instance == null) {
            instance = new CartBadgeManager(context, tvCartCount);
        }
        return instance;
    }

    public void updateCartCount() {
        int count = cartDAO.getCartItemCount(userId);
        if (count > 0) {
            tvCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(count));
        } else {
            tvCartCount.setVisibility(View.GONE);
        }
    }

    public void incrementCartCount() {
        int currentCount = Integer.parseInt(tvCartCount.getText().toString());
        tvCartCount.setText(String.valueOf(currentCount + 1));
        tvCartCount.setVisibility(View.VISIBLE);
    }

    public void decrementCartCount() {
        int currentCount = Integer.parseInt(tvCartCount.getText().toString());
        if (currentCount > 1) {
            tvCartCount.setText(String.valueOf(currentCount - 1));
        } else {
            tvCartCount.setVisibility(View.GONE);
        }
    }
} 