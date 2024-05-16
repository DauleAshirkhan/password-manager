package com.example.projectpart1;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_database";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_PASSWORD_ID = "password_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_DESCRIPTION = "description"; // Новый столбец для описания пароля

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_PASSWORD + " TEXT" +
                ")";
        db.execSQL(createUserTableQuery);

        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_PASSWORDS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID + " INTEGER," +
                COLUMN_USER_PASSWORD + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT" + // Добавляем столбец для описания пароля
                ")";
        db.execSQL(createTableQuery);
    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Если версия базы данных меньше 2, добавляем столбец user_password
            db.execSQL("ALTER TABLE " + TABLE_PASSWORDS + " ADD COLUMN " + COLUMN_DESCRIPTION + " TEXT");

        }
        if (oldVersion < 3) {
            // Если версия базы данных меньше 3, добавляем столбец description
            db.execSQL("ALTER TABLE " + TABLE_PASSWORDS + " ADD COLUMN " + COLUMN_USER_PASSWORD + " TEXT");        }
    }


    public long addPassword(long userId, String password, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_DESCRIPTION, description); // Добавляем описание пароля
        long result = db.insert(TABLE_PASSWORDS, null, values);
        db.close();
        return result;
    }
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        db.close();
        return result;
    }
    public long addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }




    public Cursor getUserPasswords(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.query(TABLE_PASSWORDS, null, selection, selectionArgs, null, null, null);
    }

    public boolean deletePassword(long userId, String password, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_ID + " = ? AND " + COLUMN_USER_PASSWORD + " = ? AND " + COLUMN_DESCRIPTION + " = ?";
        String[] selectionArgs = {String.valueOf(userId), password, description};
        int deletedRows = db.delete(TABLE_PASSWORDS, selection, selectionArgs);
        db.close();
        return deletedRows > 0;
    }
}
