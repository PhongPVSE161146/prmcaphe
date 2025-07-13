package com.example.project_of_me;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.Models.Cart;
import java.util.List;

public class PlacedOrderAdapter extends RecyclerView.Adapter<PlacedOrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Cart> cartList;

    public PlacedOrderAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_summary, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Cart cart = cartList.get(position);

        holder.tvTotalPrice.setText(String.format("Tổng tiền: %,.0f₫", cart.getTotalPrice()));
        holder.tvCreatedAt.setText("Ngày đặt: " + cart.getCreatedAt());
        holder.tvOrderStatus.setText("Trạng thái: " + cart.getStatus());
        // Xử lý xóa đơn hàng
        holder.ivDeleteOrder.setOnClickListener(v -> {
            CartDAO orderDAO = new CartDAO(context);
            boolean isDeleted = orderDAO.clearCart(cart.getId());

            if (isDeleted) {
                cartList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartList.size());
                Toast.makeText(context, "Đã xoá đơn hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Lỗi khi xoá đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName, tvTotalPrice, tvCreatedAt, tvOrderStatus;
        public ImageView ivDeleteOrder, ivOrderIcon;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrderIcon = itemView.findViewById(R.id.ivOrderIcon);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            ivDeleteOrder = itemView.findViewById(R.id.ivDeleteOrder);
        }
    }
}
