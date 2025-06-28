package com.example.project_of_me.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class ImagePicker {
    // Hằng số để xác định yêu cầu chọn ảnh từ thư viện
    public static final int PICK_IMAGE_REQUEST = 1;
    // Hằng số để xác định yêu cầu chụp ảnh từ camera
    public static final int CAMERA_REQUEST_CODE = 2;

    // Phương thức để mở thư viện ảnh cho người dùng chọn ảnh
    public static void chooseImage(Activity activity) {
        // Tạo Intent với hành động ACTION_PICK để chọn ảnh từ bộ nhớ ngoài
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Chỉ định loại dữ liệu là ảnh
        intent.setType("image/*");
        // Khởi chạy Intent và chờ kết quả trả về
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public static void openCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    // Phương thức để mở camera chụp ảnh
    public static void openCamera(Activity activity) {
        // Tạo Intent với hành động ACTION_IMAGE_CAPTURE để chụp ảnh
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Khởi chạy Intent và chờ kết quả trả về
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    // Phương thức xử lý kết quả trả về khi người dùng chọn ảnh từ thư viện
    public static Uri handleImageResult(int requestCode, int resultCode, Intent data) {
        // Kiểm tra nếu kết quả OK và dữ liệu trả về không null
        if (resultCode == Activity.RESULT_OK && data != null) {
            // Nếu requestCode là chọn ảnh từ thư viện
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Trả về URI của ảnh đã chọn
                return data.getData(); // Trả về URI ảnh từ thư viện
            }
        }
        // Trường hợp không thành công thì trả về null
        return null;
    }

    // Phương thức xử lý kết quả trả về khi người dùng chụp ảnh bằng camera
    public static Bitmap handleCameraResult(int requestCode, int resultCode, Intent data) {
        // Kiểm tra nếu kết quả OK và dữ liệu trả về không null
        if (resultCode == Activity.RESULT_OK && data != null) {
            // Nếu requestCode là chụp ảnh từ camera
            if (requestCode == CAMERA_REQUEST_CODE) {
                // Trả về đối tượng Bitmap ảnh vừa chụp
                return (Bitmap) data.getExtras().get("data"); // Trả về ảnh chụp
            }
        }
        // Trường hợp không thành công thì trả về null
        return null;
    }
}
