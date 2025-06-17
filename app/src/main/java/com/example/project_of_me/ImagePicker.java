package com.example.project_of_me.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class ImagePicker {
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int CAMERA_REQUEST_CODE = 2;

    public static void chooseImage(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public static void openCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public static Uri handleImageResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                return data.getData(); // Trả về URI ảnh từ thư viện
            }
        }
        return null;
    }

    public static Bitmap handleCameraResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                return (Bitmap) data.getExtras().get("data"); // Trả về ảnh chụp
            }
        }
        return null;
    }
}
