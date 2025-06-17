package com.example.project_of_me.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.project_of_me.DAO.CartDAO;
import com.example.project_of_me.DAO.UserDAO;
import com.example.project_of_me.Models.User;
import com.example.project_of_me.Order;
import com.example.project_of_me.R;

public class CartNotificationService extends Service {
    private static final String TAG = "CartNotificationService";
    private static final String CHANNEL_ID = "cart_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final long CHECK_INTERVAL = 60000; // Kiểm tra mỗi phút

    private Handler handler;
    private CartDAO cartDAO;
    private int userId;
    private int lastCartCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        handler = new Handler(Looper.getMainLooper());
        cartDAO = new CartDAO(this);
        createNotificationChannel();
        
        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        userId = 1; // Giá trị mặc định
        if (!email.isEmpty()) {
            UserDAO userDAO = new UserDAO(this);
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userId = user.getId();
            }
        }
        Log.d(TAG, "User ID: " + userId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        
        // Tạo notification cho foreground service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cart)
            .setContentTitle("Theo dõi giỏ hàng")
            .setContentText("Đang theo dõi số lượng sản phẩm trong giỏ hàng")
            .setPriority(NotificationCompat.PRIORITY_LOW);

        // Chuyển service thành foreground service
        startForeground(NOTIFICATION_ID, builder.build());
        
        startCartMonitoring();
        return START_STICKY;
    }

    private void startCartMonitoring() {
        // Kiểm tra ngay lập tức khi service khởi động
        checkCartCount();
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkCartCount();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        }, CHECK_INTERVAL);
    }

    private void checkCartCount() {
        int currentCount = cartDAO.getCartItemCount(userId);
        Log.d(TAG, "Current cart count: " + currentCount + ", Last count: " + lastCartCount);
        if (currentCount != lastCartCount) {
            Log.d(TAG, "Cart count changed, updating notification");
            updateNotification(currentCount);
            lastCartCount = currentCount;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Cart Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for cart updates");
            channel.setShowBadge(true); // Cho phép hiển thị badge
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void updateNotification(int cartCount) {
        Log.d(TAG, "Updating notification with count: " + cartCount);
        Intent intent = new Intent(this, Order.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = "Giỏ hàng của bạn";
        String content = cartCount > 0 
            ? "Bạn có " + cartCount + " sản phẩm trong giỏ hàng"
            : "Giỏ hàng của bạn đang trống";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cart)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        // Thêm huy hiệu số lượng sản phẩm vào biểu tượng ứng dụng
        if (cartCount > 0) {
            builder.setNumber(cartCount);
        }

        NotificationManager notificationManager = 
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (cartCount > 0) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        handler.removeCallbacksAndMessages(null);
    }
} 