package com.example.project_of_me;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_of_me.Utils.CartBadgeManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.Models.CartItemDetail;
import com.example.project_of_me.Models.Coffee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<CartItemDetail> orderItemList;
    // Sử dụng Set để lưu vị trí các item được tick chọn
    private Set<Integer> selectedPositions = new HashSet<>();
    private Coffee food;
    private TextView tvTotalPrice;  // Add this reference
    private CartDAO cartDAO;
    // Thêm Map để lưu số lượng tạm thời
    private Map<Integer, Integer> tempQuantities = new HashMap<>();

    public OrderAdapter(Context context, List<CartItemDetail> orderItemList, TextView tvTotalPrice) {
        this.context = context;
        this.orderItemList = orderItemList;
        this.tvTotalPrice = tvTotalPrice;
        this.cartDAO = new CartDAO(context);
        // Khởi tạo tempQuantities với số lượng hiện tại
        for (int i = 0; i < orderItemList.size(); i++) {
            tempQuantities.put(i, orderItemList.get(i).getQuantity());
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout dành riêng cho order item (item_order.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        CartItemDetail item = orderItemList.get(position);
        holder.foodName.setText(item.getFoodName());
        holder.foodDescription.setText(item.getFoodDescription());

        // Sử dụng số lượng tạm thời từ tempQuantities, nếu không có thì dùng số lượng từ item
        Integer currentQuantity = tempQuantities.get(position);
        if (currentQuantity == null) {
            currentQuantity = item.getQuantity();
            tempQuantities.put(position, currentQuantity);
        }
        holder.tvQuantity.setText(String.valueOf(currentQuantity));
        holder.foodPrice.setText(String.format("%,.0f₫ x %d", item.getPrice(), currentQuantity));

        // Load hình ảnh
        String imagePath = item.getFoodImage();
        Bitmap bitmap = getBitmapFromFile(imagePath);
        if (bitmap != null) {
            holder.foodImg.setImageBitmap(bitmap);
        } else {
            holder.foodImg.setImageResource(R.drawable.ic_login);
        }

        // Xử lý CheckBox
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(selectedPositions.contains(holder.getAdapterPosition()));
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (isChecked) {
                    selectedPositions.add(adapterPosition);
                    // Khi tick chọn, cập nhật số lượng vào database
                    updateQuantityInDatabase(item.getOrderItemId(),
                            tempQuantities.get(adapterPosition));
                } else {
                    selectedPositions.remove(adapterPosition);
                }
                updateTotalPrice();
            }
        });

        // Xử lý nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int newQuantity = tempQuantities.get(adapterPosition) + 1;
                tempQuantities.put(adapterPosition, newQuantity);

                // Cập nhật UI
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                holder.foodPrice.setText(String.format("%,.0f₫ x %d", item.getPrice(), newQuantity));
                // Nếu item đang được chọn, cập nhật tổng tiền
                updateTotalPrice();
                updateQuantityInDatabase(item.getOrderItemId(), newQuantity);
                // Cập nhật vào database nếu đang được chọn
            }
        });

        // Xử lý nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int currentQty = tempQuantities.get(adapterPosition);
                if (currentQty > 1) {
                    int newQuantity = currentQty - 1;
                    tempQuantities.put(adapterPosition, newQuantity);

                    // Cập nhật UI
                    holder.tvQuantity.setText(String.valueOf(newQuantity));
                    holder.foodPrice.setText(String.format("%,.0f₫ x %d", item.getPrice(), newQuantity));
                    updateTotalPrice();
                    updateQuantityInDatabase(item.getOrderItemId(), newQuantity);
                } else {
                    Toast.makeText(context, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                boolean deleted = cartDAO.clearCart(orderItemList.get(adapterPosition).getOrderItemId());
                if (deleted) {
                    Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    orderItemList.remove(adapterPosition);
                    selectedPositions.remove(adapterPosition);

                    // Cập nhật lại tempQuantities cho các item còn lại
                    Map<Integer, Integer> newTempQuantities = new HashMap<>();
                    for (int i = 0; i < orderItemList.size(); i++) {
                        newTempQuantities.put(i, orderItemList.get(i).getQuantity());
                    }
                    tempQuantities.clear();
                    tempQuantities.putAll(newTempQuantities);

                    notifyItemRemoved(adapterPosition);
                    notifyItemRangeChanged(adapterPosition, orderItemList.size());
                    updateTotalPrice();

                    // Cập nhật badge giỏ hàng
                    CartBadgeManager.getInstance(context, null).updateCartCount();
                } else {
                    Toast.makeText(context, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    // Phương thức trả về danh sách OrderItemDetail được tick chọn
    public List<CartItemDetail> getSelectedItems() {
        List<CartItemDetail> selectedItems = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            selectedItems.add(orderItemList.get(pos));
        }
        return selectedItems;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName, foodDescription, foodPrice, tvQuantity;
        public ImageView foodImg;
        public ImageButton btnDelete, btnIncrease, btnDecrease;
        public CheckBox cbSelect;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodDescription = itemView.findViewById(R.id.foodDescription);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImg = itemView.findViewById(R.id.foodImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (Integer pos : selectedPositions) {
            CartItemDetail item = orderItemList.get(pos);
            // Sử dụng số lượng tạm thời để tính tổng tiền
            totalPrice += item.getPrice() * tempQuantities.get(pos);
        }

        if (tvTotalPrice != null) {
            tvTotalPrice.setText(String.format("%,.0f₫", totalPrice));
        }
    }

    // Phương thức cập nhật số lượng vào database
    private void updateQuantityInDatabase(int orderItemId, int newQuantity) {
        boolean updated = cartDAO.updateQuantity(orderItemId, newQuantity);
        if (!updated) {
            Toast.makeText(context, "Không thể cập nhật số lượng", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức để lấy số lượng hiện tại của một item
    public int getCurrentQuantity(int position) {
        return tempQuantities.getOrDefault(position, 1);
    }

    private Bitmap getBitmapFromFile(String imagePath) {
        return BitmapFactory.decodeFile(imagePath);
    }
}
