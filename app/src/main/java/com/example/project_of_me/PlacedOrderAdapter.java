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
    // Context để truy cập giao diện và resource
    private Context context;

    // Danh sách các đơn hàng (Cart)
    private List<Cart> cartList;

    // Constructor khởi tạo adapter với context và danh sách đơn hàng
    public PlacedOrderAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_order_summary để tạo view cho mỗi item
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_summary, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        // Lấy đơn hàng tại vị trí hiện tại
        Cart cart = cartList.get(position);

        // Hiển thị tổng tiền theo định dạng tiền Việt
        holder.tvTotalPrice.setText(String.format("Tổng tiền: %,.0f₫", cart.getTotalPrice()));

        // Hiển thị ngày tạo đơn hàng
        holder.tvCreatedAt.setText("Ngày đặt: " + cart.getCreatedAt());

        // Hiển thị trạng thái đơn hàng
        holder.tvOrderStatus.setText("Trạng thái: " + cart.getStatus());

        // Gắn sự kiện xóa đơn hàng khi nhấn icon thùng rác
        holder.ivDeleteOrder.setOnClickListener(v -> {
            // Tạo DAO để xử lý xoá trong database
            CartDAO orderDAO = new CartDAO(context);

            // Gọi hàm xoá đơn hàng từ DB dựa vào ID
            boolean isDeleted = orderDAO.clearCart(cart.getId());

            if (isDeleted) {
                // Xoá thành công: xoá khỏi danh sách hiển thị và cập nhật UI
                cartList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartList.size());
                Toast.makeText(context, "Đã xoá đơn hàng", Toast.LENGTH_SHORT).show();
            } else {
                // Xoá thất bại: hiển thị lỗi
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
