package com.example.project_of_me.DAO;
import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.graphics.Bitmap;

import com.example.project_of_me.Models.User;
import com.example.project_of_me.Database.ConnectDB;

public class UserDAO {
    private SQLiteDatabase db;
    private ConnectDB dbHelper;

    public UserDAO(Context context) {
        dbHelper = new ConnectDB(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Thêm user mới
    public boolean insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getName());
        values.put("email", user.getEmail());
        values.put("phoneNumber", user.getPhone());
        values.put("passwordHash", user.getPassword());
        values.put("address", user.getAddress());
        values.put("role", user.getRole());
        long result = db.insert("users", null, values);

        return result != -1;
    }

    // Lấy danh sách tất cả users
//    public List<User> getAllUsers() {
//        List<User> userList = new ArrayList<>();
//        Cursor cursor = db.rawQuery("SELECT * FROM users", null);
//        while (cursor.moveToNext()) {
//            User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
//            userList.add(user);
//        }
//        cursor.close();
//        return userList;
//    }

    // 🔹 Kiểm tra xem user có tồn tại với username và password không
    public boolean checkUser(String email, String password) {
        String query = "SELECT * FROM users WHERE email=? AND passwordHash=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
//    public boolean isAdmin(String email, String password) {
//        String query = "SELECT role FROM users WHERE email=? AND password=?";
//        Cursor cursor = db.rawQuery(query, new String[]{email, password});
//        boolean isAdmin = false;
//        if (cursor.moveToFirst()) {
//            String role = cursor.getString(cursor.getColumnIndex("role"));
//            isAdmin = "admin".equalsIgnoreCase(role);
//        }
//        cursor.close();
//        return isAdmin;
//    }


    // 🔹 Kiểm tra xem user có tồn tại chỉ với email không
    public boolean checkUser_email(String email) {
        String query = "SELECT * FROM users WHERE email=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    // 🔹 Cập nhật mật khẩu mới cho user
    public boolean updatePassword(String email, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("passwordHash", newPassword);

        int rowsAffected = db.update("users", values, "email=?", new String[]{email});
        return rowsAffected > 0;
    }


    public User getUserByEmail(String email) {
        User user = null;
        String query = "SELECT * FROM users WHERE email=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
        }
        cursor.close();
        return user;
    }


    public boolean updateUserInfo(String email, String newName, String newPhone) {
        ContentValues values = new ContentValues();
        values.put("name", newName);
        values.put("phone", newPhone);
        int rowsAffected = db.update("users", values, "email=?", new String[]{email});
        return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng bị ảnh hưởng
    }

    public boolean updateUserInfoImage(String email, String newName, String newPhone, Bitmap newimg) {
        ContentValues values = new ContentValues();
        values.put("name", newName);
        values.put("phone", newPhone);
        if (newimg != null) {
            values.put("avatar", convertBitmapToByteArray(newimg));
        }

        int rowsAffected = db.update("users", values, "email=?", new String[]{email});
        return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng bị ảnh hưởng
    }
}

