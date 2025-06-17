package com.example.project_of_me.Database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

public class ConnectDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SalesAppDB.db";
    private static final int DATABASE_VERSION = 4;

    // Create users table
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "passwordHash TEXT NOT NULL, " +  // Changed 'password' to 'passwordHash'
                    "email TEXT UNIQUE NOT NULL, " +
                    "phoneNumber TEXT, " +
                    "address TEXT, " +
                    "role TEXT NOT NULL, " +  // Added 'role'
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    private static final String CREATE_CATEGORIES_TABLE =
            "CREATE TABLE IF NOT EXISTS categories (\n" +
                    "    categoryID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +  // Equivalent to CategoryID with auto-increment
                    "    categoryName TEXT NOT NULL\n" +  // Equivalent to CategoryName (using TEXT in SQLite instead of NVARCHAR)
                    ");\n";

    // Create foods table
    private static final String CREATE_PRODUCTS_TABLE =
            "CREATE TABLE IF NOT EXISTS products (\n" +
                    "    productID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +  // Equivalent to ProductID
                    "    productName TEXT NOT NULL,\n" +  // Equivalent to ProductName
                    "    briefDescription TEXT,\n" +  // Equivalent to BriefDescription
                    "    fullDescription TEXT,\n" +  // Equivalent to FullDescription
                    "    technicalSpecifications TEXT,\n" +  // Equivalent to TechnicalSpecifications
                    "    price REAL NOT NULL,\n" +  // Equivalent to Price (REAL in SQLite for decimal numbers)
                    "    imageURL TEXT,\n" +  // Equivalent to ImageURL (use TEXT for URLs in SQLite)
                    "    categoryID INTEGER,\n" +  // Equivalent to CategoryID (INTEGER for foreign keys)
                    "    FOREIGN KEY (categoryID) REFERENCES categories(categoryID)\n" +  // Foreign key constraint for category
                    ");\n";


    private static final String CREATE_CART_TABLE =
            "CREATE TABLE IF NOT EXISTS carts (\n" +
                    "    cartID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    userID INTEGER,\n" +
                    "    totalPrice REAL NOT NULL,\n" +
                    "    status TEXT NOT NULL,\n" +
                    "    FOREIGN KEY (userID) REFERENCES users(id)\n" +
                    ");";

    private static final String CREATE_CART_ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS cart_items (\n" +
                    "    cartItemID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    cartID INTEGER,\n" +
                    "    productID INTEGER,\n" +
                    "    quantity INTEGER NOT NULL,\n" +
                    "    price REAL NOT NULL,\n" +
                    "    FOREIGN KEY (cartID) REFERENCES carts(cartID),\n" +
                    "    FOREIGN KEY (productID) REFERENCES products(productID)\n" +
                    ");";

    private static final String CREATE_ORDERS_TABLE =
            "CREATE TABLE IF NOT EXISTS orders (\n" +
                    "    orderID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    cartID INTEGER,\n" +
                    "    userID INTEGER,\n" +
                    "    paymentMethod TEXT NOT NULL,\n" +
                    "    billingAddress TEXT NOT NULL,\n" +
                    "    orderStatus TEXT NOT NULL,\n" +
                    "    orderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    FOREIGN KEY (cartID) REFERENCES carts(cartID),\n" +
                    "    FOREIGN KEY (userID) REFERENCES users(id)\n" +
                    ");";

    private static final String CREATE_PAYMENTS_TABLE =
            "CREATE TABLE IF NOT EXISTS payments (\n" +
                    "    paymentID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    orderID INTEGER,\n" +
                    "    amount REAL NOT NULL,\n" +
                    "    paymentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    paymentStatus TEXT NOT NULL,\n" +
                    "    FOREIGN KEY (orderID) REFERENCES orders(orderID)\n" +
                    ");";

    private static final String CREATE_NOTIFICATIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS notifications (\n" +
                    "    notificationID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    userID INTEGER,\n" +
                    "    message TEXT,\n" +
                    "    isRead INTEGER NOT NULL DEFAULT 0,\n" +
                    "    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    FOREIGN KEY (userID) REFERENCES users(id)\n" +
                    ");";

    private static final String CREATE_CHAT_MESSAGES_TABLE =
            "CREATE TABLE IF NOT EXISTS chat_messages (\n" +
                    "    chatMessageID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    userID INTEGER,\n" +
                    "    message TEXT,\n" +
                    "    sentAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    FOREIGN KEY (userID) REFERENCES users(id)\n" +
                    ");";

    private static final String CREATE_STORE_LOCATIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS store_locations (\n" +
                    "    locationID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    latitude REAL NOT NULL,\n" +
                    "    longitude REAL NOT NULL,\n" +
                    "    address TEXT NOT NULL\n" +
                    ");";




    public ConnectDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);
        db.execSQL(CREATE_PAYMENTS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_CART_ITEMS_TABLE);
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_CHAT_MESSAGES_TABLE);
        db.execSQL(CREATE_STORE_LOCATIONS_TABLE);


        Log.d("ConnectDB", "Database tables created successfully!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try { db.execSQL("DROP TABLE IF EXISTS users"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS categories"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS products"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS carts"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS cart_items"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS orders"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS payments"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS notifications"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS chat_messages"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }
        try { db.execSQL("DROP TABLE IF EXISTS store_locations"); } catch (Exception e) { Log.e("ConnectDB", e.getMessage()); }

        // Recreate tables
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys = ON");
    }
}
