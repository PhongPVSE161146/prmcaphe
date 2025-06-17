package com.example.project_of_me.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.project_of_me.Database.ConnectDB;
import com.example.project_of_me.Models.Cart;
import com.example.project_of_me.Models.CartItemDetail;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private ConnectDB dbHelper;

    public CartDAO(Context context) {
        dbHelper = new ConnectDB(context);
    }

    public SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    // Lấy cartID của giỏ hàng pending cho user
    public int getPendingCartId(SQLiteDatabase db, int userId) {
        Cursor cursor = db.rawQuery("SELECT cartID FROM carts WHERE userID=? AND status='pending' LIMIT 1",
                new String[]{String.valueOf(userId)});
        int cartId = -1;
        if (cursor.moveToFirst()) {
            cartId = cursor.getInt(0);
        }
        cursor.close();
        return cartId;
    }

    // Tạo giỏ hàng mới cho user và trả về cartID
    public int createCart(SQLiteDatabase db, int userId) {
        ContentValues values = new ContentValues();
        values.put("userID", userId);
        values.put("totalPrice", 0.0);
        values.put("status", "pending");
        long id = db.insert("carts", null, values);
        return (int) id;
    }

    // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
    public boolean checkCartItemExists(int cartId, int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = false;

        try {
            String query = "SELECT COUNT(*) FROM cart_items WHERE cartID = ? AND productID = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(cartId), String.valueOf(productId)});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int count = cursor.getInt(0);
                    exists = count > 0;
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return exists;
    }

    // Thêm sản phẩm vào giỏ hàng
    public boolean addCartItem(int userId, int productId, int quantity, double price) {
        Log.d("CartDAO", "Input - UserID: " + userId + ", ProductID: " + productId + ", Quantity: " + quantity + ", Price: " + price);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Lấy hoặc tạo giỏ hàng pending
            int cartId = getPendingCartId(db, userId);
            Log.d("CartDAO", "Pending cart ID: " + cartId);
            if (cartId == -1) {
                cartId = createCart(db, userId);
                Log.d("CartDAO", "Created new cart with ID: " + cartId);
                if (cartId == -1) {
                    Log.e("CartDAO", "Failed to create new cart");
                    return false;
                }
            }

            // Kiểm tra xem sản phẩm đã có trong giỏ chưa
            Cursor cursor = db.rawQuery("SELECT cartItemID, quantity FROM cart_items WHERE cartID = ? AND productID = ?",
                    new String[]{String.valueOf(cartId), String.valueOf(productId)});

            boolean success = false;
            if (cursor.moveToFirst()) {
                // Đã tồn tại: cập nhật số lượng
                int cartItemId = cursor.getInt(0);
                int existingQuantity = cursor.getInt(1);
                int newQuantity = existingQuantity + quantity;
                Log.d("CartDAO", "Updating existing item - CartItemID: " + cartItemId + ", New quantity: " + newQuantity);
                ContentValues values = new ContentValues();
                values.put("quantity", newQuantity);
                int rowsAffected = db.update("cart_items", values, "cartItemID = ?", new String[]{String.valueOf(cartItemId)});
                success = rowsAffected > 0;
                Log.d("CartDAO", "Update result: " + (success ? "Success" : "Failed"));
            } else {
                // Chưa có: thêm mới
                Log.d("CartDAO", "Adding new item to cart");
                ContentValues values = new ContentValues();
                values.put("cartID", cartId);
                values.put("productID", productId);
                values.put("quantity", quantity);
                values.put("price", price);
                long result = db.insert("cart_items", null, values);
                success = result != -1;
                Log.d("CartDAO", "Insert result: " + (success ? "Success, ID: " + result : "Failed"));
            }
            cursor.close();

            // Cập nhật tổng tiền giỏ hàng
            if (success) {
                updateCartTotalPrice(db, cartId);
                Log.d("CartDAO", "Cart total price updated");
            }

            return success;

        } catch (Exception e) {
            Log.e("CartDAO", "Error adding item to cart", e);
            return false;
        } finally {
            db.close();
        }
    }

    // Cập nhật tổng tiền giỏ hàng dựa trên các items
    public void updateCartTotalPrice(SQLiteDatabase db, int cartId) {
        try {
            // Tính tổng tiền từ các cart items
            Cursor cursor = db.rawQuery("SELECT SUM(quantity * price) FROM cart_items WHERE cartID = ?",
                    new String[]{String.valueOf(cartId)});

            double totalPrice = 0;
            if (cursor.moveToFirst()) {
                totalPrice = cursor.getDouble(0);
            }
            cursor.close();

            // Cập nhật tổng tiền vào bảng carts
            ContentValues values = new ContentValues();
            values.put("totalPrice", totalPrice);
            db.update("carts", values, "cartID = ?", new String[]{String.valueOf(cartId)});

        } catch (Exception e) {
            Log.e("CartDAO", "Error updating cart total price", e);
        }
    }

    // Lấy danh sách items trong giỏ hàng
    public List<CartItemDetail> getCartItems(int userId) {
        List<CartItemDetail> cartItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String query = "SELECT ci.cartItemID, ci.cartID, ci.productID, ci.quantity, ci.price, " +
                    "p.productName, p.fullDescription, p.imageURL " +
                    "FROM cart_items ci " +
                    "JOIN carts c ON ci.cartID = c.cartID " +
                    "JOIN products p ON ci.productID = p.productID " +
                    "WHERE c.userID = ? AND c.status = 'pending'";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                do {
                    CartItemDetail item = new CartItemDetail();
                    item.setOrderItemId(cursor.getInt(0));     // cartItemID
                    item.setOrderId(cursor.getInt(1));         // cartID
                    item.setFoodId(cursor.getInt(2));          // productID
                    item.setQuantity(cursor.getInt(3));
                    item.setPrice(cursor.getDouble(4));
                    item.setFoodName(cursor.getString(5));
                    item.setFoodDescription(cursor.getString(6));
                    item.setFoodImage(cursor.getString(7));
                    cartItems.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return cartItems;
    }

    // Lấy tổng tiền giỏ hàng
    public double getCartTotalPrice(int userId) {
        double total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT totalPrice FROM carts WHERE userID = ? AND status = 'pending'",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return total;
    }

    // Xóa một sản phẩm khỏi giỏ hàng
    public boolean clearCart(int cartItemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Lấy cartID trước khi xóa để cập nhật tổng tiền
            Cursor cursor = db.rawQuery("SELECT cartID FROM cart_items WHERE cartItemID = ?",
                    new String[]{String.valueOf(cartItemId)});
            int cartId = -1;
            if (cursor.moveToFirst()) {
                cartId = cursor.getInt(0);
            }
            cursor.close();

            // Xóa item cụ thể
            int rowsAffected = db.delete("cart_items", "cartItemID = ?", new String[]{String.valueOf(cartItemId)});
            boolean success = rowsAffected > 0;

            // Cập nhật tổng tiền nếu xóa thành công
            if (success && cartId != -1) {
                updateCartTotalPrice(db, cartId);
            }

            return success;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    // Cập nhật số lượng của cart item
    public boolean updateCartItemQuantity(int cartItemId, int newQuantity) {
        if (newQuantity <= 0) {
            return clearCart(cartItemId);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Lấy cartID để cập nhật tổng tiền sau
            Cursor cursor = db.rawQuery("SELECT cartID FROM cart_items WHERE cartItemID = ?",
                    new String[]{String.valueOf(cartItemId)});
            int cartId = -1;
            if (cursor.moveToFirst()) {
                cartId = cursor.getInt(0);
            }
            cursor.close();

            // Cập nhật số lượng
            ContentValues values = new ContentValues();
            values.put("quantity", newQuantity);
            int rowsAffected = db.update("cart_items", values, "cartItemID = ?", new String[]{String.valueOf(cartItemId)});
            boolean success = rowsAffected > 0;

            // Cập nhật tổng tiền
            if (success && cartId != -1) {
                updateCartTotalPrice(db, cartId);
            }

            return success;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    // Chuyển giỏ hàng thành đơn hàng (checkout)
    public int checkoutCart(int userId, String paymentMethod, String billingAddress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Lấy giỏ hàng pending
            int cartId = getPendingCartId(db, userId);
            if (cartId == -1) return -1;

            // Cập nhật status của cart thành 'completed'
            ContentValues cartValues = new ContentValues();
            cartValues.put("status", "completed");
            db.update("carts", cartValues, "cartID = ?", new String[]{String.valueOf(cartId)});

            // Tạo order mới
            ContentValues orderValues = new ContentValues();
            orderValues.put("cartID", cartId);
            orderValues.put("userID", userId);
            orderValues.put("paymentMethod", paymentMethod);
            orderValues.put("billingAddress", billingAddress);
            orderValues.put("orderStatus", "pending");

            long orderId = db.insert("orders", null, orderValues);
            
            if (orderId != -1) {
                // Xóa tất cả các item trong giỏ hàng
                db.delete("cart_items", "cartID = ?", new String[]{String.valueOf(cartId)});
                
                // Tạo giỏ hàng mới trống cho user
                ContentValues newCartValues = new ContentValues();
                newCartValues.put("userID", userId);
                newCartValues.put("totalPrice", 0.0);
                newCartValues.put("status", "pending");
                db.insert("carts", null, newCartValues);
            }
            
            return (int) orderId;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            db.close();
        }
    }

    // Lấy danh sách đơn hàng đã hoàn thành
    public List<Cart> getCompletedOrders(int userId) {
        List<Cart> carts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String query = "SELECT o.orderID, o.userID, c.totalPrice, o.orderStatus, o.orderDate " +
                    "FROM orders o " +
                    "JOIN carts c ON o.cartID = c.cartID " +
                    "WHERE o.userID = ? AND o.orderStatus = 'completed'";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                do {
                    Cart cart = new Cart();
                    cart.setId(cursor.getInt(0));
                    cart.setUserId(cursor.getInt(1));
                    cart.setTotalPrice(cursor.getDouble(2));
                    cart.setStatus(cursor.getString(3));
                    cart.setCreatedAt(cursor.getString(4));
                    carts.add(cart);
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return carts;
    }

    public boolean updateQuantity(int orderItemId, int newQuantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);

        int rowsAffected = db.update("cart_items", values, "cartItemID = ?", 
            new String[]{String.valueOf(orderItemId)});
        
        return rowsAffected > 0;
    }

    public Cart getOrderById(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cart order = null;

        try {
            String query = "SELECT o.orderID, o.userID, c.totalPrice, o.orderStatus, o.orderDate " +
                    "FROM orders o " +
                    "JOIN carts c ON o.cartID = c.cartID " +
                    "WHERE o.orderID = ?";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

            if (cursor.moveToFirst()) {
                order = new Cart();
                order.setId(cursor.getInt(0));
                order.setUserId(cursor.getInt(1));
                order.setTotalPrice(cursor.getDouble(2));
                order.setStatus(cursor.getString(3));
                order.setCreatedAt(cursor.getString(4));
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return order;
    }

    public int getCartItemCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;

        try {
            // Kiểm tra xem có giỏ hàng pending không
            Cursor cartCursor = db.rawQuery("SELECT cartID FROM carts WHERE userID = ? AND status = 'pending'",
                    new String[]{String.valueOf(userId)});
            
            if (!cartCursor.moveToFirst()) {
                cartCursor.close();
                return 0; // Không có giỏ hàng pending
            }
            cartCursor.close();

            // Đếm số lượng sản phẩm trong giỏ hàng
            String query = "SELECT COUNT(*) FROM cart_items ci " +
                    "JOIN carts c ON ci.cartID = c.cartID " +
                    "WHERE c.userID = ? AND c.status = 'pending'";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return count;
    }
    // Xóa toàn bộ giỏ hàng
    public boolean clearAllCartItems(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            int cartId = getPendingCartId(db, userId);
            if (cartId == -1) return false;

            // Xóa tất cả cart items
            db.delete("cart_items", "cartID = ?", new String[]{String.valueOf(cartId)});

            // Cập nhật tổng tiền về 0
            ContentValues values = new ContentValues();
            values.put("totalPrice", 0.0);
            db.update("carts", values, "cartID = ?", new String[]{String.valueOf(cartId)});

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }
}