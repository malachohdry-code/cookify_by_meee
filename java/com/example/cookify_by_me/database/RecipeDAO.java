package com.example.cookify_by_me.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cookify_by_me.models.Recipe;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public RecipeDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addRecipe(Recipe recipe, int userId) {
        ContentValues values = recipeToContentValues(recipe);
        values.put(DBHelper.R_COL_USER_ID, userId);
        return database.insert(DBHelper.RECIPE_TABLE, null, values);
    }

    public int updateRecipe(Recipe recipe) {
        ContentValues values = recipeToContentValues(recipe);
        return database.update(DBHelper.RECIPE_TABLE, values, 
                DBHelper.R_COL_1 + " = ?", new String[]{String.valueOf(recipe.getId())});
    }

    private ContentValues recipeToContentValues(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.R_COL_2, recipe.getName());
        values.put(DBHelper.R_COL_3, recipe.getCategory());
        values.put(DBHelper.R_COL_4, recipe.getImage());
        values.put(DBHelper.R_COL_5, recipe.getIngredients());
        values.put(DBHelper.R_COL_6, recipe.getSteps());
        values.put(DBHelper.R_COL_7, recipe.getTime());
        values.put(DBHelper.R_COL_8, recipe.getDifficulty());
        values.put(DBHelper.R_COL_9, recipe.getDescription());
        values.put(DBHelper.R_COL_10, recipe.getRating());
        values.put(DBHelper.R_COL_11, recipe.getKcal());
        values.put(DBHelper.R_COL_12, recipe.getProtein());
        values.put(DBHelper.R_COL_13, recipe.getChefsTip());
        values.put(DBHelper.R_COL_TAGS, recipe.getTags());
        return values;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.RECIPE_TABLE, null, null, null, null, null, DBHelper.R_COL_1 + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                recipes.add(cursorToRecipe(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return recipes;
    }

    public Recipe getRecipeById(int id) {
        String selection = DBHelper.R_COL_1 + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = database.query(DBHelper.RECIPE_TABLE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Recipe recipe = cursorToRecipe(cursor);
            cursor.close();
            return recipe;
        }
        return null;
    }

    public List<Recipe> getRecipesByCategory(String category) {
        List<Recipe> recipes = new ArrayList<>();
        String selection = DBHelper.R_COL_3 + " = ?";
        String[] selectionArgs = {category};
        Cursor cursor = database.query(DBHelper.RECIPE_TABLE, null, selection, selectionArgs, null, null, DBHelper.R_COL_1 + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                recipes.add(cursorToRecipe(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return recipes;
    }

    public List<Recipe> getUserPosts(int userId) {
        List<Recipe> recipes = new ArrayList<>();
        String selection = DBHelper.R_COL_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = database.query(DBHelper.RECIPE_TABLE, null, selection, selectionArgs, null, null, DBHelper.R_COL_1 + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                recipes.add(cursorToRecipe(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return recipes;
    }

    // Favorites Logic
    public boolean isFavorite(int userId, int recipeId) {
        String selection = DBHelper.F_COL_2 + " = ? AND " + DBHelper.F_COL_3 + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(recipeId)};
        Cursor cursor = database.query(DBHelper.FAV_TABLE, null, selection, selectionArgs, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public void toggleFavorite(int userId, int recipeId) {
        if (isFavorite(userId, recipeId)) {
            database.delete(DBHelper.FAV_TABLE, 
                DBHelper.F_COL_2 + " = ? AND " + DBHelper.F_COL_3 + " = ?", 
                new String[]{String.valueOf(userId), String.valueOf(recipeId)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.F_COL_2, userId);
            cv.put(DBHelper.F_COL_3, recipeId);
            database.insert(DBHelper.FAV_TABLE, null, cv);
        }
    }

    public void addToRecentlyViewed(int userId, int recipeId) {
        database.delete(DBHelper.TABLE_RECENTLY_VIEWED, 
            DBHelper.COL_RV_USER_ID + " = ? AND " + DBHelper.COL_RV_RECIPE_ID + " = ?", 
            new String[]{String.valueOf(userId), String.valueOf(recipeId)});
        
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_RV_USER_ID, userId);
        values.put(DBHelper.COL_RV_RECIPE_ID, recipeId);
        database.insert(DBHelper.TABLE_RECENTLY_VIEWED, null, values);
        
        database.execSQL("DELETE FROM " + DBHelper.TABLE_RECENTLY_VIEWED + " WHERE " + DBHelper.COL_RV_ID + " NOT IN " +
            "(SELECT " + DBHelper.COL_RV_ID + " FROM " + DBHelper.TABLE_RECENTLY_VIEWED + 
            " WHERE " + DBHelper.COL_RV_USER_ID + " = " + userId + 
            " ORDER BY " + DBHelper.COL_RV_TIMESTAMP + " DESC LIMIT 10)");
    }

    public List<Recipe> getRecentlyViewed(int userId) {
        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT r.* FROM " + DBHelper.RECIPE_TABLE + " r " +
                "INNER JOIN " + DBHelper.TABLE_RECENTLY_VIEWED + " rv ON r." + DBHelper.R_COL_1 + " = rv." + DBHelper.COL_RV_RECIPE_ID +
                " WHERE rv." + DBHelper.COL_RV_USER_ID + " = ?" +
                " ORDER BY rv." + DBHelper.COL_RV_TIMESTAMP + " DESC";
        
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                recipes.add(cursorToRecipe(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return recipes;
    }

    public List<Recipe> getFavoriteRecipes(int userId) {
        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT r.* FROM " + DBHelper.RECIPE_TABLE + " r " +
                "INNER JOIN " + DBHelper.FAV_TABLE + " f ON r." + DBHelper.R_COL_1 + " = f." + DBHelper.F_COL_3 +
                " WHERE f." + DBHelper.F_COL_2 + " = ?" +
                " ORDER BY f." + DBHelper.F_COL_1 + " DESC";
        
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                recipes.add(cursorToRecipe(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return recipes;
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        return new Recipe(
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.R_COL_1)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_2)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_3)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_4)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_5)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_6)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_7)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_8)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_9)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_10)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_11)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_12)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_13)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_COL_TAGS))
        );
    }
}
