package com.example.project_of_me.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project_of_me.Database.ConnectDB;
import com.example.project_of_me.Models.Cart;
import com.example.project_of_me.Models.CartItem;
import com.example.project_of_me.Models.CartItemDetail;
import com.example.project_of_me.Models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private ConnectDB dbHelper;
    private SQLiteDatabase database;

    public OrderDAO(Context context) {
        dbHelper = new ConnectDB(context);
    }

    // Tạo đơn hàng mới
    public int createOrder(int userId, double totalPrice) {
        database = dbHelper.getWritableDatabase();
        
        // Tạo cart mới trước
        ContentValues cartValues = new ContentValues();
        cartValues.put("userID", userId);
        cartValues.put("totalPrice", totalPrice);
        cartValues.put("status", "pending");
        long cartId = database.insert("carts", null, cartValues);
        
        if (cartId == -1) {
            return -1;
        }

        // Sau đó tạo order và liên kết với cart
        ContentValues orderValues = new ContentValues();
        orderValues.put("userID", userId);
        orderValues.put("cartID", cartId);
        orderValues.put("orderStatus", "pending");
        orderValues.put("orderDate", System.currentTimeMillis());
        orderValues.put("paymentMethod", "Cash"); // Mặc định thanh toán tiền mặt
        orderValues.put("billingAddress", ""); // Để trống, có thể cập nhật sau

        long orderId = database.insert("orders", null, orderValues);
        return (int) orderId;
    }

    // Thêm sản phẩm vào đơn hàng
    public boolean addOrderItem(int orderId, int coffeeId, int quantity, double price) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("coffee_id", coffeeId);
        values.put("quantity", quantity);
        values.put("price", price);

        long result = database.insert("order_items", null, values);
        return result != -1;
    }

    // Cập nhật trạng thái đơn hàng
    public boolean updateOrderStatus(int orderId, String status) {
        database = dbHelper.getWritableDatabase();
        
        // Cập nhật trạng thái order
        ContentValues orderValues = new ContentValues();
        orderValues.put("orderStatus", status);
        int orderResult = database.update("orders", orderValues, "orderID = ?", new String[]{String.valueOf(orderId)});
        
        if (orderResult > 0) {
            // Lấy cartID từ order
            Cursor cursor = database.query("orders", new String[]{"cartID"}, "orderID = ?", 
                new String[]{String.valueOf(orderId)}, null, null, null);
            
            if (cursor.moveToFirst()) {
                int cartId = cursor.getInt(cursor.getColumnIndex("cartID"));
                cursor.close();
                
                // Cập nhật trạng thái cart tương ứng
                ContentValues cartValues = new ContentValues();
                cartValues.put("status", status);
                database.update("carts", cartValues, "cartID = ?", new String[]{String.valueOf(cartId)});
            }
        }
        
        return orderResult > 0;
    }

    // Lấy danh sách đơn hàng đã hoàn thành của user
    public List<Cart> getCompletedOrders(int userId) {
        database = dbHelper.getReadableDatabase();
        List<Cart> orders = new ArrayList<>();

        String query = "SELECT o.orderID, o.userID, c.totalPrice, o.orderStatus, o.orderDate " +
                      "FROM orders o " +
                      "JOIN carts c ON o.cartID = c.cartID " +
                      "WHERE o.userID = ? AND o.orderStatus = 'completed' " +
                      "ORDER BY o.orderDate DESC";
                      
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Cart order = new Cart();
                order.setId(cursor.getInt(cursor.getColumnIndex("orderID")));
                order.setUserId(cursor.getInt(cursor.getColumnIndex("userID")));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndex("totalPrice")));
                order.setStatus(cursor.getString(cursor.getColumnIndex("orderStatus")));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    // Lấy chi tiết các sản phẩm trong đơn hàng
    public List<CartItemDetail> getOrderItems(int orderId) {
        database = dbHelper.getReadableDatabase();
        List<CartItemDetail> items = new ArrayList<>();

        String query = "SELECT oi.*, c.name as food_name, c.description as food_description, c.image as food_image " +
                      "FROM order_items oi " +
                      "JOIN coffees c ON oi.coffee_id = c.id " +
                      "WHERE oi.order_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor.moveToFirst()) {
            do {
                CartItemDetail item = new CartItemDetail();
                item.setOrderItemId(cursor.getInt(cursor.getColumnIndex("id")));
                item.setOrderId(cursor.getInt(cursor.getColumnIndex("order_id")));
                item.setFoodId(cursor.getInt(cursor.getColumnIndex("coffee_id")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                item.setFoodName(cursor.getString(cursor.getColumnIndex("food_name")));
                item.setFoodDescription(cursor.getString(cursor.getColumnIndex("food_description")));
                item.setFoodImage(cursor.getString(cursor.getColumnIndex("food_image")));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    // Xóa một sản phẩm khỏi đơn hàng
    public boolean deleteOrderItem(int orderItemId) {
        database = dbHelper.getWritableDatabase();
        int result = database.delete("order_items", "id = ?", new String[]{String.valueOf(orderItemId)});
        return result > 0;
    }

    // Lấy tổng tiền của đơn hàng
    public double getOrderTotalPrice(int orderId) {
        database = dbHelper.getReadableDatabase();
        double totalPrice = 0;

        String query = "SELECT total_price FROM orders WHERE id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor.moveToFirst()) {
            totalPrice = cursor.getDouble(cursor.getColumnIndex("total_price"));
        }
        cursor.close();
        return totalPrice;
    }

    // Cập nhật tổng tiền đơn hàng
    public boolean updateOrderTotalPrice(int orderId, double totalPrice) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("total_price", totalPrice);

        int result = database.update("orders", values, "id = ?", new String[]{String.valueOf(orderId)});
        return result > 0;
    }

    // Lấy danh sách sản phẩm trong đơn hàng của user
    public List<CartItemDetail> getOrderItemsByUserId(int userId) {
        database = dbHelper.getReadableDatabase();
        List<CartItemDetail> items = new ArrayList<>();

        String query = "SELECT oi.*, c.name as food_name, c.description as food_description, c.image as food_image " +
                      "FROM order_items oi " +
                      "JOIN orders o ON oi.order_id = o.id " +
                      "JOIN coffees c ON oi.coffee_id = c.id " +
                      "WHERE o.user_id = ? AND o.status = 'pending'";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                CartItemDetail item = new CartItemDetail();
                item.setOrderItemId(cursor.getInt(cursor.getColumnIndex("id")));
                item.setOrderId(cursor.getInt(cursor.getColumnIndex("order_id")));
                item.setFoodId(cursor.getInt(cursor.getColumnIndex("coffee_id")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                item.setFoodName(cursor.getString(cursor.getColumnIndex("food_name")));
                item.setFoodDescription(cursor.getString(cursor.getColumnIndex("food_description")));
                item.setFoodImage(cursor.getString(cursor.getColumnIndex("food_image")));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }
}
