package com.example.cookify_by_me.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Cookify.db";
    public static final int DB_VERSION = 38; // Incremented to refresh with new image links

    // USERS TABLE
    public static final String USER_TABLE = "users";
    public static final String U_COL_ID = "id";
    public static final String U_COL_FIRST_NAME = "first_name";
    public static final String U_COL_LAST_NAME = "last_name";
    public static final String U_COL_FULL_NAME = "full_name";
    public static final String U_COL_EMAIL = "email";
    public static final String U_COL_PASSWORD = "password";

    // RECIPES TABLE
    public static final String RECIPE_TABLE = "recipes";
    public static final String R_COL_1 = "id";
    public static final String R_COL_2 = "name";
    public static final String R_COL_3 = "category";
    public static final String R_COL_4 = "image";
    public static final String R_COL_5 = "ingredients";
    public static final String R_COL_6 = "steps";
    public static final String R_COL_7 = "time";
    public static final String R_COL_8 = "difficulty";
    public static final String R_COL_9 = "description";
    public static final String R_COL_10 = "rating";
    public static final String R_COL_11 = "kcal";
    public static final String R_COL_12 = "protein";
    public static final String R_COL_13 = "chefs_tip";
    public static final String R_COL_TAGS = "tags";
    public static final String R_COL_USER_ID = "user_id";

    // FAVORITES TABLE
    public static final String FAV_TABLE = "favorites";
    public static final String F_COL_1 = "id";
    public static final String F_COL_2 = "user_id";
    public static final String F_COL_3 = "recipe_id";

    // RECENTLY VIEWED TABLE
    public static final String TABLE_RECENTLY_VIEWED = "recently_viewed";
    public static final String COL_RV_ID = "id";
    public static final String COL_RV_USER_ID = "user_id";
    public static final String COL_RV_RECIPE_ID = "recipe_id";
    public static final String COL_RV_TIMESTAMP = "viewed_at";

    // CATEGORIES TABLE
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_IMAGE = "image";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_TABLE + "(" +
                U_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                U_COL_FIRST_NAME + " TEXT," +
                U_COL_LAST_NAME + " TEXT," +
                U_COL_FULL_NAME + " TEXT," +
                U_COL_EMAIL + " TEXT UNIQUE," +
                U_COL_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + RECIPE_TABLE + "(" +
                R_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                R_COL_2 + " TEXT," +
                R_COL_3 + " TEXT," +
                R_COL_4 + " TEXT," +
                R_COL_5 + " TEXT," +
                R_COL_6 + " TEXT," +
                R_COL_7 + " TEXT," +
                R_COL_8 + " TEXT," +
                R_COL_9 + " TEXT," +
                R_COL_10 + " TEXT," +
                R_COL_11 + " TEXT," +
                R_COL_12 + " TEXT," +
                R_COL_13 + " TEXT," +
                R_COL_TAGS + " TEXT," +
                R_COL_USER_ID + " INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE " + FAV_TABLE + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "recipe_id INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_RECENTLY_VIEWED + "(" +
                COL_RV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_RV_USER_ID + " INTEGER," +
                COL_RV_RECIPE_ID + " INTEGER," +
                COL_RV_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CATEGORY_NAME + " TEXT," +
                COLUMN_CATEGORY_IMAGE + " TEXT)");
        
        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        String[] cats = {"Desi Food", "Italian", "Chinese", "American", "Thai", "Desserts"};
        for (String cat : cats) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_CATEGORY_NAME, cat);
            cv.put(COLUMN_CATEGORY_IMAGE, "");
            db.insert(TABLE_CATEGORIES, null, cv);
        }

        // --- Desi Food ---
        insertRecipe(db, "Royal Mutton Biryani", "Desi Food", "mutton_biryani", "Mutton, Basmati Rice, Yogurt, Onions, Ghee, Saffron", "Marinate mutton. Parboil rice. Layer and Dum.", "90 mins", "Hard", "Fragrant layered meat and rice.", "4.9 (245)", "850", "52g", "Use heavy bottom pot.", "Spicy, Traditional");
        insertRecipe(db, "Butter Chicken", "Desi Food", "butter_chicken", "Chicken, Tomato Puree, Butter, Cream, Ginger-Garlic Paste", "Grill chicken. Simmer in tomato gravy with cream.", "45 mins", "Medium", "Creamy and rich tomato-based chicken.", "4.8 (1.2k)", "650", "40g", "Don't overcook the cream.", "Creamy, Popular");
        insertRecipe(db, "Palak Paneer", "Desi Food", "palak_paneer", "Spinach, Paneer, Green Chillies, Spices", "Blanch spinach. Sauté paneer. Blend spinach and mix.", "30 mins", "Easy", "Cottage cheese in spinach gravy.", "4.6 (890)", "320", "15g", "Blanch spinach quickly to keep it green.", "Healthy, Vegetarian");
        insertRecipe(db, "Chicken Nihari", "Desi Food", "nihari", "Chicken, Wheat Flour, Nihari Masala, Oil", "Slow cook chicken with spices and flour thickening.", "120 mins", "Hard", "Slow-cooked spicy chicken stew.", "4.7 (500)", "450", "35g", "Garnish with ginger and lemon.", "Spicy, Stew");
        insertRecipe(db, "Daal Makhani", "Desi Food", "daal_makhani", "Black Lentils, Kidney Beans, Butter, Cream", "Slow cook lentils overnight. Temper with spices and cream.", "180 mins", "Hard", "Creamy black lentils cooked slowly.", "4.9 (2k)", "380", "18g", "Cook on low flame for best texture.", "Vegetarian, Rich");
        insertRecipe(db, "Seekh Kebab", "Desi Food", "seekh_kebab", "Minced Meat, Onions, Green Chillies, Kebab Masala", "Mix meat with spices. Skewer and grill.", "30 mins", "Medium", "Spicy grilled minced meat skewers.", "4.7 (750)", "280", "25g", "Use fatty meat for juicy kebabs.", "Grilled, BBQ");
        insertRecipe(db, "Beef Haleem", "Desi Food", "beef_haleem", "Beef, Wheat, Barley, Lentils, Spices", "Slow cook meat and grains until mashed and thick.", "240 mins", "Hard", "Rich and thick meat and grain porridge.", "4.8 (600)", "550", "45g", "Garnish with fried onions.", "Traditional, Heavy");

        // --- Italian ---
        insertRecipe(db, "Margherita Pizza", "Italian", "margherita_pizza", "Pizza Dough, Tomato Sauce, Mozzarella, Basil", "Spread sauce. Add cheese. Bake at high heat. Top with basil.", "15 mins", "Medium", "Classic Italian pizza.", "4.8 (3k)", "800", "30g", "Use fresh mozzarella.", "Pizza, Classic");
        insertRecipe(db, "Spaghetti Carbonara", "Italian", "spaghetti_carbonara", "Spaghetti, Eggs, Guanciale, Pecorino Romano", "Cook pasta. Fry guanciale. Mix eggs and cheese. Combine.", "20 mins", "Medium", "Creamy egg and cheese based pasta.", "4.7 (1.5k)", "600", "25g", "Don't scramble the eggs.", "Pasta, Classic");
        insertRecipe(db, "Fettuccine Alfredo", "Italian", "fettuccine_alfredo", "Fettuccine, Butter, Parmesan Cheese", "Cook pasta. Melt butter. Add cheese and pasta water.", "15 mins", "Easy", "Simple and buttery parmesan pasta.", "4.5 (2k)", "700", "20g", "Use authentic Parmesan.", "Pasta, Simple");
        insertRecipe(db, "Lasagna Classica", "Italian", "lasagna_classica", "Lasagna Sheets, Ragù, Béchamel, Parmesan", "Layer sheets, meat sauce, and white sauce. Bake.", "60 mins", "Hard", "Layered baked pasta dish.", "4.9 (1k)", "950", "45g", "Let it rest before cutting.", "Pasta, Baked");
        insertRecipe(db, "Mushroom Risotto", "Italian", "mushroom_risotto", "Arborio Rice, Mushrooms, Broth, Wine", "Toast rice. Gradually add broth while stirring.", "40 mins", "Hard", "Creamy rice with earthy mushrooms.", "4.6 (600)", "450", "12g", "Stir constantly for creaminess.", "Rice, Gourmet");
        insertRecipe(db, "Penne Arrabbiata", "Italian", "penne_arrabbiata", "Penne, Garlic, Tomatoes, Chili Flakes", "Sauté garlic and chili. Add tomatoes. Mix with pasta.", "20 mins", "Easy", "Spicy tomato pasta.", "4.5 (1.2k)", "400", "12g", "Add more chili for extra heat.", "Pasta, Spicy");
        insertRecipe(db, "Pesto Genovese", "Italian", "pesto_genovese", "Basil, Pine Nuts, Garlic, Parmesan, Olive Oil", "Blend all ingredients until smooth. Toss with pasta.", "10 mins", "Easy", "Fresh and nutty basil sauce pasta.", "4.8 (800)", "500", "10g", "Don't heat the pesto.", "Pasta, Fresh");

        // --- Chinese ---
        insertRecipe(db, "Kung Pao Chicken", "Chinese", "kung_pao_chicken", "Chicken, Peanuts, Dried Chillies, Soy Sauce", "Stir fry chicken with chillies and peanuts.", "25 mins", "Medium", "Spicy stir-fry with chicken and peanuts.", "4.7 (900)", "450", "30g", "Use Szechuan peppercorns.", "Spicy, Stir-fry");
        insertRecipe(db, "Beef and Broccoli", "Chinese", "beef_broccoli", "Beef Strips, Broccoli, Oyster Sauce, Soy Sauce", "Sear beef. Steam broccoli. Toss in sauce.", "20 mins", "Easy", "Classic savory beef and broccoli.", "4.6 (1k)", "380", "35g", "Slice beef against the grain.", "Healthy, Fast");
        insertRecipe(db, "Sweet and Sour Shrimp", "Chinese", "sweet_sour_shrimp", "Shrimp, Pineapple, Peppers, Vinegar, Sugar", "Fry shrimp. Toss with sweet and sour sauce.", "25 mins", "Medium", "Tangy and sweet shrimp dish.", "4.5 (700)", "350", "25g", "Use fresh pineapple.", "Seafood, Tangy");
        insertRecipe(db, "Chicken Chow Mein", "Chinese", "chow_mein", "Noodles, Chicken, Cabbage, Carrots, Soy Sauce", "Stir fry noodles with meat and vegetables.", "20 mins", "Easy", "Popular stir-fried noodle dish.", "4.7 (2k)", "500", "25g", "High heat for wok hei.", "Noodles, Fast");
        insertRecipe(db, "Vegetable Spring Rolls", "Chinese", "spring_rolls", "Wrappers, Cabbage, Carrots, Mushrooms", "Fill wrappers with veggies. Roll and deep fry.", "30 mins", "Medium", "Crispy fried vegetable rolls.", "4.4 (1.5k)", "200", "5g", "Keep oil temperature steady.", "Appetizer, Crispy");
        insertRecipe(db, "Mapo Tofu", "Chinese", "mapo_tofu", "Tofu, Minced Pork, Chili Bean Paste", "Sauté pork. Add paste and tofu. Simmer.", "25 mins", "Hard", "Spicy and numbing Szechuan tofu.", "4.8 (400)", "320", "20g", "Use soft silken tofu.", "Spicy, Szechuan");
        insertRecipe(db, "Hot and Sour Soup", "Chinese", "hot_sour_soup", "Mushrooms, Bamboo Shoots, Vinegar, Pepper", "Boil broth with veggies. Add vinegar and eggs.", "30 mins", "Medium", "Classic tangy and spicy soup.", "4.6 (600)", "150", "8g", "Balance vinegar and pepper.", "Soup, Spicy");

        // --- American ---
        insertRecipe(db, "Classic Beef Burger", "American", "beef_burger", "Ground Beef, Buns, Lettuce, Tomato, Cheese", "Grill patty. Assemble with toppings on toasted buns.", "20 mins", "Easy", "Juicy homemade beef burger.", "4.8 (3.5k)", "600", "35g", "Don't press the patty.", "Burger, Classic");
        insertRecipe(db, "BBQ Chicken Wings", "American", "bbq_wings", "Chicken Wings, BBQ Sauce, Spices", "Bake or fry wings. Toss in BBQ sauce.", "40 mins", "Easy", "Tangy and smoky chicken wings.", "4.7 (2.5k)", "450", "30g", "Bake for healthier wings.", "Wings, BBQ");
        insertRecipe(db, "Mac and Cheese", "American", "mac_cheese", "Macaroni, Cheddar Cheese, Milk, Butter", "Cook pasta. Make cheese sauce. Mix and bake.", "30 mins", "Easy", "Comforting creamy cheesy pasta.", "4.6 (4k)", "550", "20g", "Use multiple types of cheese.", "Comfort, Cheesy");
        insertRecipe(db, "Pepperoni Pizza", "American", "pepperoni_pizza", "Dough, Sauce, Mozzarella, Pepperoni", "Top dough with sauce, cheese, and pepperoni. Bake.", "20 mins", "Medium", "Everyone's favorite pizza.", "4.9 (5k)", "900", "35g", "Use high-quality pepperoni.", "Pizza, Popular");
        insertRecipe(db, "Fried Chicken", "American", "fried_chicken", "Chicken, Flour, Buttermilk, Spices", "Marinate in buttermilk. Coat in flour. Deep fry.", "50 mins", "Hard", "Crispy and juicy fried chicken.", "4.8 (2.2k)", "700", "40g", "Fry in batches.", "Crispy, Classic");
        insertRecipe(db, "Meatloaf", "American", "meatloaf", "Ground Beef, Breadcrumbs, Ketchup, Onions", "Mix ingredients. Shape into loaf. Bake with glaze.", "70 mins", "Medium", "Classic family dinner meatloaf.", "4.5 (1.2k)", "400", "28g", "Don't overmix the meat.", "Dinner, Classic");
        insertRecipe(db, "Hot Dogs", "American", "hot_dogs", "Hot Dog Sausages, Buns, Mustard, Ketchup", "Grill sausages. Place in buns. Add condiments.", "10 mins", "Easy", "Quick and easy American snack.", "4.3 (1k)", "300", "12g", "Steam the buns.", "Snack, Fast");

        // --- Thai ---
        insertRecipe(db, "Pad Thai", "Thai", "pad_thai", "Rice Noodles, Shrimp, Tofu, Peanuts, Bean Sprouts", "Stir fry noodles with sauce, meat, and nuts.", "25 mins", "Medium", "Classic Thai stir-fried noodles.", "4.9 (2k)", "480", "20g", "Use tamarind paste for sauce.", "Noodles, Iconic");
        insertRecipe(db, "Green Chicken Curry", "Thai", "green_curry", "Chicken, Green Curry Paste, Coconut Milk", "Sauté paste. Add coconut milk and chicken. Simmer.", "30 mins", "Medium", "Fragrant and spicy coconut curry.", "4.8 (1.5k)", "550", "25g", "Don't boil coconut milk too hard.", "Curry, Spicy");
        insertRecipe(db, "Tom Yum Goong", "Thai", "tom_yum", "Shrimp, Lemongrass, Galangal, Lime", "Boil aromatics. Add shrimp and lime juice.", "20 mins", "Medium", "Spicy and sour Thai shrimp soup.", "4.7 (1.2k)", "200", "18g", "Use fresh lemongrass.", "Soup, Spicy");
        insertRecipe(db, "Red Curry Duck", "Thai", "red_curry_duck", "Roasted Duck, Red Curry Paste, Pineapple", "Simmer duck in red curry with fruit.", "45 mins", "Hard", "Rich and fruity red curry.", "4.8 (400)", "650", "30g", "Add lychees for sweetness.", "Curry, Gourmet");
        insertRecipe(db, "Massaman Beef Curry", "Thai", "massaman_curry", "Beef, Potatoes, Peanuts, Massaman Paste", "Slow cook beef in nutty coconut curry.", "90 mins", "Hard", "Mild and nutty rich curry.", "4.9 (800)", "700", "35g", "Use tender beef cuts.", "Curry, Nutty");
        insertRecipe(db, "Pad Kra Pao", "Thai", "pad_kra_pao", "Minced Chicken, Holy Basil, Chillies", "Stir fry chicken with chillies and basil.", "15 mins", "Easy", "Spicy Thai basil stir-fry.", "4.7 (1k)", "350", "25g", "Top with a fried egg.", "Fast, Spicy");
        insertRecipe(db, "Panang Curry", "Thai", "panang_curry", "Beef, Panang Paste, Coconut Cream", "Sauté paste. Add meat and thick cream.", "30 mins", "Medium", "Thick and salty Thai curry.", "4.6 (500)", "600", "30g", "Finish with lime leaves.", "Curry, Rich");

        // --- Desserts ---
        insertRecipe(db, "Molten Chocolate Lava Cake", "Desserts", "lava_cake", "Dark Chocolate, Butter, Eggs, Sugar, Flour", "Melt chocolate. Whisk eggs. Bake for 11 mins.", "20 mins", "Medium", "Cake with warm flowing center.", "4.9 (812)", "340", "6g", "Don't overbake.", "Sweet, Chocolate");
        insertRecipe(db, "New York Cheesecake", "Desserts", "cheesecake", "Cream Cheese, Biscuits, Butter, Eggs", "Press crust. Beat cheese and eggs. Bake in water bath.", "120 mins", "Hard", "Rich and velvety cheesecake.", "4.8 (560)", "480", "11g", "Chill overnight.", "Sweet, Creamy");
        insertRecipe(db, "Traditional Tiramisu", "Desserts", "tiramisu", "Mascarpone, Ladyfingers, Espresso, Cocoa", "Layer espresso-soaked biscuits with cream.", "40 mins", "Medium", "Coffee-flavored Italian dessert.", "4.9 (445)", "320", "8g", "Use strong coffee.", "Coffee, Italian");
        insertRecipe(db, "Apple Pie", "Desserts", "apple_pie", "Apples, Pastry Crust, Cinnamon, Sugar", "Fill crust with sliced apples and spices. Bake.", "60 mins", "Medium", "Classic American fruit pie.", "4.7 (1.8k)", "400", "4g", "Serve with vanilla ice cream.", "Sweet, Classic");
        insertRecipe(db, "Chocolate Brownies", "Desserts", "brownies", "Chocolate, Butter, Sugar, Cocoa, Flour", "Mix and bake in a square pan.", "35 mins", "Easy", "Fudgy and chocolatey squares.", "4.8 (3k)", "300", "4g", "Add walnuts for crunch.", "Sweet, Chocolate");
        insertRecipe(db, "Gulab Jamun", "Desserts", "gulab_jamun", "Milk Solids, Flour, Sugar Syrup, Cardamom", "Make small balls. Deep fry. Soak in syrup.", "45 mins", "Hard", "Soft milk-based balls in syrup.", "4.9 (1.5k)", "250", "5g", "Serve warm.", "Traditional, Sweet");
        insertRecipe(db, "Baklava", "Desserts", "baklava", "Phyllo Dough, Walnuts, Honey Syrup", "Layer dough and nuts. Bake. Pour syrup over.", "90 mins", "Hard", "Crunchy layered pastry with nuts.", "4.8 (900)", "450", "6g", "Keep phyllo covered so it doesn't dry.", "Sweet, Nutty");
    }

    private void insertRecipe(SQLiteDatabase db, String name, String cat, String image, String ing, String steps, String time, String diff, String desc, String rate, String kcal, String protein, String tip, String tags) {
        ContentValues cv = new ContentValues();
        cv.put(R_COL_2, name);
        cv.put(R_COL_3, cat);
        cv.put(R_COL_4, image);
        cv.put(R_COL_5, ing);
        cv.put(R_COL_6, steps);
        cv.put(R_COL_7, time);
        cv.put(R_COL_8, diff);
        cv.put(R_COL_9, desc);
        cv.put(R_COL_10, rate);
        cv.put(R_COL_11, kcal);
        cv.put(R_COL_12, protein);
        cv.put(R_COL_13, tip);
        cv.put(R_COL_TAGS, tags);
        cv.put(R_COL_USER_ID, 0);
        db.insert(RECIPE_TABLE, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RECIPE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FAV_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENTLY_VIEWED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    public boolean insertUser(String firstName, String lastName, String fullName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COL_FIRST_NAME, firstName);
        cv.put(U_COL_LAST_NAME, lastName);
        cv.put(U_COL_FULL_NAME, fullName);
        cv.put(U_COL_EMAIL, email);
        cv.put(U_COL_PASSWORD, password);
        long result = db.insert(USER_TABLE, null, cv);
        return result != -1;
    }
}
