package com.example.project_of_me.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class ImagePicker {
    public static final int PICK_IMAGE_REQUEST = 1; // Mã request để chọn ảnh từ thư viện
    public static final int CAMERA_REQUEST_CODE = 2; // Mã request để chụp ảnh từ camera

    // Hàm mở thư viện để chọn ảnh từ bộ nhớ thiết bị
    public static void chooseImage(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Gọi intent chọn ảnh
        intent.setType("image/*"); // Chỉ chọn các file là ảnh
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST); // Bắt đầu activity chọn ảnh
    }

    // Hàm mở camera để chụp ảnh mới
    public static void openCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Gọi intent chụp ảnh bằng camera
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE); // Bắt đầu activity chụp ảnh
    }

    // Hàm xử lý kết quả trả về khi người dùng chọn ảnh từ thư viện
    public static Uri handleImageResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                return data.getData(); // Trả về đường dẫn (URI) của ảnh đã chọn
            }
        }
        return null; // Nếu không hợp lệ thì trả về null
    }

    // Hàm xử lý kết quả trả về khi người dùng chụp ảnh bằng camera
    public static Bitmap handleCameraResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                return (Bitmap) data.getExtras().get("data"); // Lấy ảnh dưới dạng Bitmap từ dữ liệu trả về
            }
        }
        return null; // Nếu không hợp lệ thì trả về null
    }
}
