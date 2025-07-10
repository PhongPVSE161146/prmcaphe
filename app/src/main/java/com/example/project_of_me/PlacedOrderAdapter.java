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
    private List<Cart> cartList; // Danh sách các đơn hàng đã đặt

    // Constructor nhận context và danh sách đơn hàng
    public PlacedOrderAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo ViewHolder bằng cách inflate layout item_order_summary
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_summary, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        // Gán dữ liệu cho từng item trong danh sách đơn hàng
        Cart cart = cartList.get(position);

        holder.tvTotalPrice.setText(String.format("Tổng tiền: %,.0f₫", cart.getTotalPrice())); // Hiển thị tổng tiền
        holder.tvCreatedAt.setText("Ngày đặt: " + cart.getCreatedAt()); // Ngày đặt hàng
        holder.tvOrderStatus.setText("Trạng thái: " + cart.getStatus()); // Trạng thái đơn hàng

        // Xử lý sự kiện khi nhấn vào biểu tượng xóa đơn hàng
        holder.ivDeleteOrder.setOnClickListener(v -> {
            CartDAO orderDAO = new CartDAO(context); // Tạo đối tượng DAO để thao tác CSDL
            boolean isDeleted = orderDAO.clearCart(cart.getId()); // Xoá đơn hàng khỏi CSDL

            if (isDeleted) {
                // Nếu xoá thành công, xoá item khỏi danh sách và cập nhật RecyclerView
                cartList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartList.size());
                Toast.makeText(context, "Đã xoá đơn hàng", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu thất bại, hiển thị thông báo lỗi
                Toast.makeText(context, "Lỗi khi xoá đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng đơn hàng trong danh sách
        return cartList.size();
    }

    // ViewHolder đại diện cho từng item hiển thị đơn hàng
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName, tvTotalPrice, tvCreatedAt, tvOrderStatus;
        public ImageView ivDeleteOrder, ivOrderIcon;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liên kết các view trong layout với biến tương ứng
            ivOrderIcon = itemView.findViewById(R.id.ivOrderIcon);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            ivDeleteOrder = itemView.findViewById(R.id.ivDeleteOrder);
        }
    }
}
