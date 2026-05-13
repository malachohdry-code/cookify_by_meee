package com.example.cookify_by_me.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cookify_by_me.models.User;

public class UserDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.U_COL_FIRST_NAME, user.getFirstName());
        values.put(DBHelper.U_COL_LAST_NAME, user.getLastName());
        values.put(DBHelper.U_COL_FULL_NAME, user.getName());
        values.put(DBHelper.U_COL_EMAIL, user.getEmail());
        values.put(DBHelper.U_COL_PASSWORD, user.getPassword());
        return database.insert(DBHelper.USER_TABLE, null, values);
    }

    public User checkUser(String email, String password) {
        String selection = DBHelper.U_COL_EMAIL + " = ?" + " AND " + DBHelper.U_COL_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = database.query(DBHelper.USER_TABLE, null, selection, selectionArgs, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUserById(int id) {
        String selection = DBHelper.U_COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = database.query(DBHelper.USER_TABLE, null, selection, selectionArgs, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    private User cursorToUser(Cursor cursor) {
        return new User(
            cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.U_COL_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.U_COL_FIRST_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.U_COL_LAST_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.U_COL_FULL_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.U_COL_EMAIL)),
            cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.U_COL_PASSWORD))
        );
    }
}
