package com.example.project_of_me.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.project_of_me.Database.ConnectDB;
import com.example.project_of_me.Models.Coffee;

import java.util.ArrayList;
import java.util.List;

public class CoffeeDAO {
    private ConnectDB dbHelper;
    private SQLiteDatabase db;

    public CoffeeDAO(Context context) {
        dbHelper = new ConnectDB(context);
    }
    // Thêm món ăn
    public boolean insertCoffee(Coffee coffee) {
        // Lấy cơ sở dữ liệu ghi
        db = dbHelper.getWritableDatabase();

        // Khởi tạo ContentValues để lưu thông tin của món ăn
        ContentValues values = new ContentValues();
        // Gán giá trị từ đối tượng Coffee vào ContentValues
        values.put("ProductName", coffee.getProductName()); // Tên sản phẩm (tương ứng với 'name')
        values.put("BriefDescription", coffee.getBriefDescription()); // Mô tả ngắn (tương ứng với 'description')
        values.put("fullDescription", coffee.getFullDescription()); // Giá sản phẩm (tương ứng với 'price')
        values.put("technicalSpecifications", coffee.getTechnicalSpecifications()); // Giá sản phẩm (tương ứng với 'price')
        values.put("Price", coffee.getPrice()); // Giá sản phẩm (tương ứng với 'price')
        values.put("ImageURL", coffee.getImageURL()); // Đường dẫn hình ảnh (tương ứng với 'image_url')
        values.put("CategoryID", coffee.getCategoryID()); // ID danh mục (tùy vào database schema của bạn)
        // Chèn vào bảng Products (tên bảng trong database mới)
        long result = db.insert("Products", null, values);
        // Đóng cơ sở dữ liệu
        db.close();
        // Kiểm tra kết quả chèn vào
        if (result == -1) {
            Log.e("ProductDAO", "Insert product failed");
            return false;
        } else {
            Log.d("ProductDAO", "Insert product successful");
            return true;
        }
    }

    // Lấy số lượng món ăn trong CSDL
    public int getCoffeeCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM Products", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("CoffeeDAO", "Error getting coffee count", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return count;
    }

    //     Lấy danh sách món ăn
    public List<Coffee> getAllCoffee() {
        List<Coffee> coffeeList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                Coffee coffee = new Coffee();
                coffee.setProductID(cursor.getInt(cursor.getColumnIndex("productID")));
                coffee.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
                coffee.setBriefDescription(cursor.getString(cursor.getColumnIndex("briefDescription")));
                coffee.setFullDescription(cursor.getString(cursor.getColumnIndex("fullDescription")));
                coffee.setTechnicalSpecifications(cursor.getString(cursor.getColumnIndex("technicalSpecifications")));
                coffee.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                coffee.setImageURL(cursor.getString(cursor.getColumnIndex("imageURL")));
                coffee.setCategoryID(cursor.getInt(cursor.getColumnIndex("categoryID")));
                coffeeList.add(coffee);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return coffeeList;
    }

    //     Lấy danh sách món ăn
    public List<Coffee> getAllCoffeeByType(String type) {
        List<Coffee> coffeeList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        // Truy vấn JOIN giữa bảng Products và Categories để lấy loại sản phẩm (type)
        String query = "SELECT p.productID, p.ProductName, p.fullDescription, p.Price, p.ImageURL, p.CategoryID, c.CategoryName " +
                "FROM Products p " +
                "JOIN Categories c ON p.CategoryID = c.CategoryID " +
                "WHERE c.CategoryName = ?";
        // Thực hiện truy vấn với loại sản phẩm (type)
        Cursor cursor = db.rawQuery(query, new String[]{type});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Coffee coffee = new Coffee();
                coffee.setProductID(cursor.getInt(cursor.getColumnIndex("productID")));
                coffee.setProductName(cursor.getString(cursor.getColumnIndex("productName"))); // Lấy tên món từ bảng Products
//                coffee.setBriefDescription(cursor.getString(cursor.getColumnIndex("briefDescription"))); // Mô tả ngắn
                coffee.setFullDescription(cursor.getString(cursor.getColumnIndex("fullDescription"))); // Mô tả ngắn
                coffee.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                coffee.setImageURL(cursor.getString(cursor.getColumnIndex("imageURL")));
                coffee.setCategoryID(cursor.getInt(cursor.getColumnIndex("categoryID"))); // Lưu CategoryID
                coffee.setType(cursor.getString(cursor.getColumnIndex("categoryName"))); // Gán type từ CategoryName
                coffeeList.add(coffee);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return coffeeList;
    }


    // Lấy món ăn theo ID
    public Coffee getCoffeeById(int id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE productID = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            Coffee food = new Coffee();
            food.setProductID(cursor.getInt(0));
            food.setProductName(cursor.getString(1));
            food.setBriefDescription(cursor.getString(2));
            food.setFullDescription(cursor.getString(3));
            food.setTechnicalSpecifications(cursor.getString(4));
            food.setPrice(cursor.getDouble(5));
            food.setImageURL(cursor.getString(6));
            food.setCategoryID(cursor.getInt(7));
            cursor.close();
            db.close();
            return food;
        }

        cursor.close();
        db.close();
        return null; // Không tìm thấy món ăn
    }

//    public boolean updateFood(Coffee food) {
//        db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("name", food.getName());
//        values.put("description", food.getDescription());
//        values.put("price", food.getPrice());
//        values.put("image_url", food.getImage_url());
//        values.put("status", food.getStatus());
//
//        int result = db.update("foods", values, "id = ?", new String[]{String.valueOf(food.getId())});
//        db.close();
//
//        if (result == 0) {
//            Log.e("FoodDAO", "Update food failed");
//            return false;
//        } else {
//            Log.d("FoodDAO", "Update food successful");
//            return true;
//        }
//    }
//    public boolean deleteFood(int id) {
//        db = dbHelper.getWritableDatabase();
//        int result = db.delete("foods", "id = ?", new String[]{String.valueOf(id)});
//        db.close();
//
//        if (result == 0) {
//            Log.e("FoodDAO", "Delete food failed");
//            return false;
//        } else {
//            Log.d("FoodDAO", "Delete food successful");
//            return true;
//        }
//    }


}
