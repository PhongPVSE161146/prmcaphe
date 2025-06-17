package com.example.project_of_me;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.Coffee;
import com.example.project_of_me.Models.User;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.FoodViewHolder> {
    private List<Coffee> foodList;
    private Context context;

    public ProductAdapter(Context context, List<Coffee> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Coffee currentFood = foodList.get(position);

        holder.foodName.setText(currentFood.getProductName());
        holder.foodDescription.setText(currentFood.getFullDescription());
        holder.foodPrice.setText(String.format("%,.0f₫", currentFood.getPrice()));
        String imagePath = currentFood.getImageURL();
//
//        // Convert byte array to Bitmap and set it to ImageView
        Bitmap bitmap = getBitmapFromFile(imagePath);
        if (bitmap != null) {
            holder.foodImg.setImageBitmap(bitmap);
        } else {
            holder.foodImg.setImageResource(R.drawable.ic_login);
        }
        // Lấy user_id từ SharedPreferences (ví dụ)
        int userIdTemp = 1; // mặc định nếu không lấy được từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");
        if (!email.isEmpty()) {
            UserDAO userDAO = new UserDAO(context);
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userIdTemp = user.getId();
            }
        }
        final int finalUserId = userIdTemp; // biến final cho lambda
        Log.d("HomeActivity", "finalUserId" + finalUserId);

        // Xử lý sự kiện click vào nút "Thêm Giỏ Hàng"
        holder.btnOrder.setOnClickListener(v -> {
            // Sử dụng OrderDAO để thêm món ăn vào giỏ hàng
            CartDAO cartDAO = new CartDAO(context);
            // Thêm order item, với số lượng mặc định là 1
            Log.d("FoodAdapter", "UserID: " + finalUserId);
            Log.d("FoodAdapter", "ProductID: " + currentFood.getProductID());
            Log.d("FoodAdapter", "Product Name: " + currentFood.getProductName());
            Log.d("FoodAdapter", "Price: " + currentFood.getPrice());
            boolean addedToCart = cartDAO.addCartItem(
                    finalUserId,
                    currentFood.getProductID(),
                    1,
                    currentFood.getPrice()
            );
            if (addedToCart) {
//                cartDAO.updateCartTotalPrice(orderId);
                Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện click vào item (mở chi tiết món ăn)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetail.class);
            intent.putExtra("foodId", currentFood.getProductID()); // Chỉ truyền ID món ăn
            context.startActivity(intent);
        });
    }
    public void updateFoodList(List<Coffee> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }

    private Bitmap getBitmapFromFile(String imagePath) {
        return BitmapFactory.decodeFile(imagePath);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName, foodDescription, foodPrice;
        public ImageView foodImg, ivFavorite;
        public ImageButton btnOrder;
        public Button btnViewReviews;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodDescription = itemView.findViewById(R.id.foodDescription);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImg = itemView.findViewById(R.id.foodImage);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }
}
