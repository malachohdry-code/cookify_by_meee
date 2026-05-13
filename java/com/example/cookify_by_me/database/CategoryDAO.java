package com.example.cookify_by_me.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cookify_by_me.models.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public CategoryDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CATEGORY_NAME, category.getName());
        values.put(DBHelper.COLUMN_CATEGORY_IMAGE, category.getImageUrl());
        return database.insert(DBHelper.TABLE_CATEGORIES, null, values);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_CATEGORIES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY_IMAGE))
                );
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }
}
