package com.example.project_of_me;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.Coffee;
import com.example.project_of_me.Models.User;
import com.example.project_of_me.DAO.CoffeeDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.example.project_of_me.Utils.CartBadgeManager;

public class Home extends AppCompatActivity {
    private RecyclerView recyclerView, recyclerViewCold;
    private ProductAdapter adapterHot, adapterCold;
    private CoffeeDAO coffeeDAO;
    private BottomNavigationView bottomNavigationView;
    private EditText etSearch;
    private TextView tvSeeAll, tvSeeAll1, tvSeeAll2;
    private List<Coffee> listCoffeeHot;
    private List<Coffee> listCoffeeCold;
    private ImageView imgCart, imgUser;
    private TextView tvTitle;
    private TextView tvCartCount;
    private CartBadgeManager cartBadgeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm này được gọi khi activity được khởi tạo
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Gắn layout cho activity

        // Khởi tạo DAO, ánh xạ view và cấu hình RecyclerView
        // Lấy danh sách đồ uống từ cơ sở dữ liệu và gán vào adapter
        // Thiết lập sự kiện click cho các nút điều hướng và hình ảnh
        // Khởi tạo badge hiển thị số lượng sản phẩm trong giỏ hàng
    }

    @Override
    protected void onResume() {
        // Hàm này được gọi khi activity quay lại trạng thái foreground
        super.onResume();
        cartBadgeManager.updateCartCount(); // Cập nhật số lượng sản phẩm trong giỏ hàng
    }

    public Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        // Chuyển ID của ảnh drawable thành đối tượng Bitmap
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        // Chuyển bitmap thành mảng byte[] (dạng nhị phân) để lưu trữ hoặc truyền đi
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public String saveImageToInternalStorage(Bitmap bitmap) {
        // Lưu ảnh bitmap vào bộ nhớ trong của thiết bị và trả về đường dẫn đến ảnh
        Context context = getApplicationContext();
        FileOutputStream fos = null;
        File directory = context.getFilesDir(); // Lấy thư mục trong bộ nhớ nội bộ
        String fileName = "coffee_image_" + System.currentTimeMillis() + ".png";
        File file = new File(directory, fileName);

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Nén ảnh và ghi vào file
            fos.close();
        } catch (IOException e) {
            e.printStackTrace(); // Ghi log nếu có lỗi xảy ra
        }

        return file.getAbsolutePath(); // Trả về đường dẫn tuyệt đối tới ảnh đã lưu
    }

    private void insertSampleFoods() {
        // Thêm dữ liệu mẫu vào CSDL nếu CSDL đang trống

        if (coffeeDAO.getCoffeeCount() == 0) {
            // Chuyển ảnh drawable thành đường dẫn ảnh và tạo các đối tượng Coffee mẫu
            // Gán thông tin sản phẩm và lưu vào CSDL qua CoffeeDAO
            // Mỗi sản phẩm được khởi tạo riêng biệt với tên, mô tả, giá, hình ảnh và category

            Toast.makeText(this, "Đã thêm 5 món ăn mẫu vào CSDL", Toast.LENGTH_SHORT).show();
        } else {
            // Nếu dữ liệu đã có thì hiển thị thông báo
            Toast.makeText(this, "Dữ liệu mẫu đã có trong CSDL", Toast.LENGTH_SHORT).show();
        }
    }
}